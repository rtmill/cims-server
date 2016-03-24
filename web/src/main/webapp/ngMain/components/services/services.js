angular.module('ServicesModule',[
    'ngResource'


    ])
  .factory('HierarchyData', ['$resource',
        function($resource){
	  		return $resource('http://localhost:8080/openhds/api/rest/ng/locationHierarchies', {}, {
          query: {
              method: 'GET',
              format: '.json',
              isArray: true,
              withCredentials: true
            }
      })
  }])
  
   .factory('GetHierarchyLocations', ['$resource',
        function($resource){
	  		return $resource('http://localhost:8080/openhds/api/rest/ng/locationHierarchies/:extId', {}, {
          query: {
              method: 'GET',
              format: '.json',
              isArray: true,
              withCredentials: true
            }
      })
  }])

    .factory('LocationData', ['$resource',
        function($resource){
            return $resource('http://localhost:8080/openhds/api/rest/ng/locations', {}, {
                query: {
                    method: 'GET',
                    format: '.json',
                    isArray: true,
                    withCredentials: true
                  }
            })
        }])

      .factory('ResidencyData', ['$resource',
        function($resource){
            return $resource('http://localhost:8080/openhds/api/rest/ng/residencies', {}, {
                query: {
                    method: 'GET',
                    format: '.json',
                    isArray: true,
                    withCredentials: true
                  }
            })
        }])
        
        .factory('IndividualData', ['$resource',
        function($resource){
            return $resource('http://localhost:8080/openhds/api/rest/ng/individuals', {}, {
                query: {
                    method: 'GET',
                    format: '.json',
                    isArray: true,
                    withCredentials: true
                  }
            })
        }])
        
         .factory('UpdateLocation', ['$resource',
        function($resource){
            return $resource('http://localhost:8080/openhds/api/rest/ng/updateLocation', {}, {
                update: {
                    method: 'POST',
                    format: '.json',
                    isArray: true,
                    withCredentials: true
                }
            })
        }])

      .factory('TestResponse', ['$resource',
        function($resource){
            return $resource('http://localhost:8080/openhds/api/rest/ng/locExist/M1497S100E01P1', {}, {
                query: {
                    method: 'GET',
                    isArray: true,
                    withCredentials: true
                }
            })
        }])

    .factory('CreateLocation', ['$resource',
        function($resource){
            return $resource('http://localhost:8080/openhds/api/rest/ng/createLocation', {}, {
                update: {
                    method: 'POST',
                    format: '.json',
                    isArray: true,
                    withCredentials: true
                }
            })
        }])

    .factory('HierarchyLevels', ['$resource',
        function($resource){
            return $resource('http://localhost:8080/openhds/api/rest/ng/hierarchyLevels', {}, {
                query: {
                    method: 'GET',
                    format: '.json',
                    isArray: true,
                    withCredentials: true
                }
            })
        }])
;
;