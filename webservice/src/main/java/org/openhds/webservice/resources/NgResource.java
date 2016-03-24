package org.openhds.webservice.resources;

/**
 * Created by R.Miller on 3/24/16.
 *
 * This class is a placeholder that performs given REST operations for the AngularJS Web UI
 *
 * TODO: break this up into different classes, similar to the model used in this folder
 */



        import org.openhds.controller.exception.ConstraintViolations;
        import org.openhds.controller.service.CurrentUser;
        import org.openhds.controller.service.refactor.FieldWorkerService;
        import org.openhds.controller.service.LocationHierarchyService;
        import org.openhds.controller.service.ResidencyService;
        import org.openhds.controller.service.IndividualService;
        import org.openhds.domain.model.*;
        import org.openhds.domain.model.LocationHierarchy.LocationHierarchies;
        import org.openhds.domain.model.LocationHierarchyLevel;
        import org.openhds.domain.util.ShallowCopier;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.PathVariable;
        import org.springframework.web.bind.annotation.RequestBody;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RequestMethod;
        import org.springframework.web.bind.annotation.ResponseBody;





        import java.util.ArrayList;
        import java.util.List;



@Controller
@RequestMapping("/ng")
public class NgResource{

    private final LocationHierarchyService locationHierarchyService;
    private final IndividualService individualService;
    private final ResidencyService residencyService;
    private final FieldWorkerService fieldWorkerService;




    @Autowired
    public NgResource(IndividualService individualService, ResidencyService residencyService,
                              LocationHierarchyService locationHierarchyService, FieldWorkerService fieldWorkerService, CurrentUser currentUser) {
        this.individualService = individualService;
        this.residencyService = residencyService;
        this.locationHierarchyService = locationHierarchyService;
        this.fieldWorkerService = fieldWorkerService;

    }

    @RequestMapping(value ="/locations",method = RequestMethod.GET, produces="application/json")
    @ResponseBody
    public ResponseEntity<List<Location>> getAllLocationsJson() {
        List<Location> locations = locationHierarchyService.getAllLocations();
        List<Location> copies = new ArrayList<>(locations.size());

        for (Location loc : locations) {
            Location copy = ShallowCopier.makeShallowCopy(loc);
            copies.add(copy);
        }
        return new ResponseEntity<List<Location>>(copies, HttpStatus.OK);

    }

    @RequestMapping(value = "/locations/{extId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Location getLocationByExtIdJson(@PathVariable String extId) {
        Location location = locationHierarchyService.findLocationById(extId);
        Location copy = ShallowCopier.makeShallowCopy(location);
        return copy;
    }

    @RequestMapping(value = "/locExist/{extId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<LocationStub> getLocationExist(@PathVariable String extId) {
        Location location = locationHierarchyService.findLocationById(extId);
        LocationStub locStub = new LocationStub();
        locStub.setExtID(extId);
        if (location == null){
            locStub = null;
            return new ResponseEntity<>(locStub, HttpStatus.OK);
        }
        else{
            locStub.setLocationName("Does exist");
            return new ResponseEntity<>(locStub, HttpStatus.OK);
        }
    }









    @RequestMapping(value = "/createLocation",method= RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseEntity<Location> createLocation(@RequestBody LocationStub locationStub) throws ConstraintViolations {

        // Map data fields
        //---------------
        Location loc = locationHierarchyService.locationStubToLocation(locationStub);

        // Map objects
        //------------

        // Field Worker / CollectedBy
        FieldWorker fw = fieldWorkerService.getByUuid("4786a2b0c59d11e39c1a0800200c9a66"); // (locationStub.getCollectedBy())
        if (fw != null){
            loc.setCollectedBy(fw);
        }

        // Location Hierarchy
        LocationHierarchy lh = locationHierarchyService.findByExtId("3 AgriferM1497S100"); // locationStub.getLocationHierarchy()
        if (lh != null){
            loc.setLocationHierarchy(lh);
        }

        // User / InsertBy
		/*
		org.openhds.domain.model.User user = currentUser.getCurrentUser();

		if ( user != null){
			loc.setInsertBy(user);
		}*/

        locationHierarchyService.createLocation(loc);


        // TODO : Change to not return location? Should only need the response
        Location copy = ShallowCopier.makeShallowCopy(loc);
        return new ResponseEntity<>(copy , HttpStatus.CREATED);

    }







    @RequestMapping(value = "/test",method= RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<LocationStub> updateLoc() {

        LocationStub locationStub = new LocationStub();
        locationStub.setExtID("extIdFromTest");
        locationStub.setLocationName("Mark's House");

        return new ResponseEntity<>(locationStub, HttpStatus.OK);

    }


    @RequestMapping(value = "/updateLocation",method= RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> getLocation(@RequestBody LocationStub locationStub) throws ConstraintViolations {
        Location temp = locationHierarchyService.findLocationById(locationStub.getExtId());
        temp.setLocationName(locationStub.getLocationName());
        locationHierarchyService.updateLocation(temp);

        locationStub.setLocationName(temp.getLocationName());


        return new ResponseEntity<String>("update successful", HttpStatus.OK);

    }

    @RequestMapping(value= "setLocationDeleted", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> setLocationDeleted(@RequestBody LocationStub locationStub) throws ConstraintViolations{
        Location temp = locationHierarchyService.findLocationById(locationStub.getExtId());
        if (temp == null){
            return new ResponseEntity<String>("location could not be found", HttpStatus.OK);
        }
        temp.setDeleted(true);



        return new ResponseEntity<String>("location deleted", HttpStatus.OK);
    }



    @RequestMapping(value= "/locationHierarchies", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<LocationHierarchy>> getLocationHierarchyJson() {

        List<LocationHierarchy> allLocationHierarcies = locationHierarchyService.getAllLocationHierarchies();
        List<LocationHierarchy> copies = new ArrayList<LocationHierarchy>();

        for (LocationHierarchy lh : allLocationHierarcies) {
            LocationHierarchy copy = new LocationHierarchy();
            copy.setExtId(lh.getExtId());

            LocationHierarchyLevel level = new LocationHierarchyLevel();
            level.setName(lh.getLevel().getName());
            copy.setUuid(lh.getUuid());
            copy.setLevel(level);
            copy.setName(lh.getName());

            LocationHierarchy parent = new LocationHierarchy();
            parent.setExtId(lh.getParent().getExtId());
            parent.setUuid(lh.getParent().getUuid());
            copy.setParent(parent);

            copies.add(copy);
        }

        LocationHierarchies locationHierarchies = new LocationHierarchy.LocationHierarchies();
        locationHierarchies.setLocationHierarchies(copies);

        return new ResponseEntity<List<LocationHierarchy>>(copies, HttpStatus.OK);
    }

    @RequestMapping(value= "/locationHierarchies/{extId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<Location>> getLocationsForLocationHierarchy(@PathVariable String extId) {

        LocationHierarchy temp = locationHierarchyService.findByExtId(extId);

        List<Location> locationList = locationHierarchyService.getLocationsForLH(temp);
        List<Location> copies = new ArrayList<>(locationList.size());

        for (Location loc : locationList) {
            Location copy = ShallowCopier.makeShallowCopy(loc);
            copies.add(copy);
        }

        return new ResponseEntity<List<Location>>(copies, HttpStatus.OK);
    }


    @RequestMapping(value = "/residencies", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<Residency>> getAllResidenciesJson() {
        int count = (int) residencyService.getTotalResidencyCount();
        List<Residency> residencies = residencyService.getAllResidenciesInRange(null, count);
        List<Residency> copies = new ArrayList<>(residencies.size());

        for (Residency r : residencies) {
            Residency copy = ShallowCopier.makeShallowCopy(r);
            copies.add(copy);
        }

        return new ResponseEntity<List<Residency>>(copies, HttpStatus.OK);
    }


    @RequestMapping(value ="/individuals", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<Individual>> getAllIndividualsJson() {
        List<Individual> allIndividual = individualService.getAllIndividuals();
        List<Individual> copies = new ArrayList<>(allIndividual.size());
        for (Individual individual : allIndividual) {
            Individual copy = ShallowCopier.makeShallowCopy(individual);
            copies.add(copy);
        }

        return new ResponseEntity<List<Individual>>(copies, HttpStatus.OK);
    }


    @RequestMapping(value="/hierarchyLevels", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<LocationHierarchyLevel>> getHierarchyLevels(){
        List<LocationHierarchyLevel> levels =locationHierarchyService.getAllLevels();

        return new ResponseEntity<>(levels, HttpStatus.OK);
    }

}





