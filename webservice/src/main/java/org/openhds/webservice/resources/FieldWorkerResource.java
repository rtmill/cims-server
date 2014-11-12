package org.openhds.webservice.resources;

import java.util.ArrayList;
import java.util.List;

import org.openhds.controller.service.FieldWorkerService;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.FieldWorker.FieldWorkers;
import org.openhds.domain.util.ShallowCopier;
import org.openhds.webservice.response.WebserviceResult;
import org.openhds.webservice.response.constants.ResultCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/fieldworkers")
public class FieldWorkerResource {

    private FieldWorkerService fieldWorkerService;

    @Autowired
    public FieldWorkerResource(FieldWorkerService fieldWorkerService) {
        this.fieldWorkerService = fieldWorkerService;
    }

    /**
     * Get all field workers in xml format
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/xml")
    @ResponseBody
    public FieldWorkers getAllFieldWorkersXml() {
    	List<FieldWorker> copies = getAllFieldWorkerCopies();

        FieldWorkers fws = new FieldWorker.FieldWorkers();
        fws.setFieldWorkers(copies);

        return fws;
    }
    
    /**
     * Get all field workers in json format
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<WebserviceResult> getAllFieldWorkersJson() {
    	List<FieldWorker> copies = getAllFieldWorkerCopies();
        
        WebserviceResult result = new WebserviceResult();
        result.addDataElement("fieldWorkers", copies);
        result.setResultCode(ResultCodes.SUCCESS_CODE);
        result.setStatus(ResultCodes.SUCCESS);
        result.setResultMessage(copies.size() + " field workers were found.");
        
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.OK);
    }
    
    /**
     * Gets shallow copy of all field workers for return to client
     * @return
     */
    private List<FieldWorker> getAllFieldWorkerCopies() {
    	 List<FieldWorker> allFieldWorkers = fieldWorkerService.getAllFieldWorkers();
         List<FieldWorker> copies = new ArrayList<FieldWorker>();
         for (FieldWorker fw : allFieldWorkers) {
             copies.add(ShallowCopier.shallowCopyFieldWorker(fw));
         }
         
         return copies;
    }
}
