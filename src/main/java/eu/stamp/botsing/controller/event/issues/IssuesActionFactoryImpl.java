package eu.stamp.botsing.controller.event.issues;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.Action;
import eu.stamp.botsing.controller.event.EventFactory;
import eu.stamp.botsing.controller.event.InvalidActionException;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;


public abstract class IssuesActionFactoryImpl {

	private Map<String, Action> actionMap;
	private Set<String> validActionsDescriptions;

	public IssuesActionFactoryImpl() 
	{
		this.actionMap = new HashMap<String, Action>();
		this.validActionsDescriptions = new HashSet<String>();

	}

	public Action getAction(JsonObject jsonObject)
			throws InvalidActionException, FilteredActionException { 
	 
		String actionName = jsonObject.get(EventFactory.ACTION).getAsString();
		Action action = this.actionMap.get(actionName);
		
		if (action == null) throw new InvalidActionException(this.getEventName(), actionName,this.validActionsDescriptions);
		else
		{
			action.applyFilter(jsonObject);
			return action;
		}
	}

	protected void addAction (Action action)
	{
		
		this.actionMap.put(action.getActionName(), action);
		this.validActionsDescriptions.add(action.getDescription());
	
	}
	
	public abstract String getEventName();


	public abstract String getQualifiedEventName();
}
