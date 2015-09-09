package org.openhds.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openhds.controller.service.VisitService;
import org.openhds.domain.model.Visit;
import org.openhds.domain.util.CalendarUtil;
import org.openhds.domain.util.ShallowCopier;
import org.openhds.task.service.AsyncTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("visitXmlWriter")
public class VisitXmlWriterTask extends XmlWriterTemplate<Visit> {

    @Autowired
    public VisitXmlWriterTask(AsyncTaskService asyncTaskService, SessionFactory factory) {
        super(asyncTaskService, factory, AsyncTaskService.VISIT_TASK_NAME);
    }

    @Override
    protected Visit makeCopyOf(Visit original) {
        return ShallowCopier.makeShallowCopy(original);
    }

    @Override
    protected String getExportQuery() {
        return "from Visit" +
                " where deleted = false" +
                " and roundNumber = :round";
    }

    @Override
    protected Map<String, Object> getQueryParams(TaskContext ctx) {
        Map<String, Object> params = new HashMap<>();
        params.put("round", getRoundNumber(ctx) - 1);
        return params;
    }

    protected int getRoundNumber(TaskContext taskContext) {
        return Integer.parseInt(taskContext.getExtraData("roundNumber"));
    }

    @Override
    protected Class<?> getBoundClass() {
        return Visit.class;
    }

    @Override
    protected String getStartElementName() {
        return "visits";
    }

}
