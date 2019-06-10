package eu.stamp.botsing.controller.event.issues;

import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.RestException;

public class InvalidActionException  extends Exception implements RestException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String actionName,
					eventName;
	
	public InvalidActionException(String event, String action) {
		super ("Invalid action "+action+ " for event "+event);
		this.actionName = action;
		this.eventName = event;
	}


	@Override
	public ResponseBean geResponseBean ()
	{
		return new ResponseBean(400, "action '"+this.actionName+"' is not supported for event "+this.eventName);
	}


	public String getActionName() {
		return actionName;
	}


	public String getEventName() {
		return eventName;
	}


	

	
	
}
