package org.openhds.webservice.resources.bioko;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.domain.model.ErrorLog;
import org.openhds.domain.model.FieldWorker;
import org.openhds.errorhandling.constants.ErrorConstants;
import org.openhds.errorhandling.service.ErrorHandlingService;
import org.openhds.errorhandling.util.ErrorLogUtil;
import org.openhds.webservice.WebServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AbstractFormResource {

    @Autowired
    private ErrorHandlingService errorService;

    //private Marshaller marshaller;

    protected ResponseEntity<WebServiceCallException> requestError(String message) {
        WebServiceCallException error = new WebServiceCallException();
        error.getErrors().add(message);
        return new ResponseEntity<WebServiceCallException>(error, HttpStatus.BAD_REQUEST);
    }

    protected ResponseEntity<WebServiceCallException> serverError(String message) {
        WebServiceCallException error = new WebServiceCallException();
        error.getErrors().add(message);
        return new ResponseEntity<WebServiceCallException>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected ResponseEntity<WebServiceCallException> requestError(ConstraintViolations cv) {
        return new ResponseEntity<WebServiceCallException>(new WebServiceCallException(cv),
                HttpStatus.BAD_REQUEST);
    }

    protected void logError(ConstraintViolations cv, FieldWorker fw, String payload, String simpleClassName) {

        ErrorLog error = ErrorLogUtil.generateErrorLog(ErrorConstants.UNASSIGNED, payload, null, simpleClassName,
                fw, ErrorConstants.UNRESOLVED_ERROR_STATUS, cv.getViolations());
        errorService.logError(error);

    }


}
