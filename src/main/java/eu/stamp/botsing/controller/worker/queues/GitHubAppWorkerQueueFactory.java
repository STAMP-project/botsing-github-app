package eu.stamp.botsing.controller.worker.queues;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.worker.GitHubAppWorker;
import eu.stamp.botsing.controller.worker.GitHubAppWorkerError;
import eu.stamp.botsing.controller.worker.GitHubAppWorkerFactoryAbstract;
import eu.stamp.botsing.controller.worker.WorkerFactory;

@Component ("queueFactory")
public class GitHubAppWorkerQueueFactory  extends GitHubAppWorkerFactoryAbstract implements WorkerFactory {

	private QueueManager queueManager;
	
	@Autowired
	public GitHubAppWorkerQueueFactory(QueueManager queueManager) throws Exception {
		this.queueManager = queueManager;
		this.queueManager.startSubscriber();
	}

	public GitHubAppWorker getWorker (String bodyString) throws IOException
	{
		GitHubAppWorker response;
		JsonObject jsonObject = getJSonObjectFromBodyString(bodyString);
		String action = jsonObject.get("action").getAsString();
		
		if (action.equals(OPENED)) 
		{
			
			response = this.queueManager.getWorker();
		}
		
		else response = new GitHubAppWorkerError();
		
		response.setParameters(jsonObject,bodyString);
		return response;
		
	}

}
