indexApp
    .controller(
        'pagesAnalysisCtrl',
        function($scope, $http, $timeout,$interval) {
            $scope.url = "http://127.0.0.1:3001/";
            //$scope.url="http://119.167.221.16:23000/";
            $scope.selectTableName = [];
            $scope.timeHourIndex=[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23];
            $scope.selectedHourIndex=$scope.timeHourIndex[0];
            $scope.hourDetail=undefined;
            $scope.hourDataStr=undefined;
            $scope.getTableName=function(){
                $http({
                    url : 'recordmap/gettablename',
                    method : 'POST',
                    data : {
                        "userId": 1
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    $scope.tableNameList = resp.data["tableName"];
                    console.log("$scope.tableNameList",$scope.tableNameList);
                    $scope.setTable($scope.tableNameList);
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            };
            $scope.setTable=function(tableNameList) {
                //使用layui的表格
                layui.use('table', function(){
                    var table = layui.table;

                    //第一个实例
                    table.render({
                        elem: '#demo'
                        ,height: 500
                        ,data: tableNameList
                        ,page: false //开启分页
                        ,toolbar: '<div>' +
                            '<button lay-event="add"'+
                            ' class="btn btn-info btn-xs"\n' +
                            '<span class="fa fa-check">选择</span>\n' +
                            '</button></div>' //开启工具栏，此处显示默认图标，可以自定义模板，详见文档
                        ,totalRow: false //开启合计行
                        ,cols: [[ //表头
                            {type: 'checkbox', fixed: 'left'}
                            ,{field: 'tableName', title: '表名', sort: true, fixed: 'left'}
                        ]]
                    });
                    table.on('toolbar(test)', function(obj) {
                        console.log("data");
                        var checkStatus = table.checkStatus(obj.config.id)
                        ,data = checkStatus.data; //获取选中的数据
                        console.log("data",checkStatus);
                        $scope.selectTableName = checkStatus.data;
                        $scope.analysisData($scope.selectTableName);
                    });
                });
            };
            $scope.setTable2=function(hourData) {
                //使用layui的表格
                layui.use('table', function(){
                    var table = layui.table;

                    //第一个实例
                    table.render({
                        elem: '#demo1'
                        ,height: 350
                        ,data: hourData
                        ,page: true //开启分页
                        ,toolbar: 'default' //开启工具栏，此处显示默认图标，可以自定义模板，详见文档
                        ,totalRow: true //开启合计行
                        ,cols: [[ //表头
                            {field: 'distence', title: '基准距离', sort: true, width:100, fixed: 'left', totalRowText: '合计：'}
                            ,{field: 'labelName', title: '位置标注', width:100}
                            ,{field: 'lat', title: '纬度', width: 100}
                            ,{field: 'lng', title: '经度', width:100}
                            ,{field: 'count', title: '重复天数', width: 100, fixed: 'right', totalRow: true}
                        ]]
                    });
                });
            };
            $scope.analysisData=function(selectTableName){
                swal.fire({
                    title:"分析选中天数轨迹，请稍等"
                });
                console.log("selectTableName",selectTableName);
                $http({
                    url : $scope.url+'analysisdata',
                    method : 'POST',
                    data : selectTableName
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    $scope.hourDetail=resp.data["hourList"];
                    swal.close();
                    $scope.selectChange();
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            };
            $scope.selectChange=function(){
                var selectHourList=$scope.hourDetail[$scope.selectedHourIndex];
                var titleList=[];
                var chartDataList=[];
                for(var i=0;i<selectHourList.length;i++){
                    titleList.push(selectHourList[i]["labelName"]);
                    var tempMap={
                        "value":selectHourList[i]["count"],
                        "name":selectHourList[i]["labelName"]
                    };
                    chartDataList.push(tempMap);
                }
                $scope.echartShow(titleList, chartDataList);
                $scope.setTable2(selectHourList);
            };

            $scope.echartShow=function (titleData, hourData) {
                var myChart = echarts.init(document.getElementById('hourRecord'));
                var option = {
                    tooltip: {
                        trigger: 'item',
                        formatter: '{a} <br/>{b}: {c} ({d}%)'
                    },
                    legend: {
                        orient: 'vertical',
                        left: 10,
                        data: titleData
                    },
                    series: [
                        {
                            name: '访问来源',
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
                        }
                    ]
                };

                myChart.setOption(option,true);
                window.addEventListener("resize", function () {
                    myChart.resize();
                });
            };
            $scope.getTableName();
        });