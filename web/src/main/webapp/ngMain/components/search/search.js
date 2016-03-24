/**
 * Created by motech on 3/7/16.
 */
angular.module('SearchModule',[
    'ServicesModule'
])
    .controller('SearchController',[
        '$scope',
        function($scope){
            $scope.tempVariable = "working";

        }
    ])

    .controller('SearchIndividualController',[
        '$scope',
        'IndividualData',
        function($scope, IndividualData){
            $scope.individualData = IndividualData.query();
            $scope.tempVariable = "indiv working";


            $scope.searchOption = {
                searchByValue : null,
                options : [
                    {id : 1, name : "First Name"},
                    {id : 2, name : "Last Name "},
                    {id : 3, name : "Birth Date"}
                ]
            }






        }
    ])

    .controller('SearchLocationController', [
        '$scope',
        'HierarchyLevels',
        'HierarchyData',
        function($scope, HierarchyLevels, HierarchyData){

            $scope.levels = HierarchyLevels.query();
            $scope.hierarchies = HierarchyData.query();
            $scope.tempVar = "Location Search Controller Working";

            $scope.mySelection= null;

            $scope.levelChoice = 0;

            function showCurrentLevel(keyId){
                return keyId <= levelChoice + 1;
            }

            $scope.showCurrentLevel = showCurrentLevel;

            $scope.tempLevel = null;
            $scope.tempHier = null;
            $scope.selected = [];


            $scope.selectHier = function(hier){
               // $scope.selected[hier.level.name] = hier.name;
                $scope.selected[hier.level.name] = hier;
            }


        }
    ])



;