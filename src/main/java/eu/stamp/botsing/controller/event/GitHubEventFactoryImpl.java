package eu.stamp.botsing.controller.event;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component ("basicEventFactory")
public class GitHubEventFactoryImpl extends GitHubAbstractEventFactoryImpl implements GitHubEventFactory {

	@Autowired
	private List<GitHubActionFactory> actionFactories;
	
	
	@PostConstruct
	public void init ()
	{
		
		for (GitHubActionFactory actionFactory: this.actionFactories)
		{
			this.addActionFactory(actionFactory.eventName(), actionFactory);
		}
	}
	

}
