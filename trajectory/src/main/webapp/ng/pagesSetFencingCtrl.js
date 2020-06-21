/**
 * pages-fencing的控制器
 *
 * @author lijiajie
 */
indexApp
    .controller(
        'pagesSetFencingCtrl',
        function($scope, $http, $timeout,$interval) {



            getAllFencing();
            function getAllFencing(){
                $http({
                    url : 'fencing/getfilterfencing',
                    method : 'GET'
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    $scope.fencingInfoList = resp.data["fencingList"];
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            }

            $scope.loadFencing=function(fencing){
                map.clearOverlays();
                console.log("fencing",fencing);
                let fencingPoint = JSON.parse(fencing.fencingJson);
                console.log("fencingPoint",fencingPoint.fencingJSON);
                $scope.fencingPointList = fencingPoint.fencingJSON;
                $scope.finshFencing();
            };

            /**
             * @description 加载显示地图
             */
            $scope.fencingPointList = [];
            $scope.initPoint=undefined;
            $scope.stopMapFlage = 0;
            let map = new BMap.Map("allmap");
            map.centerAndZoom(new BMap.Point(120.505285,36.168837), 16);
            map.enableScrollWheelZoom(true);
            let locList=[];
            //单击获取点击的经纬度
            $scope.firstFlag = 0;
            let geoc = new BMap.Geocoder();
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
             * @description 设置电子栅栏的定点坐标
             * @param e
             * @param locationInfo
             */
            $scope.confirmLocation = function (e, locationInfo) {
                var tempMap = {
                    "lng":e.point.lng,
                    "lat":e.point.lat
                };
                $scope.fencingPointList.push(tempMap);
                console.log("locationInfo",locationInfo);
            };

            $scope.isClickConfirm=false;
            $scope.finshFencing=function () {
                let pointList = [];
                for (var i = 0; i < $scope.fencingPointList.length; i++) {
                    pointList.push(new BMap.Point($scope.fencingPointList[i]["lng"], $scope.fencingPointList[i]["lat"]));
                }
                console.log(pointList);
                let polygon = new BMap.Polygon(pointList, {strokeColor:"blue", strokeWeight:2, strokeOpacity:0.5});  //创建多边形
                polygon.name="fencingLine";
                map.addOverlay(polygon);
                $scope.isClickConfirm=true;
            };
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
            };


            $scope.fencingDescribe = undefined;
            $scope.confirm = function () {
                if($scope.fencingDescribe===null){
                    swal.fire({
                        "title":"必须输入围栏描述",
                        "timer":2000
                    })
                }else {
                    $http({
                        url : 'fencing/setfilterfencing',
                        method : 'POST',
                        data : {
                            "fencingDescribe":$scope.fencingDescribe,
                            "fencingPoints":$scope.fencingPointList,
                            "isFilterFencing":1,
                            "userId":"",
                            "taskId":0
                        }
                    }).then(function(resp, status) {
                        $scope.status = status;
                        console.log(resp.data);
                        swal.fire({
                            title:resp.data["state"],
                            timer:2000
                        });
                        getAllFencing();
                    }, function(resp, status) {
                        $scope.resp = resp;
                        $scope.status = status;
                    });
                }
            };

            /**
             * @description 清空地图上画过的轨迹并删除后端数据库的围栏信息。
             */
            $scope.deleteFencing=function(fencing){
                $http({
                    url : 'fencing/deleteareafencing',
                    method : 'POST',
                    data : {
                        "fencingId":fencing.id
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    swal.fire({
                        title:resp.data["state"],
                        timer:2000
                    });
                    if(resp.data["state"]==="success"){
                        $scope.reSetFencing();
                        getAllFencing();
                    }
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
                //map.clearOverlays();
            };
        });