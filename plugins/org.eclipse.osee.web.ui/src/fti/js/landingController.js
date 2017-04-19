app.controller('landingController', ['Task', function(Task) {
	var vm = this;
	vm.programs = [{ name: "hello", id: "432"}, {name : "world", id: "422"}];
	vm.test = "h";
}]);