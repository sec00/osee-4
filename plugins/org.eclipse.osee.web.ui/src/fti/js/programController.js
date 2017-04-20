app.controller('programController', ['Task', '$route', function(Task, $route) {
	var vm = this;
	vm.programs = [{ name: "hello", id: "432"}, {name : "world", id: "422"}];
	vm.test = "h";
	
	$route.current.params;
	
	vm.gridOptions = {
		data: vm.programs,
		columnDefs: [
		    { name: 'name'},
		    { name: 'id', cellTemplate: '<div class="ui-grid-cell-contents" title="TOOLTIP"><a href="#/issue?issueId={{row.entity.id}}">{{row.entity.id}}</a></div>'},
		]
	}
}]);