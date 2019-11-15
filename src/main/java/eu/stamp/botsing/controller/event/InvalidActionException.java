package eu.stamp.botsing.controller.event;

import java.util.Set;

public class InvalidActionException  extends Exception implements RestException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String actionName,
					eventName;
	
	private Set<String> validActions;
	
	
	public InvalidActionException(String event, String action, Set<String> validActions) {
		super ("Invalid action "+action+ " for event "+event);
		this.actionName = action;
		this.eventName = event;
		this.validActions = validActions;
	
	}


	@Override
	public ResponseBean geResponseBean ()
	{
		return new ResponseBean(400, "action '"+this.actionName+"' is not supported for event "+this.eventName+" supported actions: "+this.validActions);
	}


	public String getActionName() {
		return actionName;
	}


	public String getEventName() {
		return eventName;
	}


	public String getValidActions ()
	{
		return this.validActions.toString();
	}


	
	
}
