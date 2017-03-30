angular.module('MiHexample', ['MiHexample.service', 'jsonforms', 'jsonforms-bootstrap', 'ui.bootstrap'])
    .controller('TasksController', ['Schema', 'UISchema', 'Task', function(Schema, UISchema, Task) {
        var vm = this;


    vm.taskSchema = Schema;
    vm.taskUISchema = UISchema;
    vm.taskData=Task;       
    }]);
