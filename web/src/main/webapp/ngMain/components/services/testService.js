angular.module('testService', [])


    .factory('greeting', function(){
        var greet = "message from test service"

        return greet;
    })

;