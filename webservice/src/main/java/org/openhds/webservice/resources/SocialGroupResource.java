package org.openhds.webservice.resources;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.service.SocialGroupService;
import org.openhds.domain.model.SocialGroup;
import org.openhds.domain.model.SocialGroup.SocialGroups;
import org.openhds.domain.util.ShallowCopier;
import org.openhds.task.support.FileResolver;
import org.openhds.webservice.CacheResponseWriter;
import org.openhds.webservice.FieldBuilder;
import org.openhds.webservice.WebServiceCallException;
import org.openhds.webservice.response.WebserviceResult;
import org.openhds.webservice.response.constants.ResultCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/socialgroups")
public class SocialGroupResource {
    private static final Logger logger = LoggerFactory.getLogger(SocialGroupResource.class);

    private SocialGroupService socialGroupService;
    private FieldBuilder fieldBuilder;
    private FileResolver fileResolver;

    @Autowired
    public SocialGroupResource(SocialGroupService socialGroupService, FieldBuilder fieldBuilder,
            FileResolver fileResolver) {
        this.socialGroupService = socialGroupService;
        this.fieldBuilder = fieldBuilder;
        this.fileResolver = fileResolver;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public SocialGroups getAllSocialGroups() {
        List<SocialGroup> allSocialGroups = socialGroupService.getAllSocialGroups();
        List<SocialGroup> copies = new ArrayList<SocialGroup>();

        for (SocialGroup sg : allSocialGroups) {
            SocialGroup copy = ShallowCopier.shallowCopySocialGroup(sg);
            copies.add(copy);
        }

        SocialGroups sgs = new SocialGroup.SocialGroups();
        sgs.setSocialGroups(copies);

        return sgs;
    }
    
    @RequestMapping(value = "/{extId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends Serializable> getSocialGroupByExtIdJson(@PathVariable String extId) throws Exception {
    	
    	WebserviceResult result = new WebserviceResult();
    
    	SocialGroup socialGroup = socialGroupService.findSocialGroupById(extId, "Unable to find social group with extId=" + extId);
        if (socialGroup == null) {
        	result.setResultCode(ResultCodes.ENTITY_NOT_FOUND_CODE);
        	result.setStatus(ResultCodes.FAILURE);
        	result.setResultMessage("Unable to find social group with extId=" + extId);
            return new ResponseEntity<WebserviceResult>(result, HttpStatus.NOT_FOUND);
        }

        result.addDataElement("socialgroup", ShallowCopier.shallowCopySocialGroup(socialGroup));
        result.setResultCode(ResultCodes.SUCCESS_CODE);
        result.setStatus(ResultCodes.SUCCESS);
        result.setResultMessage("Social group was found");
        
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<? extends Serializable> insert(@RequestBody SocialGroup socialGroup) {
        ConstraintViolations cv = new ConstraintViolations();

        socialGroup.setCollectedBy(fieldBuilder.referenceField(socialGroup.getCollectedBy(), cv));
        socialGroup.setGroupHead(fieldBuilder.referenceField(socialGroup.getGroupHead(), cv,
                "Invalid Ext Id for Group Head"));

        if (cv.hasViolations()) {
            return new ResponseEntity<WebServiceCallException>(new WebServiceCallException(cv), HttpStatus.BAD_REQUEST);
        }

        try {
            socialGroupService.createSocialGroup(socialGroup);
        } catch (ConstraintViolations e) {
            return new ResponseEntity<WebServiceCallException>(new WebServiceCallException(e), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<SocialGroup>(ShallowCopier.shallowCopySocialGroup(socialGroup), HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends Serializable> createSocialGroupJson(@RequestBody SocialGroup socialGroup) {
        ConstraintViolations cv = new ConstraintViolations();

        socialGroup.setCollectedBy(fieldBuilder.referenceField(socialGroup.getCollectedBy(), cv));
        socialGroup.setGroupHead(fieldBuilder.referenceField(socialGroup.getGroupHead(), cv,
                "Invalid Ext Id for Group Head"));

        if (cv.hasViolations()) {
            return new ResponseEntity<WebServiceCallException>(new WebServiceCallException(cv), HttpStatus.BAD_REQUEST);
        }

        try {
            socialGroupService.createSocialGroup(socialGroup);
        } catch (ConstraintViolations e) {
            return new ResponseEntity<WebServiceCallException>(new WebServiceCallException(e), HttpStatus.BAD_REQUEST);
        }

        WebserviceResult result = new WebserviceResult();
        result.addDataElement("socialgroup", ShallowCopier.shallowCopySocialGroup(socialGroup));
        result.setResultCode(ResultCodes.SUCCESS_CODE);
        result.setStatus(ResultCodes.SUCCESS);
        result.setResultMessage("Social Group created");
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/cached", method = RequestMethod.GET)
    public void getCachedSocialGroups(HttpServletResponse response) {
        try {
            CacheResponseWriter.writeResponse(fileResolver.resolvesocialGroupXmlFile(), response);
        } catch (IOException e) {
            logger.error("Problem writing social group xml file: " + e.getMessage());
        }
    }

}
