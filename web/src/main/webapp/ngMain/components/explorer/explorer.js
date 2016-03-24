angular.module('ExplorerModule',[
    'ServicesModule'

    ]
)
    .controller('ExplorerController', [
        '$scope',
        'HierarchyData',
        'GetHierarchyLocations',
        'LocationData',
        'ResidencyData',
        'IndividualData',

        function($scope, HierarchyData, GetHierarchyLocations, LocationData, ResidencyData, IndividualData){
            $scope.locHierarchies = HierarchyData.query();
            $scope.locationData = LocationData.query();
            $scope.residencyData = ResidencyData.query();
            $scope.individualData = IndividualData.query();
            $scope.currentLocations = null;

            // --





            
            $scope.currentRegion = null;
            $scope.currentProvince = null;
            $scope.currentDistrict = null; 
            $scope.currentSubdistrict = null; 
            $scope.currentLocality = null; 
            $scope.currentMapArea = null;
            $scope.currentSector = null; 
            $scope.currentLocationHierarchy = null;

            
            function setCurrentRegion(locationHierarchy){
            	$scope.currentRegion = locationHierarchy;
             	$scope.currentProvince = null;
             	$scope.currentDistrict = null;
             	$scope.currentSubdistrict = null;
             	$scope.currentLocality = null;
             	$scope.currentMapArea = null;
            	$scope.currentSector = null; 
             	$scope.currentLocationHieararchy = null;
            }
            
            function setCurrentProvince(locationHierarchy){
            	$scope.currentProvince = locationHierarchy;
            	$scope.currentDistrict = null;
            	$scope.currentSubdistrict = null;
             	$scope.currentLocality = null;
             	$scope.currentMapArea = null;
            	$scope.currentSector = null; 
             	$scope.currentLocationHieararchy = null;
            }
            
            function setCurrentDistrict(locationHierarchy){
            	$scope.currentDistrict = locationHierarchy;
            	$scope.currentSubdistrict = null;
             	$scope.currentLocality = null;
             	$scope.currentMapArea = null;
            	$scope.currentSector = null; 
             	$scope.currentLocationHieararchy = null;
            }
            
            function setCurrentSubdistrict(locationHierarchy){
            	$scope.currentSubdistrict = locationHierarchy;
            	$scope.currentLocality = null; 
             	$scope.currentMapArea = null;
            	$scope.currentSector = null; 
             	$scope.currentLocationHieararchy = null;
            }

            function setCurrentLocality(locationHierarchy){
            	$scope.currentLocality = locationHierarchy;
             	$scope.currentMapArea = null;
            	$scope.currentSector = null; 
             	$scope.currentLocationHieararchy = null;
            }
            
            function setCurrentMapArea(locationHierarchy){
            	$scope.currentMapArea = locationHierarchy;
            	$scope.currentLocationHiearchy = null; 
            	$scope.currentSector = null; 
            }
            
            function setCurrentSector(locationHierarchy){
            	$scope.currentSector = locationHierarchy;
            	$scope.currentLocations = GetHierarchyLocations.query({extId:locationHierarchy.extId});
            }

    
            
            function isCurrentRegionNotNull(){
                return $scope.currentRegion;
            }
            
            function isCurrentProvinceNotNull(){
                return $scope.currentProvince;
            }
            
            function isCurrentDistrictNotNull(){
            	return $scope.currentDistrict;
            }
            
            function isCurrentSubdistrictNotNull(){
            	return $scope.currentSubdistrict;
            }

            function isCurrentLocalityNotNull(){
                return $scope.currentLocality;
            }
            
            function isCurrentMapAreaNotNull(){
                return $scope.currentMapArea;
            }
            
            function isCurrentSectorNotNull(){
                return $scope.currentSector;
            }
            
  

            
            $scope.setCurrentRegion = setCurrentRegion;
            $scope.setCurrentProvince = setCurrentProvince;
            $scope.setCurrentDistrict = setCurrentDistrict; 
            $scope.setCurrentSubdistrict = setCurrentSubdistrict; 
            $scope.setCurrentLocality = setCurrentLocality;
            $scope.setCurrentMapArea = setCurrentMapArea;
            $scope.setCurrentSector = setCurrentSector;
                       
            $scope.isCurrentRegionNotNull = isCurrentRegionNotNull;
            $scope.isCurrentProvinceNotNull = isCurrentProvinceNotNull;
            $scope.isCurrentDistrictNotNull = isCurrentDistrictNotNull;
            $scope.isCurrentSubdistrictNotNull = isCurrentSubdistrictNotNull;
            $scope.isCurrentLocalityNotNull = isCurrentLocalityNotNull;
            $scope.isCurrentMapAreaNotNull = isCurrentMapAreaNotNull;
            $scope.isCurrentSectorNotNull = isCurrentSectorNotNull;

            
            
            
            
            // Location
            
            
            $scope.currentLocation = null;

            function setCurrentLocation(location){
                $scope.currentLocation = location;
            }

            function isCurrentLocation(location){
                return $scope.currentLocation !== null && location.uuid === $scope.currentLocation.uuid;
            }

            function isCurrentLocationNotNull(){
                return $scope.currentLocation;
            }

            $scope.setCurrentLocation = setCurrentLocation;
            $scope.isCurrentLocation = isCurrentLocation;
            $scope.isCurrentLocationNotNull = isCurrentLocationNotNull;
            

            $scope.currentIndividual = null;

            function setCurrentIndividual(individual){
                $scope.currentIndividual = individual;
            }

            function isCurrentIndividualNotNull(){
                return $scope.currentIndividual;
            }

            $scope.setCurrentIndividual = setCurrentIndividual;
            $scope.isCurrentIndividualNotNull = isCurrentIndividualNotNull;

            $scope.newExtId = "a";

            function setNewExtId(mapArea, sector){
                $scope.newExtId = mapArea.name + sector.name;
            }

            $scope.setNewExtId = setNewExtId;



            $scope.newLocation = null;

            function setNewLocation(){
                $scope.newLocation = {
                    region : $scope.currentRegion,
                    province: $scope.currentProvince,
                    district: $scope.currentDistrict,
                    subdistrict: $scope.currentSubdistrict,
                    locality : $scope.currentLocality,
                    mapArea : $scope.currentMapArea,
                    sector : $scope.currentSector,
                    building : "",
                    floor : ""
                 }

            }

            $scope.setNewLocation = setNewLocation;


            



    }])
;

