package eu.stamp.botsing.controller.event.github.issues;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.Action;
import eu.stamp.botsing.controller.event.EventFactory;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;



@Component 
public class GitHubIssuesActionFactoryImpl implements GitHubIssuesActionFactory{

	private Map<String, Action> actionMap;
	
	@Autowired
	private List<GitHubIssuesAction> actions;
	
	
	@PostConstruct
	public void init ()
	{
		this.actionMap = new HashMap<String, Action>();
		
		for (Action action: this.actions)
		{
			this.actionMap.put(action.getQualifiedActionName(), action);
		}
	}

	@Override
	public Action getAction(JsonObject jsonObject)
			throws InvalidActionException, FilteredActionException { 
	 
		String actionName = jsonObject.get(EventFactory.ACTION).getAsString();
		Action action = this.actionMap.get(EVENT_NAME+"."+actionName);
		
		if (action == null) throw new InvalidActionException(EVENT_NAME, actionName);
		else
		{
			action.applyFilter(jsonObject);
			return action;
		}
	}

	

	@Override
	public String getEventName() {
		return EVENT_NAME;
	}

	@Override
	public String getQualifiedEventName() 
	{
		return TOOL_NAME+"."+EVENT_NAME;
	}
}
