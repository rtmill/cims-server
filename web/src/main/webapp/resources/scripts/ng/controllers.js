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
	   
	   // TODO use factory to share data between controllers and not rootScope
	   // selected objects used in multiple controllers
	   $rootScope.selectedLocation = null;
	   $rootScope.selectedHousehold = null;
	   $rootScope.selectedIndividual = null;
	   
	   $rootScope.locations = [];
	   $rootScope.households = [];
	   $rootScope.individuals = [];
	   
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
		   return index >= $scope.selectedHierList.length;
	   };
	   $scope.levelSelected = function(index) {
		   // TODO move to reset function
		   // reset child types
		   $rootScope.selectedLocation = null;
		   $rootScope.locations = [];
		   $rootScope.selectedHousehold = null;
		   $rootScope.households = [];
		   $rootScope.individuals = [];
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
		   	$rootScope.selectedLocation = null;
	   		$rootScope.locations = [];
		   	$rootScope.individuals = [];
		   	$rootScope.selectedHousehold = null;
		   	locationService.getLocationsForLevel({id : $scope.selectedLevel.uuid})
	   			.$promise
	   			.then(function(result) {
	   				$rootScope.locations = result.data.locations;
	   			});
	   }
	   $scope.selectLocationItem = function(loc) {
		   $rootScope.selectedLocation = loc;
		   $rootScope.locations = [];
		   $rootScope.individuals = [];
		   $rootScope.selectedHousehold = null;
		   
		   // load social group with same extId as location
		   // if there is no social group found then allow for creation of individual and social group
		   // otherwise show list of individuals in social group and allow them to be edited or a new individual created
		   
		   // trying to load social group
		   // TODO error handling
		   socialGroupService.get({id:loc.extId})
		   		.$promise
		   		.then(function(result){
		   			console.log("Get social group by extId=" + loc.extId + " " + result.data);
		   			if (result.data.socialgroup != null) {
		   				$rootScope.selectedHousehold = result.data.socialgroup;
		   				
		   			   var householdExtId = $rootScope.selectedHousehold.extId;
		 			   console.log("Loading individuals for social group with extId=" + householdExtId);
		 			   // load individuals for location
		 			   individualService.getIndividualsForSocialGroup({id:householdExtId})
		 			   	.$promise
		 			   	.then(function(result) {
		 			   		$rootScope.individuals = result.data.individuals;
		 			   	});
		   			}
		   		});
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
	   $scope.showCreateIndividualButton = function() {
		   return ($rootScope.selectedLocation != null);
	   };
	   $scope.showCreateRelationshipButton = function() {
		   return false;
	   };

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

	   // TODO change into service to get display strings for creating individual form
	   // possibly get values from server
	   $scope.getIndRelationshipToHead = function(ind) {
		   var display = "Undefiined";
		  
		   if (ind != null && ind.allMemberships[0] != null && ind.allMemberships[0].bIsToA != null) {
			   switch(ind.allMemberships[0].bIsToA) {
			    case "1":
			        display = "Head";
			        break;
			    case "2":
			        display = "Spouse";
			        break;
			    case "3":
			        display = "Son/Daughter";
			        break;
			    case "4":
			        display = "Brother/Sister";
			        break;
			    case "5":
			        display = "Parent";
			        break;
			    case "6":
			        display = "Grandchild";
			        break;
			    case "7":
			        display = "Not Relaed";
			        break;
			    case "8":
			        display = "Other Relative";
			        break;
			    case "9":
			        display = "Don't Know";
			        break;
			    case "10":
			        display = "Cousin";
			        break;
			    case "11":
			        display = "Nephew/Niece";
			        break;
			    default:
			    	display = "N/A";
			    	break;
			}
		   }
		   
		   return display;
	   } 
	   
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
        			$rootScope.selectedHousehold = null;
        			$rootScope.individuals = [];
        			$location.path("/home");
        		},
        		function(error) {
        			var title, items;
        			if (error.status == '400') {
        				title = error.data.resultMessage;
        				if (error.data.resultCode == RESULT_CODES.CONSTRAINT_VIOLATIONS_CODE) {
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
    .controller('IndividualCtrl', ['$scope', '$rootScope', '$resource', '$location', 'individualFormService', 'individualService', 'RESULT_CODES', 'openModal', 
                                   function ($scope, $rootScope, $resource, $location, individualFormService, individualService, RESULT_CODES, openModal) {  
	 
       	var init = function() {
       		if ($scope.selectedLocation == null) {
       			$location.path("/home");
       		}
        };
        
        init(); 
    	
    	// start date picker
    	 $scope.today = function() {
    		    $scope.dt = new Date();
		  };
		  $scope.today();

		  $scope.clear = function () {
		    $scope.dt = null;
		  };

		  $scope.setDOB = function() {
			  
			  // set DOB if it is not set and both age and age units have values
			  if ($scope.newIndividual.individualAge !== undefined &&
					  $scope.newIndividual.individualAgeUnits !== undefined &&  
					  $scope.newIndividual.individualDateOfBirth === undefined) {
				  
				  var age = $scope.newIndividual.individualAge;
				  var units = $scope.newIndividual.individualAgeUnits;
				  
				  // set DOB using values
				  var newDOB = new Date();
				  if (units === 'Months') {
					  newDOB = newDOB.setMonth(newDOB.getMonth() - age);
				  } else if (units === 'Years') {
					  newDOB = newDOB.setFullYear(newDOB.getFullYear() - age);
				  }
				  
				  $scope.newIndividual.individualDateOfBirth = new Date(newDOB);
			  }
		  }
		  
		  // set minimum date of birth to 110 years before today
		  var date = new Date();
		  $scope.minDate = date.setFullYear(date.getFullYear() - 110);
		  
		  // max date is today
		  $scope.maxDate = new Date();
		  
		  $scope.disabled = function(date, mode) {
			// don't disable any dates between min and max
			return false;
		  };

		  $scope.open = function($event) {
		    $event.preventDefault();
		    $event.stopPropagation();

		    $scope.opened = true;
		  };

		  $scope.dateOptions = {
		    formatYear: 'yy',
		    startingDay: 1
		  };
    	// end date picker
    	
    	$scope.cancelNewIndividual = function(parentExtId) {
    		$location.path('/home');
		}
	  	
    	$scope.parentLocationHierarchy = $scope.selectedLevel;
    	
    	$scope.newIndividual = {
    			collectionDateTime: null,
    			householdExtId: null
    	};
    	
    	if ($scope.selectedLocation != null) {
    		 $scope.newIndividual.householdExtId = $scope.selectedLocation.extId
    	}
    	
	    $scope.createNewIndividual = function() {
	    	$scope.newIndividual.collectionDateTime = new Date();
	    	
	    	individualFormService.createIndividualForm($scope.newIndividual).$promise.then(
	       		function(result) {
	       			
	       			// TODO load new household created when head of household is created
	       			$rootScope.selectedHousehold = {
	       					extId :  $scope.selectedLocation.extId,
	       					groupName :  $scope.selectedLocation.locationName
	       			}
	       			
	       			// get all individuals for selected location/household
	       			individualService.getIndividualsForSocialGroup({id:$scope.selectedLocation.extId})
	 			   	.$promise
	 			   	.then(function(result) {
	 			   		$rootScope.individuals = result.data.individuals;
	 			   	});
	       			
	       			$location.path("/home");
	       		},
	       		function(error) {
	       			var title, items;
	       			if (error.status == '400') {
	       				if (error.data.resultCode == RESULT_CODES.CONSTRAINT_VIOLATIONS_CODE) {
	       					title = error.data.resultMessage;
	       					items = error.data.data.constraintViolations;
	       				} else {
	       					title = "Server error processing individual form";
	       				}
	       			} else {
	       				title = "Server error processing individual form";
	       			}
	       			
	       			openModal(title, items);
	       	});
       };         
  }])
  .controller('ModalInstanceCtrl', ['$scope', '$modalInstance', 'title', 'items', function($scope, $modalInstance, title, items) {
    	 $scope.modalTitle = title;
    	 $scope.modalItems = items;
	  $scope.ok = function () {
	    $modalInstance.close();
	  };
  }])
;


