package eu.stamp.botsing.controller.event;

import org.springframework.http.HttpStatus;

public class InvalidEventException extends Exception implements RestException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String eventName;
	
	public InvalidEventException(String eventName) {
		super ("Invalid event "+eventName);
		this.eventName = eventName;
	}

	public String getEventName() {
		return eventName;
	}
	
	@Override
	public ResponseBean geResponseBean ()
	{
		return new ResponseBean(HttpStatus.NOT_IMPLEMENTED.value(), "Event '" + this.eventName + "' is not supported.");
	}
	
	
}
