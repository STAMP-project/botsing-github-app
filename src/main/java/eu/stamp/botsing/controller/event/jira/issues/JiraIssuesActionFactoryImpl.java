package eu.stamp.botsing.controller.event.jira.issues;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.Action;
import eu.stamp.botsing.controller.event.ActionFactory;
import eu.stamp.botsing.controller.event.InvalidActionException;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.controller.event.issues.IssuesActionFactoryImpl;
import eu.stamp.botsing.controller.jira.JiraAppController;



@Component 
public class JiraIssuesActionFactoryImpl  extends IssuesActionFactoryImpl implements ActionFactory{

	public JiraIssuesActionFactoryImpl() {
		super ();
	}


	@Override
	public Action getAction(JsonObject jsonObject)
			throws InvalidActionException, FilteredActionException { 
	 
		return super.getAction(jsonObject);
	}

	
	@Autowired
	public void setJiraIssuesActions (List<JiraIssuesAction> actions)
	{
		for (JiraIssuesAction action : actions)
		{
			this.addAction(action);
		}
	}

	@Override
	public String getEventName() 
	{
		return JiraIssuesAction.EVENT_NAME;
	}

	@Override
	public String getQualifiedEventName() 
	{
		return JiraAppController.TOOL_NAME+"."+JiraIssuesAction.EVENT_NAME;
	}
}
