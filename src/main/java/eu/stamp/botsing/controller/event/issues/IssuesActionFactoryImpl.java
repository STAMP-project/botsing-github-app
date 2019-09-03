package eu.stamp.botsing.controller.event.issues;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.Action;
import eu.stamp.botsing.controller.event.EventFactory;
import eu.stamp.botsing.controller.event.InvalidActionException;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;


public abstract class IssuesActionFactoryImpl {

	private Map<String, Action> actionMap;


	public IssuesActionFactoryImpl() {
		this.actionMap = new HashMap<String, Action>();
	}

	public Action getAction(JsonObject jsonObject)
			throws InvalidActionException, FilteredActionException { 
	 
		String actionName = jsonObject.get(EventFactory.ACTION).getAsString();
		Action action = this.actionMap.get(actionName);
		
		if (action == null) throw new InvalidActionException(this.getEventName(), actionName);
		else
		{
			action.applyFilter(jsonObject);
			return action;
		}
	}

	protected void addAction (Action action)
	{
		
		this.actionMap.put(action.getActionName(), action);
	
	}
	
	public abstract String getEventName();


	public abstract String getQualifiedEventName();
}
