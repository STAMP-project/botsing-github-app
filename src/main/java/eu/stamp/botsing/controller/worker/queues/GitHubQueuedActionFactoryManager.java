package eu.stamp.botsing.controller.worker.queues;

import eu.stamp.botsing.controller.event.GitHubActionFactory;

public interface GitHubQueuedActionFactoryManager {

	public GitHubActionFactory getActionFactory ();
	
}
