package eu.stamp.botsing.controller.event.filter;

import org.springframework.http.HttpStatus;

import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.RestException;

public class FilteredActionException extends Exception implements RestException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String eventName,
					actionName,
					parameterName;
	
	public FilteredActionException(String eventName, String actionName, String parameterName) {
		super ("Action"+actionName+" filtered for parameters "+parameterName);
	
		this.actionName = actionName;
		this.eventName = eventName;
		this.parameterName = parameterName;
		
	}

	@Override
	public ResponseBean geResponseBean ()
	{
		return new ResponseBean(HttpStatus.BAD_REQUEST.value(), "Filtered Action '" + this.actionName + "' for event '"+this.eventName+ "' on parameter '"+this.parameterName+"'");
	}
}
