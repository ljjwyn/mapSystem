/**
 * pages-map的控制器
 *
 * @author lijiajie
 */
indexApp
    .controller(
        'pagesMapCtrl',
        function($scope, $http, $timeout,$interval) {
            $scope.url="http://127.0.0.1:3001/";
            //$scope.url="http://119.167.221.16:23000/";
            $scope.graphInfo = "dewcwc";
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
            $scope.tableName=getCookie("tableName");
            $scope.userId=1; //getCookie("userId");
            $scope.startTimeStemp=getCookie("startTimeStemp");
            console.log("tableName", $scope.tableName);
            console.log("startTimeStemp", $scope.startTimeStemp);
            console.log("userId", $scope.userId);
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
                    title: '热点图日期选择',
                    content: '您选择的日期为：'+$scope.dateSelect
                    ,anim: 1 //0-6的动画形式，-1不开启
                });
                $scope.lastTime = time3;
                $scope.finshTime = $scope.lastTime+86400;
                $scope.userDateTabelName = $scope.userId+"_"+$scope.dateSelect.replace('-', '').replace('-', '');
                console.log("userDateTabelName",$scope.userDateTabelName);
                $scope.loadMapInfo();
            };
            $scope.loadMapInfo = function(){
                $http({
                    url : $scope.url+'loadlnglat',
                    method : 'POST',
                    data : {
                        "startTimeStemp": $scope.lastTime,
                        "tableName": $scope.userDateTabelName
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    $scope.locationList=resp.data["locationList"];
                    $scope.coordinate=resp.data["coordinate"];
                    $scope.hotPoint=resp.data["hotPoint"];
                    $scope.stoppingPoint=resp.data["stoppingPoint"];
                    $scope.hourRecord=resp.data["hourRecord"];
                    $scope.locNameSet=resp.data["locNameSet"];
                    $scope.tableData=resp.data["tableData"];
                    console.log("$scope.stoppingPoint",$scope.stoppingPoint);
                    $scope.setTable($scope.tableData);
                    hotPointF($scope.hotPoint);
                    hotPointLabel($scope.stoppingPoint);
                    $scope.echartShow($scope.hourRecord,$scope.locNameSet);
                    // setMap($scope.coordinate);
                    // transLoc($scope.coordinate);
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            };
            $scope.isactive1="nav-link active";
            $scope.isactive2="nav-link";
            $scope.hotpointMapShow=true;
            $scope.hotpointShow=false;
            $scope.showHotpointMap=function () {
                $scope.isactive1="nav-link active";
                $scope.isactive2="nav-link";
                $scope.hotpointMapShow=true;
                $scope.hotpointShow=false;
            };
            $scope.showHotpoint=function () {
                $scope.isactive1="nav-link";
                $scope.isactive2="nav-link active";
                $scope.hotpointMapShow=false;
                $scope.hotpointShow=true;
            };
            function setMap(coordinate) {
                var map = new BMap.Map("allmap");    // 创建Map实例
                map.centerAndZoom(new BMap.Point(coordinate[0][0], coordinate[0][1]), 14);  // 初始化地图,设置中心点坐标和地图级别
                map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
                var sy = new BMap.Symbol(BMap_Symbol_SHAPE_BACKWARD_OPEN_ARROW, {
                    scale: 0.6,//图标缩放大小
                    strokeColor:'#fff',//设置矢量图标的线填充颜色
                    strokeWeight: '2',//设置线宽
                });
                var icons = new BMap.IconSequence(sy, '10', '30'); // 创建polyline对象
                var pois1 = [];
                for(var i = 0; i<coordinate.length; i++){
                    console.log(coordinate[i]);
                    pois1.push(new BMap.Point(coordinate[i][0], coordinate[i][1]));
                }
                var polyline =new BMap.Polyline(pois1, {
                    enableEditing: false,//是否启用线编辑，默认为false
                    enableClicking: true,//是否响应点击事件，默认为true
                    //icons:[icons],
                    strokeWeight:'4',//折线的宽度，以像素为单位
                    strokeOpacity: 0.8,//折线的透明度，取值范围0 - 1
                    strokeColor:"#18a45b" //折线颜色
                });

                map.addOverlay(polyline);          //增加折线
            }
            function hotPointF(pointList) {
                var map = new BMap.Map("allmap");          // 创建地图实例
                var point = new BMap.Point(pointList[0]["locLat"][0], pointList[0]["locLat"][1]);
                map.centerAndZoom(point, 15);             // 初始化地图，设置中心点坐标和地图级别
                map.enableScrollWheelZoom(); // 允许滚轮缩放
                var top_left_control = new BMap.ScaleControl({anchor: BMAP_ANCHOR_TOP_LEFT});// 左上角，添加比例尺
                var top_left_navigation = new BMap.NavigationControl();  //左上角，添加默认缩放平移控件
                var top_right_navigation = new BMap.NavigationControl({anchor: BMAP_ANCHOR_TOP_RIGHT, type: BMAP_NAVIGATION_CONTROL_SMALL});
                map.addControl(top_left_control);
                map.addControl(top_left_navigation);
                map.addControl(top_right_navigation);
                var points1 = [];
                $scope.hotPointStr='目标位移轨迹热度：\n';
                for(var i=0;i<pointList.length;i++){
                    points1.push({"lng":pointList[i]["locLat"][0],"lat":pointList[i]["locLat"][1],"count":pointList[i]["count"]})
                    $scope.hotPointStr += "lng:"+pointList[i]["locLat"][0]+",lat:"+pointList[i]["locLat"][1]+",位置标注:"+pointList[i]["locLabeled"]+"\n";
                }

                if(!isSupportCanvas()){
                    alert('热力图目前只支持有canvas支持的浏览器,您所使用的浏览器不能使用热力图功能~')
                }
                //详细的参数,可以查看heatmap.js的文档 https://github.com/pa7/heatmap.js/blob/master/README.md
                //参数说明如下:
                /* visible 热力图是否显示,默认为true
                 * opacity 热力的透明度,1-100
                 * radius 势力图的每个点的半径大小
                 * gradient  {JSON} 热力图的渐变区间 . gradient如下所示
                 *	{
                        .2:'rgb(0, 255, 255)',
                        .5:'rgb(0, 110, 255)',
                        .8:'rgb(100, 0, 255)'
                    }
                    其中 key 表示插值的位置, 0~1.
                        value 为颜色值.
                 */
                heatmapOverlay = new BMapLib.HeatmapOverlay({"radius":15});
                map.addOverlay(heatmapOverlay);
                heatmapOverlay.setDataSet({data:points1,max:200});
                //是否显示热力图
                heatmapOverlay.show();
                // function openHeatmap(){
                //     heatmapOverlay.show();
                // }
                // function closeHeatmap(){
                //     heatmapOverlay.hide();
                // }
                // closeHeatmap();
                function setGradient(){
                    /*格式如下所示:
                   {
                         0:'rgb(102, 255, 0)',
                         .5:'rgb(255, 170, 0)',
                         1:'rgb(255, 0, 0)'
                   }*/
                    var gradient = {};
                    var colors = document.querySelectorAll("input[type='color']");
                    colors = [].slice.call(colors,0);
                    colors.forEach(function(ele){
                        gradient[ele.getAttribute("data-key")] = ele.value;
                    });
                    heatmapOverlay.setOptions({"gradient":gradient});
                }
                //判断浏览区是否支持canvas
                function isSupportCanvas(){
                    var elem = document.createElement('canvas');
                    return !!(elem.getContext && elem.getContext('2d'));
                }
            }
            function hotPointLabel(pointList) {
                var map = new BMap.Map("hotMap");
                var point = new BMap.Point(pointList[0]['lng'], pointList[0]['lat']);
                map.centerAndZoom(point, 15);
                map.enableScrollWheelZoom();
                var top_left_control = new BMap.ScaleControl({anchor: BMAP_ANCHOR_TOP_LEFT});// 左上角，添加比例尺
                var top_left_navigation = new BMap.NavigationControl();  //左上角，添加默认缩放平移控件
                var top_right_navigation = new BMap.NavigationControl({anchor: BMAP_ANCHOR_TOP_RIGHT, type: BMAP_NAVIGATION_CONTROL_SMALL});
                map.addControl(top_left_control);
                map.addControl(top_left_navigation);
                map.addControl(top_right_navigation);
                console.log(pointList);
                for(var i=0;i<pointList.length;i++){
                    var tempPoint = new BMap.Point(pointList[i]['lng'], pointList[i]['lat']);
                    var marker = new BMap.Marker(tempPoint);  // 创建标注
                    map.addOverlay(marker);               // 将标注添加到地图中
                    marker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
                }
            }
            function transLoc(gpsLoc) {
                var points = [];
                for(var i = 0; i<10; i++){
                    console.log(gpsLoc[i][0], gpsLoc[i][1]);
                    points.push(new BMap.Point(gpsLoc[i][0], gpsLoc[i][1]));
                }
                // var points = [new BMap.Point(116.309978,39.98595),
                //     new BMap.Point(116.310034,39.986051),
                //     new BMap.Point(116.310025,39.986174),
                //     new BMap.Point(116.309956,39.986305),
                //     new BMap.Point(116.309862,39.986461)
                // ];

                //地图初始化
                var bm = new BMap.Map("allmap");
                bm.centerAndZoom(new BMap.Point(gpsLoc[0][0], gpsLoc[0][1]), 15);
                bm.enableScrollWheelZoom(true);
                //坐标转换完之后的回调函数
                translateCallback = function (data){
                    if(data.status === 0) {
                        for (var i = 0; i < data.points.length; i++) {
                            bm.addOverlay(new BMap.Marker(data.points[i]));
                            bm.setCenter(data.points[i]);
                        }
                    }
                };
                setTimeout(function(){
                    var convertor = new BMap.Convertor();
                    convertor.translate(points, 1, 5, translateCallback)
                }, 1000);
            }
            function getLocLat() {
                var map = new BMap.Map("allmap");
                map.centerAndZoom("重庆",12);
                //单击获取点击的经纬度
                map.addEventListener("click",function(e){
                    //alert(e.point.lng + "," + e.point.lat);
                    var marker = new BMap.Marker(new BMap.Point(e.point.lng, e.point.lat)); // 创建点
                    map.addOverlay(marker);
                });

            }

            $scope.echartShow=function (hourRecord, locNameSet) {
                var lngLat = [];
                var hourData = [];
                for(var i=0;i<hourRecord.length;i++){
                    // var tempData = "时间点:"+hourRecord[i]["24hour"]+",lng:"+hourRecord[i]["lng"]+",lat:"+hourRecord[i]["lat"];
                    // lngLat.push(tempData);
                    hourData.push({value: hourRecord[i]["stayTime"], name: hourRecord[i]["locLabeled"]});
                }
                var myChart = echarts.init(document.getElementById('hourRecord'));
                var option = {
                    tooltip: {
                        trigger: 'item',
                        formatter: '{a} <br/>{b}: {c} ({d}%)'
                    },
                    legend: {
                        orient: 'vertical',
                        left: 10,
                        data: locNameSet
                    },
                    series: [
                        {
                            name: '驻留点时段分布',
                            type: 'pie',
                            radius: ['50%', '70%'],
                            avoidLabelOverlap: false,
                            label: {
                                show: false,
                                position: 'center'
                            },
                            emphasis: {
                                label: {
                                    show: true,
                                    fontSize: '30',
                                    fontWeight: 'bold'
                                }
                            },
                            labelLine: {
                                show: false
                            },
                            data: hourData
                            //     [
                            //     {value: 8, name: '直接访问'},
                            //     {value: 3, name: '邮件营销'},
                            //     {value: 1, name: '联盟广告'},
                            //     {value: 4.5, name: '视频广告'},
                            //     {value: 6, name: '搜索引擎'}
                            // ]
                        }
                    ]
                };
                myChart.setOption(option,true);
                window.addEventListener("resize", function () {
                    myChart.resize();
                });
            };
            $scope.setTable=function(tableData) {
                //使用layui的表格
                layui.use('table', function(){
                    var table = layui.table;

                    //第一个实例
                    table.render({
                        elem: '#demo'
                        ,height: 400
                        ,data: tableData
                        ,page: true //开启分页
                        ,cols: [[ //表头
                            {field: 'userId', title: '用户ID', sort: true, fixed: 'left'}
                            ,{field: 'lng', title: '经度'}
                            ,{field: 'lat', title: '纬度'}
                            ,{field: 'count', title: '停留时间(s)'}
                            ,{field: 'locLabeled', title: '位置标记'}
                        ]]
                    });

                });
            }
        });