'use strict';

angular.module('tabletuing.services', ['ngResource'])
	.factory('fieldWorkerService', ['$resource', function($resource) {
		return $resource(contextPath + '/api/rest/fieldworkers',
			   {},
			   {
				   getAllFieldWorkers: {method: "GET"}
			   });
	  }])
	  .factory('locationService', ['$resource', function($resource) {
		  return $resource(contextPath + '/api/rest/locations/:path/:id',
			   {},
			   {
				   getLocation: {method: "GET"},
				   getLocationsForLevel: {method: "GET", params: {path:"locationLevel"}},
				   saveLocation: {method: "POST"}
			   });
	  }])
	  .factory('locationHierService', ['$resource', function($resource) {
		  return $resource(contextPath + '/api/rest/locationhierarchies/:path/:id',
			  {},
			  {
				  getLevels: {method: "GET", params: {path:"levels"}}
			  });
	  }]);

//, id:"@locationLevel"