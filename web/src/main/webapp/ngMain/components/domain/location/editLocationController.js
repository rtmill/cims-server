angular.module('EditLocationModule',[
        'ServicesModule',
        'ExplorerModule'


    ]
)

    .controller('EditLocationController',[ 
          '$scope',
          'UpdateLocation',
           function($scope, UpdateLocation){
        	  
        	  function saveLocation(){
        		  UpdateLocation.update({extId:$scope.currentLocation.extId,locationName:$scope.currentLocation.locationName});
        	  }
        	  
        	  $scope.saveLocation = saveLocation;
        	  
        	  
        	  
        }])

;
