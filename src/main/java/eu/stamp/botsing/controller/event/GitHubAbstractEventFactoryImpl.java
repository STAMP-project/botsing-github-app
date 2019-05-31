package eu.stamp.botsing.controller.event;

import java.util.HashMap;
import java.util.Map;


public abstract class GitHubAbstractEventFactoryImpl {

	
	private Map<String, GitHubActionFactory> actionFactoryMap;
	
	public GitHubAbstractEventFactoryImpl() {
		this.actionFactoryMap = new HashMap<String, GitHubActionFactory>();
	}

	protected void addActionFactory (String action, GitHubActionFactory actionFactory)
	{
		this.actionFactoryMap.put(action, actionFactory);
	}
	

	public GitHubActionFactory getActionFactory(String eventName) throws InvalidEventException 
	{
		GitHubActionFactory actionFactory =  this.actionFactoryMap.get(eventName);
	
		if (actionFactory == null) throw new InvalidEventException(eventName);
		else return actionFactory;
	}

}
