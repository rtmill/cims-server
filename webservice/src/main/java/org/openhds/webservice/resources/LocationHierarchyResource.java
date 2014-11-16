package org.openhds.webservice.resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openhds.controller.service.LocationHierarchyService;
import org.openhds.domain.model.LocationHierarchy;
import org.openhds.domain.model.LocationHierarchy.LocationHierarchies;
import org.openhds.domain.model.LocationHierarchyLevel;
import org.openhds.webservice.response.WebserviceResult;
import org.openhds.webservice.response.constants.ResultCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/locationhierarchies")
public class LocationHierarchyResource {

	private LocationHierarchyService locationHierarchyService;

	@Autowired
	public LocationHierarchyResource(LocationHierarchyService locationHierarchyService) {
		this.locationHierarchyService = locationHierarchyService;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public LocationHierarchies getEntireLocationHierarchy() {
		List<LocationHierarchy> allLocationHierarcies = locationHierarchyService.getAllLocationHierarchies();
		List<LocationHierarchy> copies = new ArrayList<LocationHierarchy>();
		
		for (LocationHierarchy lh : allLocationHierarcies) {
			LocationHierarchy copy = new LocationHierarchy();
			copy.setExtId(lh.getExtId());
			copy.setUuid(lh.getUuid());

			LocationHierarchyLevel level = new LocationHierarchyLevel();
			level.setName(lh.getLevel().getName());
			copy.setLevel(level);
			copy.setName(lh.getName());

			LocationHierarchy parent = new LocationHierarchy();
			parent.setExtId(lh.getParent().getExtId());
			copy.setParent(parent);

			copies.add(copy);
		}

		LocationHierarchies locationHierarchies = new LocationHierarchy.LocationHierarchies();
        locationHierarchies.setLocationHierarchies(copies);
		
		return locationHierarchies;
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends Serializable> getEntireLocationHierarchyJson() {
		List<LocationHierarchy> allLocationHierarcies = locationHierarchyService.getAllLocationHierarchies();
		List<LocationHierarchy> copies = new ArrayList<LocationHierarchy>();

		for (LocationHierarchy lh : allLocationHierarcies) {
			LocationHierarchy copy = new LocationHierarchy();
			copy.setExtId(lh.getExtId());
			copy.setUuid(lh.getUuid());

			LocationHierarchyLevel level = new LocationHierarchyLevel();
			level.setName(lh.getLevel().getName());
			copy.setLevel(level);
			copy.setName(lh.getName());

			LocationHierarchy parent = new LocationHierarchy();
			parent.setExtId(lh.getParent().getExtId());
			copy.setParent(parent);

			copies.add(copy);
		}

		WebserviceResult result = new WebserviceResult();
		result.addDataElement("locationhierarchies", copies);
		result.setResultCode(ResultCodes.SUCCESS_CODE);
		result.setStatus(ResultCodes.SUCCESS);
		result.setResultMessage(copies.size() + " location hierarchies found");

		return new ResponseEntity<WebserviceResult>(result, HttpStatus.OK);
	}
	
    @RequestMapping(value = "/{extId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends Serializable> getLocationHierarchyByExtId(@PathVariable String extId) {
        LocationHierarchy locationHierarchy = locationHierarchyService.findLocationHierarchyById(extId);
        
        // TODO: change to not found response
        if (locationHierarchy == null) {
            return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
        }

        WebserviceResult result = new WebserviceResult();
		result.addDataElement("locationhierarchy", locationHierarchy);
		result.setResultCode(ResultCodes.SUCCESS_CODE);
		result.setStatus(ResultCodes.SUCCESS);
		result.setResultMessage("Location hierarchy found");
        
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.OK);
    }
	
	@RequestMapping(value = "/levels", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<? extends Serializable> getAllLevels() {
		List<LocationHierarchyLevel> levels = locationHierarchyService.getAllLevels();
		
		WebserviceResult result = new WebserviceResult();
		result.addDataElement("locationhierarchylevels", levels);
		result.setResultCode(ResultCodes.SUCCESS_CODE);
		result.setStatus(ResultCodes.SUCCESS);
		result.setResultMessage(levels.size() + " location hierarchy levels found");
		
		return new ResponseEntity<WebserviceResult>(result, HttpStatus.OK);
	}

}
