package eu.stamp.botsing.controller.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component 
public class EventFactoryImpl  implements EventFactory {


	
	private Map<String, ActionFactory> actionFactoryMap;

	
	@Autowired
	public EventFactoryImpl(List<ActionFactory> actionFactories) 
	{
		this.actionFactoryMap = new HashMap<String, ActionFactory>();
	
		if (actionFactories != null)
		{
			for (ActionFactory actionFactory: actionFactories)
			{
				this.actionFactoryMap.put(actionFactory.getQualifiedEventName(), actionFactory);
			}
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
