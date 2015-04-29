
        return {
            restrict: 'E',
            controller: ['BaseController', '$scope', 'OseeAppSchema', function(BaseController, $scope, OseeAppSchema) {
                var vm = this;

                $scope.onInit = function() {
                }
                $scope.onNgChange = function(controlschema) {
                    OseeAppSchema.updateItem(controlschema);
                }
                BaseController.call(vm, $scope, OseeAppSchema);
            }],
            controllerAs: 'vm',
            template: `
                <jsonforms-control>
                    <select ng-options="option as option for option in vm.resolvedSchema.enum"
                        id="{{vm.id}}"
                        class="form-control jsf-control-enum"
                        ng-model="vm.resolvedData[vm.fragment]"
                        ng-change="onNgChange('{{vm.uiSchema}}')"
                        data-ng-init="onInit()">
                    </select>
               </jsonforms-control>
            `
        };
    })
    .run(['RendererService', 'JSONFormsTesters', function(RendererService, Testers) {
        RendererService.register('osee-dropdown-control', Testers.and(
            // Inherit this custom control from schema that call out the following using this single option:
            Testers.optionIs('customControlName', 'oseeDropdownControl')
        ), 10);
}]);