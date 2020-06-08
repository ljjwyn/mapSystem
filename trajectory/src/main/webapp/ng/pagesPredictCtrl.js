indexApp
    .controller(
        'pagesPredictCtrl',
        function($scope, $http, $timeout,$interval) {
            $scope.pathPoint = [];
            function getLocation() {
                var map = new BMap.Map("routeSet");
                map.centerAndZoom(new BMap.Point(120.505169, 36.168954), 16);
                map.enableScrollWheelZoom(true);
                //单击获取点击的经纬度
                map.addEventListener("click",function(e){
                    $scope.pathPoint.push();
                    var marker = new BMap.Marker(new BMap.Point(e.point.lng, e.point.lat)); // 创建点
                    map.addOverlay(marker);
                });
            }

            function routeShow() {
                var bmap = new BMapGL.Map("predictShow");    // 创建Map实例
                bmap.centerAndZoom(new BMapGL.Point(120.505169, 36.168954), 17);  // 初始化地图,设置中心点坐标和地图级别
                bmap.enableScrollWheelZoom(true);     // 开启鼠标滚轮缩放
                var path = [{
                    'lng': 120.505169,
                    'lat': 36.168954
                }, {
                    'lng': 120.505169,
                    'lat': 36.178954
                }];
                var point = [];
                for (var i = 0; i < path.length; i++) {
                    point.push(new BMapGL.Point(path[i].lng, path[i].lat));
                }
                var pl = new BMapGL.Polyline(point);
                setTimeout(start(), 2000);
                function start() {
                    trackAni = new BMapGLLib.TrackAnimation(bmap, pl, {
                        overallView: true,
                        tilt: 30,
                        duration: 20000,
                        delay: 300
                    });
                    trackAni.start();
                }
            }
            getLocation();
            routeShow();
        });