package org.openhds.webservice.resources;

import org.openhds.controller.service.refactor.IndividualService;
import org.openhds.controller.util.CacheResponseWriter;
import org.openhds.dao.service.GenericDao;
import org.openhds.dao.util.RangePropertyHelper;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Individual.Individuals;
import org.openhds.domain.util.ShallowCopier;
import org.openhds.task.support.FileResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("/individuals")
public class IndividualResource {

    private static final Logger logger = LoggerFactory.getLogger(IndividualResource.class);
    private IndividualService individualService;
    private FileResolver fileResolver;
    private GenericDao genericDao;

    @Autowired
    private CacheResponseWriter cacheResponseWriter;

    @Autowired
    public IndividualResource(IndividualService individualService, FileResolver fileResolver, GenericDao genericDao) {
        this.individualService = individualService;
        this.fileResolver = fileResolver;
        this.genericDao = genericDao;
    }

    @RequestMapping(value = "/{extId}", method = RequestMethod.GET)
    public ResponseEntity<? extends Serializable> getIndividualById(@PathVariable String extId) {
        Individual individual = individualService.getByExtId(extId);
        if (individual == null) {
            return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Individual>(ShallowCopier.makeShallowCopy(individual), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Individuals getAllIndividuals() {
        List<Individual> allIndividuals = individualService.getAll();
        List<Individual> copies = new ArrayList<Individual>(allIndividuals.size());

        ShallowCopier.makeShallowCopiesForCollection(allIndividuals, copies);

        Individuals individuals = new Individual.Individuals();
        individuals.setIndividuals(copies);

        return individuals;
    }

    @RequestMapping(value = "/byDate", method = RequestMethod.GET)
    @ResponseBody
    public Individuals getIndividualsByDate(@RequestParam(value="since", required=true) Calendar since,
                                            @RequestParam(value="until", required=true) Calendar until) {

        GenericDao.RangeProperty range = RangePropertyHelper.getRangeProperty("insertDate", since, until);
        List<Individual> allInRange = genericDao.findListByMultiPropertyAndRange(Individual.class, range);

        List<Individual> copies = new ArrayList<Individual>(allInRange.size());
        ShallowCopier.makeShallowCopiesForCollection(allInRange, copies);

        Individuals individuals = new Individual.Individuals();
        individuals.setIndividuals(copies);

        return individuals;
    }

    @RequestMapping(value = "/cached", method = RequestMethod.GET)
    public void getCachedIndividuals(HttpServletResponse response) {
        try {
            cacheResponseWriter.writeResponse(fileResolver.resolveIndividualXmlFile(), response);
        } catch (IOException e) {
            logger.error("Problem writing individual xml file: " + e.getMessage());
        }
    }
}
