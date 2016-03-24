var app = angular.module('openhds-ui',['ui.router','ui.router.stateHelper','ngAnimate','ngCookies','ngResource', 'ngStorage',
    'LocationsModule',
    'ExplorerModule',
    'SearchModule',
    'testService',
    'ServicesModule',
    'EditLocationModule',
    'CreateLocationModule',
    'EditIndividualModule'
]);

app.config(['stateHelperProvider','$urlRouterProvider','$urlMatcherFactoryProvider',function(stateHelperProvider,$urlRouterProvider,$urlMatcherFactoryProvider) {

    $urlRouterProvider.otherwise("/");

    $urlMatcherFactoryProvider.strictMode(false)

    stateHelperProvider.state({
        name: "home",
        url: "/",
        templateUrl: "components/home/main.html",
        controller: "MainController"

    }).state({
        name: "explorer",
        url: "/explorer",
        templateUrl: "components/explorer/explorer.html",
        controller: "ExplorerController"

    }).state({
        name: "search",
        url: "/search",
        templateUrl: "components/search/search.html",
        controller: "SearchController"

    }).state({
        name: "searchIndividual",
        url: "/searchIndividual",
        templateUrl: "components/search/searchIndividual.html",
        controller: "SearchIndividualController"

    })
        .state({
            name: "searchLocation",
            url: "/searchLocation",
            templateUrl: "components/search/searchLocation.html",
            controller: "SearchLocationController"

        });

}]);


app.controller('MainController', ['$scope',function($scope){
    $scope.message="is working...";
}]);
