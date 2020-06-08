indexApp
    .controller(
        'pagesGetLocCtrl',
        function($scope, $http, $timeout,$interval) {
            $scope.url = "http://127.0.0.1:3001/";
            //$scope.url="http://119.167.221.16:23000/";
            getLocLat();
            //routePlan();
            $scope.userId=21180231337; //先写死id只有这一个人的轨迹信息
            $scope.locationInfo = "";
            $scope.runType=["walking","driving"];
            $scope.timeDuration=[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20
            ,21,22,23,24];
            $scope.selectRunType=$scope.runType[0];
            $scope.selectedTimeDuring=$scope.timeDuration[0];
            $scope.locationList = [];
            $scope.lngNum = undefined;
            $scope.latNum = undefined;
            $scope.displayInfo = undefined;
            $scope.locLabeled = undefined;
            function getLocLat() {
                var map = new BMap.Map("allmap");
                map.centerAndZoom(new BMap.Point(120.505169, 36.168954), 16);
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
                    var tempMap={
                        "userId":$scope.userId,
                        "lng":e.point.lng,
                        "lat":e.point.lat
                    };
                    var pt = e.point;
                    geoc.getLocation(pt, function(rs){
                        var addComp = rs.addressComponents;
                        var displayinfo =$scope.displayInfo = addComp.province + ", " + addComp.city + ", " + addComp.district + ", " + addComp.street + ", " + addComp.streetNumber;
                        console.log("$scope.displayInfo",$scope.displayInfo);
                        $scope.testyixia(e, displayinfo);
                    });
                    var marker = new BMap.Marker(new BMap.Point(e.point.lng, e.point.lat)); // 创建点
                    map.addOverlay(marker);
                    var lineList=[];
                    // for(var i=0;i<locList.length;i++){
                    //     lineList.push(new BMap.Point(locList[i][0], locList[i][1]));
                    // }
                    //$scope.recordSql(tempMap);
                    //alert(e.point.lng + "," + e.point.lat);
                    if(locList.length>=2){
                        lineList.push(new BMap.Point(locList[locList.length-2][0], locList[locList.length-2][1]));
                        lineList.push(new BMap.Point(locList[locList.length-1][0], locList[locList.length-1][1]));
                    }
                    var polyline = new BMap.Polyline(lineList
                        , {strokeColor:"blue", strokeWeight:2, strokeOpacity:0.5});
                    map.addOverlay(polyline);
                });
            }
            $scope.testyixia = function(e, displayInfo){
                swal({
                    title: "是否选择驻留点:\n"+displayInfo,
                    type: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: '确认',
                    cancelButtonText: '取消'
                }).then(function(isConfirm) {
                    console.log("ic", isConfirm);
                    if (isConfirm.value) {
                        $scope.locationMap = {};
                        $scope.lngNum = e.point.lng;
                        $scope.latNum = e.point.lat;
                        $scope.$apply();
                    }
                })
            };
            $scope.lastTime = 1577808000;
            $scope.finshTime = $scope.lastTime+86400;
            $scope.confirm = function(isFinsh){
                if($scope.locLabeled==undefined){
                    swal.fire({
                        title:"请输入驻留地标注",
                        timer:2000
                    })
                }
                else {
                    console.log($scope.selectedTimeDuring);
                    console.log($scope.selectRunType);
                    $scope.locationMap["userId"]=1;
                    $scope.locationMap["lng"]=$scope.lngNum;
                    $scope.locationMap["lat"]=$scope.latNum;
                    if($scope.firstFlag==0){
                        $scope.lastTime = $scope.locationMap["timeStemp"] = $scope.lastTime + $scope.selectedTimeDuring*3600;
                        $scope.locationMap["formatTime"] = timestampToTime($scope.locationMap["timeStemp"]);
                        $scope.locationMap["runType"] = "None";
                        $scope.locationMap["distance"] = 0;
                        $scope.locationMap["stayTime"] = $scope.selectedTimeDuring;
                        $scope.locationMap["duration"] = 0;
                        $scope.locationMap["tableName"] = $scope.userDateTabelName;
                        $scope.locationMap["locLabeled"] = $scope.locLabeled;
                        $scope.locationList.push($scope.locationMap);
                        $scope.firstFlag += 1;
                        $scope.recordSql($scope.locationMap);
                    }else {
                        if(isFinsh==1){
                            $scope.locationMap["timeStemp"] = $scope.finshTime;
                            $scope.locationMap["formatTime"] = timestampToTime($scope.locationMap["timeStemp"]);
                            $scope.locationMap["runType"] = $scope.selectRunType;
                            $scope.locationMap["stayTime"] = (($scope.finshTime-$scope.lastTime)/3600).toFixed(2);
                            $scope.locationMap["distance"] = "最终驻留点"; // python后端计算了后修改这里
                            $scope.locationMap["duration"] = "无"; //同上
                            $scope.locationMap["locLabeled"] = $scope.locLabeled;
                            $scope.locationList.push($scope.locationMap);
                        }else {
                            $scope.locationMap["timeStemp"] = $scope.lastTime+$scope.selectedTimeDuring*3600;
                            $scope.locationMap["formatTime"] = timestampToTime($scope.locationMap["timeStemp"]);
                            $scope.locationMap["runType"] = $scope.selectRunType;
                            $scope.locationMap["stayTime"] = $scope.selectedTimeDuring;
                            $scope.locationMap["distance"] = "计算中..."; // python后端计算了后修改这里
                            $scope.locationMap["duration"] = "计算中..."; //同上
                            $scope.locationMap["locLabeled"] = $scope.locLabeled;
                            $scope.locationList.push($scope.locationMap);
                        }
                        var requestMap = {
                            "departureLng": $scope.locationList[$scope.firstFlag-1]["lng"],
                            "departureLat": $scope.locationList[$scope.firstFlag-1]["lat"],
                            "arrivalLng": $scope.locationList[$scope.firstFlag]["lng"],
                            "arrivalLat": $scope.locationList[$scope.firstFlag]["lat"],
                            "startTime": $scope.locationList[$scope.firstFlag-1]["timeStemp"],
                            "runType": $scope.locationList[$scope.firstFlag]["runType"]
                        };
                        console.log("flag",$scope.firstFlag);
                        $scope.routePlan(requestMap,isFinsh);
                        //$scope.firstFlag += 1;
                    }
                }

                //$scope.recordSql(tempMap);
                //alert(e.point.lng + "," + e.point.lat);
            };
            $scope.routePlan = function(request,isFinsh){
                $http({
                    url : $scope.url+'planroute',
                    method : 'POST',
                    data : {
                        "request":request,
                        "tableName":$scope.userDateTabelName
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    if(isFinsh==1){
                        console.log("$scope.locationList11111", $scope.locationList[$scope.firstFlag]);
                        $scope.locationList[$scope.firstFlag]["tableName"] = $scope.userDateTabelName;
                        $scope.recordSql($scope.locationList[$scope.firstFlag]);
                    }else {
                        $scope.locationList[$scope.firstFlag]["distance"]=resp.data["distance"];
                        $scope.locationList[$scope.firstFlag]["duration"]=resp.data["duration"];
                        $scope.lastTime = $scope.locationList[$scope.firstFlag]["timeStemp"]+=resp.data["duration"];
                        $scope.locationList[$scope.firstFlag]["formatTime"] = timestampToTime($scope.locationList[$scope.firstFlag]["timeStemp"]);
                        console.log($scope.locationList[$scope.firstFlag]["formatTime"]);
                        $scope.locationList[$scope.firstFlag]["tableName"] = $scope.userDateTabelName;
                        $scope.recordSql($scope.locationList[$scope.firstFlag]);
                    }
                    $scope.firstFlag += 1;
                    setMap(resp.data["coordinate"]);
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            };
            $scope.selectPoint=function (location) {
                $scope.locationMap = {};
                $scope.lngNum = location['lng'];
                $scope.latNum = location['lat'];
                $scope.locLabeled = location['locLabeled'];
                swal.fire({
                    title:"选择现有驻留点。\nlng:"+location['lng']+"\nlat:"+location['lat']+"\n驻留点标注:"+$scope.locLabeled,
                    timer:2000
                })
            };
            function setMap(coordinate) {
                var map = new BMap.Map("routeMap");    // 创建Map实例
                map.centerAndZoom(new BMap.Point(coordinate[0][0], coordinate[0][1]), 16);  // 初始化地图,设置中心点坐标和地图级别
                map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
                var sy = new BMap.Symbol(BMap_Symbol_SHAPE_BACKWARD_OPEN_ARROW, {
                    scale: 0.6,//图标缩放大小
                    strokeColor:'#fff',//设置矢量图标的线填充颜色
                    strokeWeight: '2'//设置线宽
                });
                var icons = new BMap.IconSequence(sy, '1', '30'); // 创建polyline对象
                var pois1 = [];
                var pois2 = [];
                pois2.push(new BMap.Point(coordinate[0][0], coordinate[0][1]));
                pois2.push(new BMap.Point(coordinate[coordinate.length-1][0], coordinate[coordinate.length-1][1]));
                for(var i = 0; i<coordinate.length; i++){
                    console.log(coordinate[i]);
                    pois1.push(new BMap.Point(coordinate[i][0], coordinate[i][1]));
                }
                console.log("i am in");
                var polyline =new BMap.Polyline(pois1, {
                    enableEditing: false,//是否启用线编辑，默认为false
                    enableClicking: true,//是否响应点击事件，默认为true
                    //icons:[icons],
                    strokeWeight:'6',//折线的宽度，以像素为单位
                    strokeOpacity: 0.8,//折线的透明度，取值范围0 - 1
                    strokeColor:"#18a45b" //折线颜色
                });
                var polyline1 =new BMap.Polyline(pois2, {
                    enableEditing: false,//是否启用线编辑，默认为false
                    enableClicking: true,//是否响应点击事件，默认为true
                    icons:[icons],
                    strokeWeight:'6',//折线的宽度，以像素为单位
                    strokeOpacity: 0.2,//折线的透明度，取值范围0 - 1
                    strokeColor:"#18a45b" //折线颜色
                });

                map.addOverlay(polyline);          //增加折线
                map.addOverlay(polyline1);          //增加折线
            }
            function timestampToTime(timestamp) {
                var date = new Date(timestamp * 1000);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
                var Y = date.getFullYear() + '-';
                var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
                var D = date.getDate() + ' ';
                var h = date.getHours() + ':';
                var m = date.getMinutes() + ':';
                var s = date.getSeconds();
                return Y + M + D + h + m + s;
            }
            $scope.recordSql = function(record){
                $http({
                    url : 'recordmap/recordloc',
                    method : 'POST',
                    data : record
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log("recordSql",resp.data);
                    $scope.locationInfo += "lng:"+record["lng"]+",lat:"+
                        record["lat"]+",timeStemp:"+record["timeStemp"]+"\n";
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            };
            function routePlan() {
                var map = new BMap.Map("allmap");
                map.centerAndZoom(new BMap.Point(116.404, 39.915), 11);

                var p1 = new BMap.Point(116.301934,39.977552);
                var p2 = new BMap.Point(116.508328,39.919141);

                var driving = new BMap.DrivingRoute(map, {renderOptions:{map: map, autoViewport: true}});
                driving.search(p1, p2);
                console.log(driving);
            }
            //执行一个laydate实例
            laydate.render({
                elem: '#test1' //指定元素
            });
            $scope.dateConfirm = function(){
                $scope.dateSelect = document.getElementById("test1").value;
                console.log("$scope.dateSelect",$scope.dateSelect);
                var date = new Date($scope.dateSelect+' 00:00:00');
                var time3 = Date.parse(date)/1000;
                layer.open({
                    title: '日期选择结果',
                    content: '您选择的出行日期为：'+$scope.dateSelect
                    ,anim: 1 //0-6的动画形式，-1不开启
                });
                $scope.lastTime = time3;
                $scope.finshTime = $scope.lastTime+86400;
                $scope.locationList=[];
                $scope.lngNum = undefined;
                $scope.latNum = undefined;
                $scope.userDateTabelName = $scope.userId+"_"+$scope.dateSelect.replace('-', '').replace('-', '');
                console.log("userDateTabelName",$scope.userDateTabelName);
                createNewTabele($scope.userDateTabelName);
                getLocLat();
                var map1 = new BMap.Map("routeMap");
            };
            function createNewTabele(tableName) {
                $http({
                    url : 'recordmap/createtable',
                    method : 'POST',
                    data : {"tableName": tableName}
                }).then(function(resp, status) {
                    $scope.status = status;
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            }
            $scope.goToMap = function () {
                document.cookie="userId="+$scope.userId;
                console.log("document.cookie",document.cookie);
                window.location.href = 'pages-hotpoint.html';
            }
        });
