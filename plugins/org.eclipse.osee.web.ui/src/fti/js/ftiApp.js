var app = angular.module('ftiApp', ['ngRoute', 'ngStorage', 'ngCookies', 'jsonforms', 'jsonforms-bootstrap', 'ui.bootstrap', 'ngResource']);

app.config(['$routeProvider',
function($routeProvider) {
	
  $routeProvider.when('/', {
	  templateUrl: '/fti/views/landing.html',
      controller: 'landingController as lc'
  }).when('/issue', {
	  templateUrl: '/fti/views/issue_view.html',
      controller: 'tasksController as tc'
  })
  .otherwise({
    redirectTo: "/"
    });
  }
]);

