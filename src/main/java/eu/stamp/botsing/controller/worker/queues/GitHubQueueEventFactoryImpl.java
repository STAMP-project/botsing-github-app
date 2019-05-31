package eu.stamp.botsing.controller.worker.queues;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.stamp.botsing.controller.event.GitHubAbstractEventFactoryImpl;
import eu.stamp.botsing.controller.event.GitHubActionFactory;
import eu.stamp.botsing.controller.event.GitHubEventFactory;



@Component ("queuedEventFactory")
public class GitHubQueueEventFactoryImpl extends GitHubAbstractEventFactoryImpl implements GitHubEventFactory {

	@Autowired
	private List<GitHubQueuedActionFactoryManager> actionFactoryManagers;
	
	
	@PostConstruct
	public void init ()
	{
		
		for (GitHubQueuedActionFactoryManager actionFactoryManager: this.actionFactoryManagers)
		{
			GitHubActionFactory actionFactory = actionFactoryManager.getActionFactory();
			this.addActionFactory(actionFactory.eventName(), actionFactory);
		}
	}
	

}
