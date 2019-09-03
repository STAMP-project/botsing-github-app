package eu.stamp.botsing.controller.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component 
public class EventFactoryImpl  implements EventFactory {

	@Autowired
	private List<ActionFactory> actionFactories;
	
	private Map<String, ActionFactory> actionFactoryMap;

	
	public EventFactoryImpl() 
	{
		this.actionFactoryMap = new HashMap<String, ActionFactory>();
	}

	@PostConstruct
	public void init ()
	{
		
		for (ActionFactory actionFactory: this.actionFactories)
		{
			this.actionFactoryMap.put(actionFactory.getQualifiedEventName(), actionFactory);
		}
	}

	

	public ActionFactory getActionFactory(String toolName,String eventName) throws InvalidEventException 
	{
		String qualifiedEvent = toolName+"."+eventName;
		
		ActionFactory actionFactory =  this.actionFactoryMap.get(qualifiedEvent);
	
		if (actionFactory == null) throw new InvalidEventException(qualifiedEvent);
		else return actionFactory;
	}

}
