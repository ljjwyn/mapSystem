/**
 * pages-fencing的控制器
 *
 * @author lijiajie
 */
indexApp
    .controller(
        'pagesManagementCtrl',
        function($scope, $http, $timeout,$interval) {

            getAllTask();
            $scope.taskManagementList = [];
            $scope.taskId=undefined;
            $scope.taskName=undefined;
            $scope.taskDescribe=undefined;
            function getAllTask(fencingId){
                $http({
                    url : 'taskmanagement/getalltask',
                    method : 'GET'
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    $scope.taskManagementList=resp.data["taskList"]
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            }

            $scope.createTask=function () {
                $http({
                    url : 'taskmanagement/createtask',
                    method : 'POST',
                    data:{
                        "taskName":$scope.taskName,
                        "taskDescribe":$scope.taskDescribe
                    }
                }).then(function(resp, status) {
                    $scope.status = status;
                    console.log(resp.data);
                    swal.fire({
                        title:"创建新任务："+resp.data["state"],
                        timer:2000
                    });
                    getAllTask();
                }, function(resp, status) {
                    $scope.resp = resp;
                    $scope.status = status;
                });
            };

            $scope.goIntoTask=function (task) {
                document.cookie="taskId="+task.id;
                document.cookie="taskName="+task.taskName;
                window.open('pages-distance.html');
            }
        });