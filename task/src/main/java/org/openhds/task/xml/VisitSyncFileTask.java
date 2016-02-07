package org.openhds.task.xml;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.SharedSessionContract;
import org.hibernate.type.CalendarType;
import org.openhds.domain.util.CalendarAdapter;
import org.openhds.task.TaskContext;
import org.openhds.task.service.AsyncTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.stream.XMLStreamWriter;

import static org.hibernate.transform.Transformers.aliasToBean;

@Component("visitXmlWriter")
public class VisitSyncFileTask extends SyncFileTemplate<VisitSyncFileTask.VisitXml> {

    @Autowired
    public VisitSyncFileTask(AsyncTaskService asyncTaskService, SessionFactory factory) {
        super(asyncTaskService, factory, AsyncTaskService.VISIT_TASK_NAME);
    }

    @Override
    protected Query createQuery(SharedSessionContract session, String query) {
        return session.createSQLQuery(query)
                .addScalar("uuid")
                .addScalar("extId")
                .addScalar("location")
                .addScalar("visitDate", CalendarType.INSTANCE)
                .addScalar("collectedBy")
                .setResultTransformer(aliasToBean(VisitXml.class));
    }

    @Override
    protected String getExportQuery() {
        return "select * from v_visit_sync where round = :round";
    }

    @Override
    protected Map<String, Object> getQueryParams(TaskContext ctx) {
        Map<String, Object> params = new HashMap<>();
        params.put("round", getRoundNumber(ctx));
        return params;
    }

    protected int getRoundNumber(TaskContext taskContext) {
        return Integer.parseInt(taskContext.getExtraData("roundNumber"));
    }

    @Override
    protected void writeXml(XMLStreamWriter xmlStreamWriter, Marshaller marshaller, VisitXml original)
            throws JAXBException {
        marshaller.marshal(original, xmlStreamWriter);
    }

    @Override
    protected Class<?> getBoundClass() {
        return VisitXml.class;
    }

    @Override
    protected String getStartElementName() {
        return "visits";
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "visit")
    public static class VisitXml {
        public String uuid;
        public String extId;
        public String location;
        @XmlJavaTypeAdapter(CalendarAdapter.class)
        public Calendar visitDate;
        public String collectedBy;
    }

}