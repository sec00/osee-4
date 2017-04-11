// A RESTful factory for retrieving tasks
app.factory('Tasks', ['$resource', function ($resource) {
      return $resource('http://localhost:3004/tasks/:taskId', { taskId: '@id' }, {
        'update': { method:'PUT' }
    });
  }]);
