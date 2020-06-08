/**
 * pages-fencing的控制器
 *
 * @author lijiajie
 */
indexApp
    .controller(
        'pagesFencingCtrl',
        function($scope, $http, $timeout,$interval) {
            //设置下拉框显示为暂时永久不显示，这个界面只能由distance跳转过来。
            $scope.isCookieId=false;

            /**
             * @description 获取cookie专用函数
             * @param name
             * @returns {string}
             */
            function getCookie(name) {
                var arr=document.cookie.split('; ');
                console.log("arr", arr);
                for(var i = 0 ; i < arr.length; i ++){
                    var arr2=arr[i].split('=');
                    if(arr2[0]==name){
                        return arr2[1];
                    }
                }
                return '';
            }


            /**
             * @description 获取所有目标的信息这个函数暂时弃用
             * 由于这个页面变成cookie传参数调用具体的目标并查看详情
             * 所以舍弃了下拉框，以及获取所有的目标信息的接口。
             */
            $scope.userList = undefined;
            $scope.userNameList = undefined;

            //核心参数 目标的Id
            $scope.userId = undefined;
            //核心参数 任务的Id
            $scope.taskId = undefined;
            $scope.userName = undefined;
            $scope.userDescription = undefined;
            //弃用
            $scope.userIndexId = undefined;
            //弃用
            $scope.selectedUserName = undefined;
            let userIdList=undefined;
            //弃用函数
            function getAllUser() {
                $http({
                    url : 'controlloc/getalluser',
                    method : 'GET'
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data["userIdList"]);
                    $scope.userList = resp.data["userList"];
                    $scope.userNameList = resp.data["userNameList"];
                    userIdList = resp.data["userIdList"];
                    $http({
                        url : 'controlloc/gettargetloc',
                        method : 'POST',
                        data:{
                            "userId":"0"
                        }
                    }).then(function(resp, status) {
                        $scope.status = status;
                        console.log(resp.data);
                    }, function(resp, status) {
                        $scope.resp = resp;
                        $scope.status = status;
                    });
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            }


            //获取目标id
            $scope.cookieTargetId=getCookie("targetId");
            console.log($scope.cookieTargetId);
            //判断是否获取到目标的id如果获取到则执行展示的相关逻辑
            if($scope.cookieTargetId!=""){
                console.log($scope.userIdList);
                $scope.isCookieId=false;
                $scope.userId=$scope.cookieTargetId;
                $scope.taskId = getCookie("taskId");

                //获取传参目标的详细信息
                $http({
                    url : 'controlloc/getauser',
                    method : 'POST',
                    data:{
                        "userId":$scope.userId
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    let userIfo = resp.data["userInfo"];
                    $scope.userName = userIfo["userName"];
                    $scope.userDescription = userIfo["userDescription"];
                    $scope.userIndexId = userIfo["id"];
                    /**
                     * @description 嵌套异步请求，获取用户信息成功后
                     * 向后端传目标id与任务id两个参数，让后端新建或开启
                     * 针对这个用户id与任务id的rabbitmq的stomp向前端传轨迹信息
                     */
                    $http({
                        url : 'controlloc/gettargetloc',
                        method : 'POST',
                        data:{
                            "userId":$scope.userId,
                            "taskId":$scope.taskId
                        }
                    }).then(function(resp, status) {
                        $scope.status = status;
                        console.log(resp.data);

                        //启动传坐标信息函数。
                        startStomp($scope.taskId, $scope.userId);
                    }, function(resp, status) {
                        $scope.resp = resp;
                        $scope.status = status;
                    });
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
                //$scope.$apply();
            }else {
                //getAllUser();
                swal.fire({
                    title:"未获取到taskId请从任务距离界面进入",
                    timer:3000
                })
            }
            /**
             * @description 建立一个链表队列
             */
            function LinkedQueue() {
                let Node = function (ele) {
                    this.ele = ele;
                    this.next = null;
                };

                let length = 0,
                    front, //队首指针
                    rear; //队尾指针
                this.push = function (ele) {
                    let node = new Node(ele),
                        temp;

                    if (length == 0) {
                        front = node;
                    } else {
                        temp = rear;
                        temp.next = node;
                    }
                    rear = node;
                    length++;
                    return true;
                };
                this.pop = function () {
                    let temp = front;
                    front = front.next
                    length--;
                    temp.next = null;
                    return temp;
                };

                this.size = function () {
                    return length;
                };
                this.getFront = function () {
                    return front;
                    // 有没有什么思路只获取队列的头结点,而不是获取整个队列
                };
                this.getRear = function () {
                    return rear;
                };
                this.toString = function () {
                    let string = '',
                        temp = front;
                    while (temp) {
                        string += temp.ele + ' ';
                        temp = temp.next;
                    }
                    return string;
                };
                this.clear = function () {
                    front = null;
                    rear = null;
                    length = 0;
                    return true;
                }
            }


            /**
             * @description 这里的map主要是电子围栏的相关设置，主要是选点与画出围栏平面图
             * 注意map变量下一个图要避开。
             * @type {Array}
             */
            $scope.fencingPointList = [];
            $scope.initPoint=undefined;
            $scope.stopMapFlage = 0;
            var map = new BMap.Map("allmap");
            map.centerAndZoom(new BMap.Point(118.48038400281214,36.688057752401846), 16);
            map.enableScrollWheelZoom(true);
            var locList=[];
            //单击获取点击的经纬度
            $scope.firstFlag = 0;
            var geoc = new BMap.Geocoder();
            map.addEventListener("click",function(e){
                var tempList=[];
                tempList.push(e.point.lng);
                tempList.push(e.point.lat);
                locList.push(tempList);
                var pt = e.point;
                geoc.getLocation(pt, function(rs){
                    var addComp = rs.addressComponents;
                    var displayinfo =$scope.displayInfo = addComp.province + ", " + addComp.city + ", " + addComp.district + ", " + addComp.street + ", " + addComp.streetNumber;
                    console.log("$scope.displayInfo",$scope.displayInfo);
                    $scope.confirmLocation(e, displayinfo);
                });
                var markerPoint = new BMap.Marker(new BMap.Point(e.point.lng, e.point.lat)); // 创建点
                markerPoint.name='markPoint';
                map.addOverlay(markerPoint);
            });

            /**
             * @description 设置电子栅栏的定点坐标,展示选中的点的信息
             * @param e
             * @param locationInfo
             */
            $scope.confirmLocation = function (e, locationInfo) {
                var tempMap = {
                    "lng":e.point.lng,
                    "lat":e.point.lat
                };
                swal.fire({
                    title:"选中点信息："+locationInfo,
                    timer:2000
                });
                $scope.fencingPointList.push(tempMap);
                console.log("locationInfo",locationInfo);
            };

            /**
             * @description 完成围栏绘制并将参数信息回传后端，后端将围栏信息存缓存与数据库
             */
            $scope.finshFencing=function () {
                let pointList = [];
                for (var i = 0; i < $scope.fencingPointList.length; i++) {
                    pointList.push(new BMap.Point($scope.fencingPointList[i]["lng"], $scope.fencingPointList[i]["lat"]));
                }
                console.log(pointList);
                let polygon = new BMap.Polygon(pointList, {strokeColor:"blue", strokeWeight:2, strokeOpacity:0.5});  //创建多边形
                polygon.name="fencingLine";
                map.addOverlay(polygon);

                //回传围栏边界点。注意传用户id与任务id，做到任务隔离。
                $http({
                    url : 'fencing/getfencing',
                    method : 'POST',
                    data : {
                        "fencingPoints":$scope.fencingPointList,
                        "taskId":$scope.taskId,
                        "userId":$scope.userId
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    swal.fire({
                        title:resp.data["state"],
                        timer:2000
                    })
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });

            };
            /**
             * @description 清空地图上画过的轨迹
             * 注意这里用的是定点清理，不是重置地图，这个方法挺好～
             */
            $scope.reSetFencing=function(){
                $scope.fencingPointList=[];
                let allOverlay = map.getOverlays();
                allOverlay.map(item => {
                    if(item.name === "fencingLine") {
                        map.removeOverlay(item)
                    }if(item.name === "markPoint") {
                        map.removeOverlay(item)
                    }
                })
                //map.clearOverlays();
            };

            /**
             * @description 定格地图，让地图不同步更新中心坐标。当然应该支持
             * 反选。
             */
            $scope.stopTheMap=function(){
                $scope.stopMapFlage = 1;
                swal.fire({
                    title:"定格地图",
                    timer:2000
                })
            };


            /**
             * @description 计算当前目标点与其他点的距离，这里没有支持区划围栏
             * 后期更新。
             * @type {Array}
             */
            $scope.distanceShow = [];
            $scope.calculateDistance=function(){
                $http({
                    url : 'distance/monitoring',
                    method : 'POST',
                    data : {
                        "userId":$scope.userId
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    $scope.distanceShow=[];
                    swal.fire({
                        title:resp.data["state"],
                        timer:2000
                    });
                    let distanceList=resp.data["distanceList"];
                    for(let i=0;i<distanceList.length;i++){
                        let allOverlay = map.getOverlays();
                        allOverlay.map(item => {
                            if(item.name === "marker"+i) {
                                map.removeOverlay(item)
                            }
                        })
                    }
                    for(let i=0;i<distanceList.length;i++){
                        let showMap = {
                            "index":i+1,
                            "distance":distanceList[i]["distance"],
                            "userId":distanceList[i]["calculatePoint"]["userId"],
                        };
                        $scope.distanceShow.push(showMap);
                        let tempMap = distanceList[i]["calculatePoint"];
                        let point = new BMap.Point(tempMap["lng"],tempMap["lat"]);
                        let marker = new BMap.Marker(point);  // 创建标注
                        //map.centerAndZoom(point, 17);
                        map.addOverlay(marker);              // 将标注添加到地图中
                        let label = new BMap.Label("距离:"+distanceList[i]["distance"],{offset:new BMap.Size(20,-10)});
                        label.setStyle({
                            color:"#4821ff"
                        });
                        marker.name="marker"+i;
                        marker.setLabel(label);
                        map.addOverlay(label);
                    }
                    showDistanceF();
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            };



            let currentLocation = new LinkedQueue();
            /**
             * @description 设置locationMap主要是展示实时坐标位置。
             */
            $scope.isStop = 0;
            $scope.currentLocList = [];
            let locationMap = new BMap.Map("locationMap");
            let point = new BMap.Point(116.404, 39.915);
            locationMap.centerAndZoom(point, 15);
            locationMap.enableScrollWheelZoom(true);
            let marker = new BMap.Marker(point);  // 创建标注
            locationMap.addOverlay(marker);               // 将标注添加到地图中
            marker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
            let BmapPointList = [];
            function updateLocationMap() {
                let locationStr = currentLocation.pop().ele;
                let locationList = locationStr.toString().split("-");
                console.log("locationStr",locationStr);
                console.log("locationList",locationList);
                let tempMap = {
                    "targetName":locationList[0],
                    "targetId":locationList[1],
                    "lng":locationList[2],
                    "lat":locationList[3]
                };
                console.log("parseFloat(locationList[2])",parseFloat(locationList[2]));
                let tempPoint = new BMap.Point(parseFloat(locationList[2]), parseFloat(locationList[3]));
                let tempPointMap = new BMap.Point(parseFloat(locationList[2]), parseFloat(locationList[3]));
                BmapPointList.push(new BMap.Point(parseFloat(locationList[2]), parseFloat(locationList[3])));
                locationMap.centerAndZoom(tempPoint, 18);
                if($scope.stopMapFlage === 0){
                    map.centerAndZoom(tempPoint, 17);
                }
                let allOverlay = locationMap.getOverlays();
                allOverlay.map(item => {
                    if(item.name === "newPoint") {
                        locationMap.removeOverlay(item)
                    }
                });
                let marker = new BMap.Marker(tempPoint);  // 创建标注
                marker.name='newPoint';
                locationMap.addOverlay(marker);               // 将标注添加到地图中
                marker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画

                let allOverlayMap = map.getOverlays();
                allOverlayMap.map(item => {
                    if(item.name === "newPoint") {
                        map.removeOverlay(item)
                    }
                });
                let markerMap = new BMap.Marker(tempPointMap);  // 创建标注
                markerMap.name='newPoint';
                map.addOverlay(markerMap);               // 将标注添加到地图中
                markerMap.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
                console.log("BmapPointList",BmapPointList);
                console.log("tempPoint",tempPoint);
                let polyline =new BMap.Polyline(BmapPointList
                    , {strokeColor:"blue", strokeWeight:2, strokeOpacity:0.5});
                locationMap.addOverlay(polyline);//增加折线
                let polyline1 =new BMap.Polyline(BmapPointList
                    , {strokeColor:"red", strokeWeight:2, strokeOpacity:0.5});
                map.addOverlay(polyline1);//增加折线
                $scope.currentLocList.push(tempMap);
                createTable();
            }


            $scope.checkIsInFencing=function () {
                $http({
                    url : 'fencing/isinfencing',
                    method : 'POST',
                    data : {
                        "userId":$scope.userId //先写死，最后配置多用户时选择或者cookie传值
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    swal.fire({
                        title:"是否在电子围栏中:"+resp.data["isFencing"],
                        timer:2000
                    })
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            };

            $scope.isactive1="nav-link active";
            $scope.isactive2="nav-link";
            $scope.isShowFencing=true;
            $scope.isShowDistance=false;
            $scope.showFencingTable=function () {
                $scope.isactive1="nav-link active";
                $scope.isactive2="nav-link";
                $scope.isShowFencing=true;
                $scope.isShowDistance=false;
            };
            $scope.showDistanceTable=function () {
                $scope.isactive1="nav-link";
                $scope.isactive2="nav-link active";
                $scope.isShowFencing=false;
                $scope.isShowDistance=true;
            };


            /**
             *@description 配置layui的表
             */
            function createTable() {
                //使用layui的表格

                let data = [];
                for (var i = $scope.currentLocList.length-1; i >= 0; i--) {
                    data.push($scope.currentLocList[i]);
                }
                console.log("$scope.currentLocList.reverse()",data);
                layui.use('table', function(){
                    var table = layui.table;
                    //第一个实例
                    table.render({
                        elem: '#location'
                        ,height: 500
                        ,data: data
                        ,page: true //开启分页
                        ,toolbar: 'default'
                        ,totalRow: false //开启合计行
                        ,cols: [[ //表头
                            {field: 'targetId', title: '目标学号', width:250, sort: true, fixed: 'left'}
                            ,{field: 'targetName', title: '目标名', width:200}
                            ,{field: 'lng', title: '经度', width: 250}
                            ,{field: 'lat', title: '纬度', width:250,fixed:'right'}
                        ]]
                    });
                });
            }

            /**
             * @description 坐标点距离表
             */
            function showDistanceF() {
                layui.use('table', function(){
                    var table = layui.table;
                    //第一个实例
                    table.render({
                        elem: '#distance'
                        ,height: 300
                        ,data:$scope.distanceShow
                        ,page: true //开启分页
                        ,toolbar: 'default'
                        ,totalRow: false //开启合计行
                        ,cols: [[ //表头
                            {field: 'index', title: 'ID', width:150, sort: true, fixed: 'left'}
                            ,{field: 'userId', title: '目标ID', width:220}
                            ,{field: 'distance', title: '距离', width: 250}
                        ]]
                    });
                });
            }

            /**
             * @description 核心功能，接受来自兔子的消息，利用了websocket技术。基于stomp协议
             * 获取指定的队列的坐标信息，这个队列由taskid与userId共同组成。
             */
            function startStomp(taskId,userId) {
                let url=undefined;
                if(taskId===0){
                    url = "/exchange/EXCHANGE_WCS_01/ROUTING_WCS_01";
                }else {
                    url = "/exchange/EXCHANGE_WCS_01_"+taskId+"_"+userId+"/ROUTING_WCS_01_"+taskId+"_"+userId;
                }
                var ws = new SockJS('http://localhost:15674/stomp');
                // 获得Stomp client对象
                var client = Stomp.over(ws);
                // SockJS does not support heart-beat: disable heart-beats
                client.heartbeat.outgoing = 0;
                client.heartbeat.incoming = 0;
                // 定义连接成功回调函数
                var on_connect = function(x) {
                    //data.body是接收到的数据
                    client.subscribe(url, function(data) {
                        var msg = data.body;
                        currentLocation.push(msg);
                        updateLocationMap();
                        console.log("msg",msg);
                        // $("#message").append("收到数据：" + msg);
                    });
                };

                // 定义错误时回调函数
                var on_error =  function() {
                    console.log('error');
                };

                // 连接RabbitMQ
                client.connect('root', 'root', on_connect, on_error, '/');
                console.log(">>>连接上ws://localhost:15674/stomp");
            }





            $scope.stopTranslate=function () {
                $http({
                    url : 'controlloc/enableloc',
                    method : 'POST',
                    data:{
                        "userId":$scope.userId,
                        "scheduledEnable":"false"
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            };
            $scope.startTranslate=function () {
                $http({
                    url : 'controlloc/enableloc',
                    method : 'POST',
                    data:{
                        "userId":$scope.userId,
                        "scheduledEnable":"true"
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            };

            function changeUser(selectIndex) {
                console.log("selectIndex",selectIndex);
                console.log("$scope.userList[selectIndex]",$scope.userList[selectIndex]);
                $scope.userId = $scope.userList[selectIndex]["userId"];
                $scope.userName = $scope.userList[selectIndex]["userName"];
                $scope.userDescription = $scope.userList[selectIndex]["userDescription"];
                $scope.userIndexId = $scope.userList[selectIndex]["id"];
                console.log("userName",$scope.userName);
                $http({
                    url : 'controlloc/gettargetloc',
                    method : 'POST',
                    data:{
                        "userId":$scope.userId
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
                $scope.$apply();
            };

            layui.use(['form', 'layedit', 'laydate'], function() {
                var form = layui.form;
                form.on('select(aihao)', function(data){
                    console.log(data.value); //得到被选中的值
                    let selectTargetName = data.value.split(":")[1];
                    let selectIndex = $scope.userNameList.indexOf(selectTargetName);
                    changeUser(selectIndex);
                    $scope.currentLocList=[];
                    BmapPointList=[];
                    map.clearOverlays();
                    locationMap.clearOverlays();
                });

            });



        });