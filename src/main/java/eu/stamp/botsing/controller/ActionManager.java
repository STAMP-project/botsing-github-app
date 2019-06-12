package eu.stamp.botsing.controller;

import eu.stamp.botsing.controller.event.Action;
import eu.stamp.botsing.controller.event.ResponseBean;

public class ActionManager {

	private Action action;
	private ActionObject actionObject;
	
	public ActionManager(Action action, ActionObject actionObject) {
		this.action =action;
		this.actionObject = actionObject;		
	}

	public ActionManager (ActionManager baseActionManager, ActionObject newActionObject)
	{
		this.action = baseActionManager.action;
		this.actionObject = newActionObject;
	}
	
	public ActionManager (Action newAction, ActionManager baseActionManager)
	{
		this.action = newAction;
		this.actionObject = baseActionManager.actionObject;
	}
	
	public ResponseBean executeAction () throws Exception
	{
		return this.action.execute(this.actionObject);
	}
	
	public String getActionName ()
	{
		return this.action.getActionName();
	}


	
}
