app.controller('TasksController', ['Schema', 'UISchema', 'Tasks', 'Task', function(Schema, UISchema, Tasks, Task) {
        var vm = this;
        vm.taskSchema = Schema;
        vm.taskUISchema = UISchema;
        vm.tasks=Tasks.query();
        vm.dataLoaded=true;
        vm.taskData = Task;
        vm.temp = 4;

        vm.query = function() {
            Tasks.query(function(response){
                vm.tasks = response;
                // vm.selectDetail();
            });
        };
        vm.selectDetail=function(){
            var taskId = vm.tasks[1].id;
            vm.dataLoaded=false;
            vm.selectedId=taskId;
            vm.taskData = Tasks.get({taskId: taskId}, function() {
                vm.dataLoaded=true;
            });
        };
        vm.save=function(){
            vm.taskData.$update(function(){
                vm.query();
            });
        };
        vm.remove=function(id){
            vm.dataLoaded=false;
            vm.taskData=undefined;
            var toDelete = Tasks.get({taskId: id}, function() {
                toDelete.$delete(function(){
                    vm.query();
                });
            });
        };
        vm.addNew=function(){
            new Tasks({name:"New Task"}).$save(function(){
                vm.query();
            });
        };
        vm.close=function(){
            vm.dataLoaded=false;
            vm.taskData=null;
            vm.selectedId=null;
        };
        vm.query();
       
    }]);
