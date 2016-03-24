/**
 * Created by motech on 2/29/16.
 */
angular.module('CreateLocationModule',[
        'ServicesModule',
        'ExplorerModule'


    ]
)
    .controller('CreateLocationController', [
            '$scope',
            'CreateLocation',
            function($scope, CreateLocation){


                // Doesn't work... why?
                $scope.responseString = null;

                function createLocation(){
                   // $scope.responseString =  CreateLocation.update(newLocation);
                }

                $scope.createLocation = createLocation;

                function checkResponse(){
                    return $scope.responseString;
                }

                $scope.checkResponse = checkResponse;




            }
        ])
;
