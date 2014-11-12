'use strict';

angular.module('tabletuing.services', ['ngResource'])
	.factory('fieldWorkerService', ['$resource',
	  function($resource){
		var fieldWorkerResource = $resource(contextPath + '/api/rest/fieldworkers',
			   {},
			   {
				   getAllFieldWorkers: {method: "GET"}
			   });
	    return fieldWorkerResource;
	  }])
	  .factory('locationService', ['$resource',
	   function($resource) {
		  return $resource(contextPath + '/api/rest/locations/locationLevel/:locationLevel',
			   {locationLevel : "@locationLevel"},
			   {
				   getLocationsForLevel: {method: "GET"}
			   });
	  }]);