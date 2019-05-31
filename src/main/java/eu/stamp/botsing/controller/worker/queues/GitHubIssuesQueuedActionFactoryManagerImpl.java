package eu.stamp.botsing.controller.worker.queues;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.stamp.botsing.controller.event.GitHubActionFactory;
import eu.stamp.botsing.controller.event.issues.GitHubIssuesAction;

@Component
public class GitHubIssuesQueuedActionFactoryManagerImpl implements GitHubQueuedActionFactoryManager {

	private GitHubIssuesQueuedActionFactory actionFactory; 
	

	
	@Autowired
	public GitHubIssuesQueuedActionFactoryManagerImpl(QueueManager queueManager,List<GitHubIssuesAction> actions) throws Exception {
		this.actionFactory = new GitHubIssuesQueuedActionFactory(queueManager,actions);
		this.actionFactory.init();
	}
	
	@Override
	public GitHubActionFactory getActionFactory() 
	{

		return this.actionFactory;
	}

}
