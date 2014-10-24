package org.openhds.controller.service.refactor.crudhelpers;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.service.refactor.FieldWorkerService;
import org.openhds.controller.service.refactor.LocationService;
import org.openhds.dao.service.GenericDao;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wolfe on 9/19/14.
 */

@Component("FieldWorkerCrudHelper")
public class FieldWorkerCrudHelper extends AbstractEntityCrudHelperImpl<FieldWorker> {


    @Autowired
    FieldWorkerService fieldWorkerService;


    @Override
    protected void preCreateSanityChecks(FieldWorker fieldWorker) throws ConstraintViolations {

    }

    @Override
    protected void cascadeReferences(FieldWorker fieldWorker) throws ConstraintViolations {

        fieldWorkerService.generateId(fieldWorker);
        fieldWorkerService.generatePasswordHash(fieldWorker);
        generateIdPrefix(fieldWorker);


    }

    @Override
    protected void validateReferences(FieldWorker fieldWorker) throws ConstraintViolations {

        ConstraintViolations constraintViolations = new ConstraintViolations();
        if (!fieldWorkerService.isEligibleForCreation(fieldWorker, constraintViolations)) {
            throw (constraintViolations);
        }

    }

    @Override
    public List<FieldWorker> getAll() {
        return genericDao.findAll(FieldWorker.class, true);
    }


    private void generateIdPrefix(FieldWorker fieldWorker){

        List<FieldWorker> fieldWorkers = getAllOrderedByIdPrefix();

        FieldWorker lastFieldWorker = fieldWorkers.get(fieldWorkers.size()-1);

        int newIdPrefix = lastFieldWorker.getIdPrefix() + 1;

        fieldWorker.setIdPrefix(newIdPrefix);

    }

    private List<FieldWorker> getAllOrderedByIdPrefix(){
        GenericDao.OrderProperty fieldWorkerIdPrefix = new GenericDao.OrderProperty() {

            public String getPropertyName() {
                return "idPrefix";
            }

            public boolean isAscending() {
                return true;
            }
        };

        return genericDao.findAllWithOrder(FieldWorker.class, fieldWorkerIdPrefix);
    }

    @Override
    public FieldWorker read(String id) {
        return genericDao.findByProperty(FieldWorker.class,"extId",id);
    }
}