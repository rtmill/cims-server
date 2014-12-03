'use strict';

/* Controllers */

angular.module('tabletuing.controllers', ['ui.bootstrap'])
   .controller('MainCtrl', ['$scope', '$rootScope', '$resource', '$location', 'locationService', 'locationHierService', 'socialGroupService', 'individualService',
                            function ($scope, $rootScope, $resource, $location, locationService, locationHierService, socialGroupService, individualService) {
	   
	   // full hierarchy
	   $scope.hierarchyItems;
	   $scope.selectedLevel = null;
	   $scope.selectedHierList = [];
	   
	   // supported levels (Region, SubRegion, Village, etc.)
	   $scope.hierarchyLevels = [];
	   
	   // selected objects used in multiple controllers
	   $rootScope.selectedLocation = null;
	   $rootScope.selectedHousehold = null;
	   $rootScope.selectedIndividual = null;
	   
	   $rootScope.locations = [];
	   $rootScope.households = [];
	   $scope.individuals = [];
	   
	   var HIERARCHY_ROOT = 'HIERARCHY_ROOT';
	   // default to root
	   $scope.parentExtId = HIERARCHY_ROOT;
	   
	   $scope.getLevel = function(index) {
		   var location = null;
		   if (index < $scope.hierarchyLevels.length && $scope.selectedHierList[index] != null) {
			   location = $scope.selectedHierList[index];
		   }
		   return location;
	   };
	   $scope.disableLevel = function(index) {
		   //console.log("index " + index + " selected hier length " + $scope.selectedHierList.length);
		   return index >= $scope.selectedHierList.length;
	   };
	   $scope.levelSelected = function(index) {
		   // TODO move to reset function
		   // reset child types
		   $rootScope.selectedLocation = null;
		   $rootScope.locations = [];
		   $rootScope.selectedHousehold = null;
		   $rootScope.households = [];
		   $rootScope.selectedIndividual = null;
		   
		   // pop off selected level
		   var levelsToRemove = $scope.selectedHierList.length - index;
		   while (levelsToRemove > 0) {
			   $scope.selectedHierList.pop();
			   levelsToRemove--;
		   }
		   
		   // reset selected level
		   if ($scope.selectedHierList.length > 0) {
		   		$scope.selectedLevel = $scope.selectedHierList[$scope.selectedHierList.length - 1];
		  		$scope.parentExtId = $scope.selectedLevel.extId;
		   } else {
			   $scope.selectedLevel = null;
			   $scope.parentExtId = HIERARCHY_ROOT;
		   }
		   
	   }
	   $scope.selectLocation = function() {
		   locationService.getLocationsForLevel({id : $scope.selectedLevel.uuid})
	   			.$promise
	   			.then(function(result) {
	   				$rootScope.locations = result.data.locations;
	   			});
		   $rootScope.selectedLocation = null;
	   }
	   $scope.selectLocationItem = function(loc) {
		   $rootScope.selectedLocation = loc;
		   $rootScope.locations = [];
		   
		   // load social group with same extId as location
		   // if there is no social group found then allow for creation of individual and social group
		   // otherwise show list of individuals in social group and allow them to be edited or a new individual created
		   
		   // trying to load social group
		   // TODO error handling
		   socialGroupService.get({id:loc.extId})
		   		.$promise
		   		.then(function(result){
		   			console.log("Get social group by extId=" + loc.extId + " " + result.data);
		   			if ($rootScope.selectedHousehold !== null) {
		   				$rootScope.selectedHousehold = result.data.socialgroup;
		   			}
		   		});
		   
		   if ($rootScope.selectedHousehold != null) {
			   // load individuals for location
			   individualService.getIndividualsForSocialGroup({id:$rootScope.selectedHousehold.extId})
			   	.$promise
			   	.then(function(result) {
			   		$scope.individuals = result.data.individuals;
			   	});
		   };
	   }
	   
	   $scope.disableLocation = function() {
		   return ($scope.selectedHierList.length < $scope.hierarchyLevels.length || $rootScope.selectedLocation == null);
	   };
	   $scope.disableHousehold = function() {
		   return ($scope.selectedHierList.length < $scope.hierarchyLevels.length || $rootScope.selectedHousehold == null);
	   };
	   $scope.showCreateLocationButton = function() {
		   return ($scope.selectedHierList.length >= $scope.hierarchyLevels.length);
	   };
//	   $scope.showCreateHouseholdButton = function() {
//		   return ($scope.selectedHierList.length >= $scope.hierarchyLevels.length && $rootScope.selectedLocation !== null);
//	   };
//	   $scope.showCreateHeadOfHouseholdButton = function() {
//		   return ($rootScope.selectedLocation != null && $rootScope.selectedHousehold != null && $rootScope.selectedHousehold.groupHead == null);
//	   };
	   $scope.showCreateIndividualButton = function() {
		   return ($rootScope.selectedLocation != null); // && $rootScope.selectedHousehold.groupHead != null);
	   };
	   $scope.showCreateRelationshipButton = function() {
		   return false;
	   };
//	   
	  // $scope.clearLocationEnabled = function() {
//		   return ($rootScope.selectedLocation !== null);
//	   };
	   
	   $scope.byParent = function() {
		   return function( item ) {
			   return (item.parent.extId == $scope.parentExtId);
		   };
	   };
	   
	   $scope.selectLevelItem = function(selectedItem) {
		   $scope.selectedHierList.push(selectedItem);
		   $scope.selectedLevel = selectedItem;
		   $scope.parentExtId = selectedItem.extId;
		   
		  
		   // load locations if last level selected
		   if ($scope.selectedHierList.length >= $scope.hierarchyLevels.length) {
			   
			   locationService.getLocationsForLevel({id : $scope.selectedLevel.uuid})
			   		.$promise
			   		.then(function(result) {
			   			$rootScope.locations = result.data.locations;
			   		 });

		   }
	   }
	   
	   $scope.createLocation = function(parentExtId) {
		   console.log("Create location for parentExtId=" + parentExtId);
		   $location.search('parentExtId', parentExtId).path('/form/location');
	   }
	   $scope.createHousehold = function() {
		   $location.path("/form/household");
	   }
	   $scope.createIndividual = function() {
		   $location.path("/form/individual");
	   }
	   
	   $scope.go = function ( path ) {
		   $location.path( path );
	    };

	   var init = function () {
		   locationHierService.get().$promise.then(function (result) {
		       $scope.hierarchyItems = result.data.locationhierarchies;
		   });
		   
		   locationHierService.getLevels().$promise.then(function (result) {
			   $scope.hierarchyLevels = result.data.locationhierarchylevels;
		   });
	   };  
	   
	   init();	   
   }])
  .controller('LocationCtrl', ['$scope', '$rootScope', '$resource', '$location', 'locationService', 'locationHierService', 'openModal', 'RESULT_CODES', 
                               function ($scope, $rootScope, $resource, $location, locationService, locationHierService, openModal, RESULT_CODES) {  
	   
	  $scope.parentLocationHierarchy = $scope.selectedLevel;

        $scope.createLocation = function() {
        	$scope.newLocation.locationHierarchy = $scope.parentLocationHierarchy;
        	
        	locationService.createLocation($scope.newLocation).$promise.then(
        		function(result) {
        			$rootScope.selectedLocation = result.data.location;
        			$rootScope.locations = [];
        			$location.path("/home");
        		},
        		function(error) {
        			var title, items;
        			if (error.status == '400') {
        				title = error.data.resultMessage;
        				if (error.data.resultCode == RESULT_CODES.CONSTAINT_VIOLATIONS_CODE) {
        					items = error.data.data.constraintViolations;
        				}
        			} else {
        				title = 'Error ' + error.status;
        			}
        			
        			openModal(title, items);
        		});
        };
        
 	   $scope.cancelLocation = function(parentExtId) {
 		   $location.path('/home');
	   }
        
       	var init = function() {
       		var parentExtId = ($location.search()).parentExtId;
       		
       		// route back to home
       		if (!parentExtId) {
       			$location.path("/home");
       		}
       		// load parent
       		else if ($scope.parentLocationHierarchy === null || ($scope.parentLocationHierarchy.extId !== ($location.search()).parentExtId)) {
       			locationHierService.get({id:parentExtId})
       				.$promise
       				.then(
       	        		function(result) {
       	        			$scope.parentLocationHierarchy = result.data.locationhierarchy;
       	        		},
       	        		function(error) {
       	        			title = 'Error ' + error.status;
       	        			openModal(title, null);
       	        		});
       		}
        	
        };
        
        init();          
  }])
//  .controller('HouseholdCtrl', ['$scope', '$rootScope', '$resource', '$location', 'socialGroupService', 'openModal', 'RESULT_CODES', 
//                                function ($scope, $rootScope, $resource, $location, socialGroupService, openModal, RESULT_CODES) {  
//	  $scope.createNewHousehold = function() {
//      	socialGroupService.createSocialGroup($scope.newHousehold).$promise.then(
//      		function(result) {
//      			$rootScope.selectedHousehold = result.data.socialgroup;
//      			$location.path("/home");
//      		},
//      		// duplicated from create location
//    		function(error) {
//    			var title, items;
//    			if (error.status == '400') {
//    				title = error.data.resultMessage;
//    				if (error.data.resultCode == RESULT_CODES.CONSTAINT_VIOLATIONS_CODE) {
//    					items = error.data.data.constraintViolations;
//    				}
//    			} else {
//    				title = 'Error ' + error.status;
//    			}
//    			
//    			openModal(title, items);
//    		});
//      };
//      
//	   $scope.cancelNewHousehold = function() {
//		   $location.path("/home");
//	   }
//      
//	  var init = function() {
//     		
//      	
//      };
//      
//      init();          
//  }])
    .controller('IndividualCtrl', ['$scope', '$rootScope', '$resource', '$location', 'individualFormService', 'openModal', function ($scope, $rootScope, $resource, $location, individualFormService, openModal) {  
	 
    	$scope.cancelNewIndividual = function(parentExtId) {
    		$location.path('/home');
		}
	  	   
	    $scope.createNewIndividual = function() {
	    	
    	$scope.newIndividual.individualRelationshipToHeadOfHousehold = ($rootScope.selectedHousehold !== null ? "1" : "0");
    	   
    	
       	$scope.newIndividual.locationHierarchy = $scope.parentLocationHierarchy;
       	
       	individualFormService.createIndividualForm($scope.newIndividual).$promise.then(
       		function(result) {
       			// TODO handle result of creating individual
       			$location.path("/home");
       		},
       		function(error) {
       			var title, items;
       			if (error.status == '400') {
       				title = error.data.resultMessage;
       				if (error.data.resultCode == RESULT_CODES.CONSTAINT_VIOLATIONS_CODE) {
       					items = error.data.data.constraintViolations;
       				}
       			} else {
       				title = 'Error ' + error.status;
       			}
       			
       			openModal(title, items);
       		});
       };
    	
       	var init = function() {
       		if ($rootScope.selectedLocation == null) {
       			$location.path("/home");
       		}
        };
        
        init();          
  }])
    .controller('MarriageCtrl', ['$scope', '$rootScope', '$resource', '$location', 'openModal', function ($scope, $rootScope, $resource, $location, openModal) {  
	   
       	var init = function() {
       		
        	
        };
        
        init();          
  }])
  .controller('ModalInstanceCtrl', ['$scope', '$modalInstance', 'title', 'items', function($scope, $modalInstance, title, items) {
    	 $scope.modalTitle = title;
    	 $scope.modalItems = items;
	  $scope.ok = function () {
	    $modalInstance.close();
	  };
  }])
;


