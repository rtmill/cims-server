package org.openhds.webservice.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class WebserviceResult implements Serializable {
	
	private String status = "";
	private String resultMessage = "";
    private String parameterResultsMessage = "";
    private String dateRangeMessage = "";
	private int resultCode;
	private Map<Object, Object> data = new HashMap<>();

    public String getDateRangeMessage() {
        return dateRangeMessage;
    }

    public void setDateRangeMessage(String dateRangeMessage) {
        this.dateRangeMessage = dateRangeMessage;
    }

    public String getParameterResultsMessage() {
        return parameterResultsMessage;
    }

    public void setParameterResultsMessage(String parameterResultsMessage) {
        this.parameterResultsMessage = parameterResultsMessage;
    }

    public void addDataElement(Object key, Object value) {
		data.put(key, value);
	}

	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getResultMessage() {
		return resultMessage;
	}
	
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	
	public int getResultCode() {
		return resultCode;
	}
	
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	
	public Map<Object, Object> getData() {
		return data;
	}
	
	public void setData(Map<Object, Object> data) {
		this.data = data;
	}

}
