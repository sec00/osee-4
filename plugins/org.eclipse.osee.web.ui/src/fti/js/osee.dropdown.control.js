app.directive('oseeDropdownControl', function() {
        return {
            restrict: 'E',
            controller: ['BaseController', '$scope', function(BaseController, $scope) {
                var vm = this;
                BaseController.call(vm, $scope);
                vm.options = vm.resolvedSchema.enum;
            }],
            controllerAs: 'vm',
            templateUrl: '../views/osee_dropdown_control.html'
        };
    })
    .run(['RendererService', 'JSONFormsTesters', function(RendererService, Testers) {
        RendererService.register('osee-dropdown-control', Testers.and(
            Testers.uiTypeIs('Control'),
            Testers.schemaTypeIs('string'),
            Testers.schemaTypeMatches(el => _.has(el, 'enum'))
        ), 10);
    }]);