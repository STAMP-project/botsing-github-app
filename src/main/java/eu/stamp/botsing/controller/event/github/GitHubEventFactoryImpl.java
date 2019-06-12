package eu.stamp.botsing.controller.event.github;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.stamp.botsing.controller.event.ActionFactory;
import eu.stamp.botsing.controller.event.EventFactory;
import eu.stamp.botsing.controller.event.InvalidEventException;



@Component 
public class GitHubEventFactoryImpl  implements EventFactory {

	@Autowired
	private List<ActionFactory> actionFactories;
	
	private Map<String, ActionFactory> actionFactoryMap;

	
	public GitHubEventFactoryImpl() 
	{
		this.actionFactoryMap = new HashMap<String, ActionFactory>();
	}

	@PostConstruct
	public void init ()
	{
		
		for (ActionFactory actionFactory: this.actionFactories)
		{
			this.actionFactoryMap.put(actionFactory.eventName(), actionFactory);
		}
	}

	

	public ActionFactory getActionFactory(String eventName) throws InvalidEventException 
	{
		ActionFactory actionFactory =  this.actionFactoryMap.get(eventName);
	
		if (actionFactory == null) throw new InvalidEventException(eventName);
		else return actionFactory;
	}

}
