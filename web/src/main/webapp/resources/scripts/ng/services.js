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
				   createLocation: {method: "POST"}
			   });
	  }])
	  .factory('locationHierService', ['$resource', function($resource) {
		  return $resource(contextPath + '/api/rest/locationhierarchies/:path/:id',
			  {},
			  {
				  getLevels: {method: "GET", params: {path:"levels"}}
			  });
	  }])
	  .factory('individualService', ['$resource', function($resource) {
		  return $resource(contextPath + '/api/rest/individuals/:path/:id',
			  {},
			  {
				  getIndividual: {method: "GET"},
				  getIndividualsForSocialGroup: {method: "GET", params: {path: "socialgroup"}},
				  createIndividual: {method: "POST"}
			  });
	  }])
	  .factory('individualFormService', ['$resource', function($resource) {
		  return $resource(contextPath + '/api/rest/individualForm',
			  {},
			  {
				  createIndividualForm: {method: "POST"}
			  });
	  }])
	  .factory('socialGroupService', ['$resource', function($resource) {
		  return $resource(contextPath + '/api/rest/socialgroups/:path/:id',
			  {},
			  {
				  getSocialGroup: {method: "GET"},
				  createSocialGroup: {method: "POST"}
			  });
	  }]);