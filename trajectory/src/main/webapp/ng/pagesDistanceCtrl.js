/**
 * pages-distance的控制器
 *
 * @author lijiajie
 */
indexApp
    .controller(
        'pagesDistanceCtrl',
        function($scope, $http, $timeout,$interval) {

            $scope.loadFencing=function () {

            };

            $scope.reSetFencing=function () {

            };

            $scope.cleanFencing=function () {

            };
            getAllFencing();

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

            $scope.taskId=getCookie("taskId");
            $scope.taskName=getCookie("taskName");
            console.log("taskId",$scope.taskId);
            console.log("taskName",$scope.taskName);

            $scope.fencingDescribeList=[];
            function getAllFencing(){
                $http({
                    url : 'fencing/getfilterfencing',
                    method : 'GET'
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    $scope.fencingInfoList = resp.data["fencingList"];
                    let fencingDescribe = resp.data["fencingDescribe"];
                    let fencingId = resp.data["fencingId"];
                    for(let i=0;i<fencingDescribe.length;i++){
                        $scope.fencingDescribeList.push(fencingId[i]+"_"+fencingDescribe[i]);
                    }
                    layui.use(['form'], function() {
                        let form = layui.form;
                        console.log("$scope.fencingDescribeList",$scope.fencingDescribeList);
                        form.on('select(aihao)', function(data){
                            console.log(data.value); //得到被选中的值
                            let selectFencingName = data.value.split(":")[1];
                            let fencingId=selectFencingName.split("_")[0];
                            $scope.showSwitch="关";
                            confirmFencing(fencingId);

                        });

                    });
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            }

            function confirmFencing(fencingId){
                $http({
                    url : 'fencing/startfilter',
                    method : 'POST',
                    data:{
                        "fencingId":fencingId,
                        "taskId":$scope.taskId
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    //getAllDistance();
                    $scope.intervalCalculate();
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            }


            /**
             * @description 加载全部的目标间距表格
             */
            function loadTable(){
                //使用layui的表格
                layui.use('table', function(){
                    var table = layui.table;

                    //第一个实例
                    table.render({
                        elem: '#demo'
                        ,height: 400
                        ,data: $scope.allPointDistance
                        ,title: '目标距离表'
                        ,page: true //开启分页
                        ,toolbar: 'default' //开启工具栏，此处显示默认图标，可以自定义模板，详见文档
                        ,totalRow: false //开启合计行
                        ,cols: [[ //表头
                            // {type: 'checkbox', fixed: 'left'}
                            {field: 'index', title: 'ID', width:200, sort: true, fixed: 'left'}
                            ,{field: 'userId', title: '目标点一', width:250}
                            ,{field: 'targetId', title: '目标点二', width: 250}
                            ,{field: 'distance', title: '距离', width:200, fixed:'right'}
                            ,{fixed: 'right',  align:'center', toolbar: '#barDemo'}
                        ]]
                    });
                    table.on('tool(test)', function(obj){ //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
                        var data = obj.data //获得当前行数据
                            ,layEvent = obj.event; //获得 lay-event 对应的值
                        if(layEvent === 'detail'){
                            console.log("data",data);
                            layer.open({
                                type: 1
                                ,title: false //不显示标题栏
                                ,closeBtn: false
                                ,area: '1000px;'
                                ,shade: 0.8
                                ,id: 'LAY_layuipro' //设定一个id，防止重复弹出
                                ,btn: ['查看详情', '关闭']
                                ,btnAlign: 'c'
                                ,moveType: 1 //拖拽模式，0或者1
                                //,content: '<div style="padding: 50px; line-height: 22px; background-color: #393D49; color: #fff; font-weight: 300;">你知道吗？亲！<br>layer ≠ layui<br><br>layer只是作为Layui的一个弹层模块，由于其用户基数较大，所以常常会有人以为layui是layerui<br><br>layer虽然已被 Layui 收编为内置的弹层模块，但仍然会作为一个独立组件全力维护、升级。<br><br>我们此后的征途是星辰大海 ^_^</div>'
                                ,content: '<div id="showAll" style="height: 450px;margin-top: 5px"></div>'
                                ,success: function(layero){
                                    var btn = layero.find('.layui-layer-btn');
                                    btn.find('.layui-layer-btn0').attr({
                                        class:"btn-success"
                                    });
                                    // document.cookie="="+id;
                                    // window.location.href = 'articleinfo.html';
                                    console.log("确认");
                                }
                            });
                            loadMap(data);
                        } else if(layEvent === 'detailPoint'){
                            loadDistanceInfo(data);
                            layer.open({
                                type: 1
                                ,title: false //不显示标题栏
                                ,closeBtn: false
                                ,area: '500px;'
                                ,shade: 0.8
                                ,id: 'LAY_layuipro1' //设定一个id，防止重复弹出
                                ,btn: ['查看详情', '关闭']
                                ,btnAlign: 'c'
                                ,moveType: 1 //拖拽模式，0或者1
                                ,content: '<div id="info" style="padding: 50px; line-height: 22px; background-color: #393D49; color: #fff; font-weight: 300;"></div>'
                                ,success: function(layero){
                                    var btn = layero.find('.layui-layer-btn');
                                    btn.find('.layui-layer-btn0').attr({
                                        href: '#'
                                        ,target: '_blank'
                                    });
                                }
                            });
                        }
                    });

                });
            }

            /**
             * @description 获取所有的目标点距离,后台已经计算的最新结果，非实时计算一次（30s）
             */
            $scope.targetIdList=undefined;
            function getAllDistance() {
                $http({
                    url : 'distance/getnewdistance',
                    method : 'POST',
                    data:{
                        "taskId":$scope.taskId
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    $scope.allPointDistance = resp.data["pointDistance"];
                    $scope.targetIdList = resp.data["targetIdList"];
                    loadTable();
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            }

            /**
             * @description 获取所有的目标点距离，实时计算
             */
            $scope.calculateDistance=function (){
                $http({
                    url : 'distance/calculatedistance',
                    method :  'POST',
                    data:{
                        "taskId":$scope.taskId
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    $scope.allPointDistance = resp.data["pointDistance"];
                    $scope.targetIdList = resp.data["targetIdList"];
                    loadTable();
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            };

            $scope.showSwitch = "关";
            let intervalId=undefined;
            $scope.timeDuration=[2,5,10,20,30];
            $scope.selectedTimeDuring=$scope.timeDuration[0];
            $scope.intervalCalculate=function () {
                if($scope.showSwitch==="关"){
                    intervalId = setInterval($scope.calculateDistance,$scope.selectedTimeDuring*1000);
                    $scope.showSwitch = "开";
                    swal.fire({
                        title:"开启刷新，定时为 "+$scope.selectedTimeDuring+" 秒",
                        timer:2000
                    })
                }else {
                    clearInterval(intervalId);
                    $scope.showSwitch = "关";
                    swal.fire({
                        title:"关闭定时刷新",
                        timer:2000
                    })
                }
            };


            $scope.moreInformation=function (loc) {
                document.cookie="targetId="+loc.targetId;
                document.cookie="taskId="+$scope.taskId;
                console.log("loc.targetId",loc.targetId);
                window.open('pages-fencing.html');
            };


            /**
             * @description 加载地图，显示所有点轨迹信息。
             */
            function loadMap(data) {
                let locationMap = new BMap.Map("showAll");
                locationMap.enableScrollWheelZoom(true);
                $http({
                    url : 'distance/getadistance',
                    method : 'POST',
                    data : {
                        "userId":data["userId"],
                        "targetId":data["targetId"],
                        "taskId":$scope.taskId
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    $scope.pointUser = resp.data["pointUser"];
                    $scope.pointTarget = resp.data["pointTarget"];
                    let pointUser = new BMap.Point($scope.pointUser["lng"],$scope.pointUser["lat"]);
                    let markerUser = new BMap.Marker(pointUser);  // 创建标注
                    locationMap.centerAndZoom(pointUser, 17);
                    locationMap.addOverlay(markerUser);              // 将标注添加到地图中
                    let labelUser = new BMap.Label("目标id:"+$scope.pointUser["userId"],{offset:new BMap.Size(20,-10)});
                    labelUser.setStyle({
                        color:"#4821ff"
                    });
                    markerUser.name="markerUser";
                    markerUser.setLabel(labelUser);
                    locationMap.addOverlay(labelUser);

                    let pointTarget = new BMap.Point($scope.pointTarget["lng"],$scope.pointTarget["lat"]);
                    let markerTarget = new BMap.Marker(pointTarget);  // 创建标注
                    //map.centerAndZoom(point, 17);
                    locationMap.addOverlay(markerTarget);              // 将标注添加到地图中
                    let labelTarget = new BMap.Label("目标id:"+$scope.pointTarget["userId"],{offset:new BMap.Size(20,-10)});
                    labelTarget.setStyle({
                        color:"#4821ff"
                    });
                    markerTarget.name="markerUser";
                    markerTarget.setLabel(labelTarget);
                    locationMap.addOverlay(labelTarget);



                    let pointMiddle = new BMap.Point(($scope.pointTarget["lng"]+$scope.pointUser["lng"])/2,
                        ($scope.pointTarget["lat"]+$scope.pointUser["lat"])/2);
                    let opts = {
                        position : pointMiddle,    // 指定文本标注所在的地理位置
                        offset   : new BMap.Size(30, -30)    //设置文本偏移量
                    };
                    let label = new BMap.Label("距离:"+resp.data["distance"], opts);  // 创建文本标注对象
                    label.setStyle({
                        color : "red",
                        fontSize : "12px",
                        height : "20px",
                        lineHeight : "20px",
                        fontFamily:"微软雅黑"
                    });
                    locationMap.addOverlay(label);


                    let polyline =new BMap.Polyline([pointUser,pointTarget]
                        , {strokeColor:"red", strokeWeight:2, strokeOpacity:0.5});
                    locationMap.addOverlay(polyline);//增加折线
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });

            }

            /**
             * @description 获取所有点的坐标详情。
             */
            function loadDistanceInfo(data) {
                $http({
                    url: 'distance/getadistance',
                    method: 'POST',
                    data: {
                        "userId": data["userId"],
                        "targetId": data["targetId"],
                        "taskId":$scope.taskId
                    }
                }).then(function (resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    $scope.pointUser = resp.data["pointUser"];
                    $scope.pointTarget = resp.data["pointTarget"];
                    let userInfo = "目标"+data["userId"]+"信息：<br>经度："+$scope.pointUser["lng"]+"  纬度："+$scope.pointUser["lat"]+"<br>地点名："+$scope.pointUser["locationName"]+"<br>"+
                        "<hr>目标"+data["targetId"]+"信息：<br>经度："+$scope.pointTarget["lng"]+"  纬度："+$scope.pointTarget["lat"]+"<br>地点名："+$scope.pointTarget["locationName"];
                    document.getElementById("info").innerHTML = userInfo;
                }, function (resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            }
        });