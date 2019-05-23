package eu.stamp.botsing.controller.worker;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import eu.stamp.botsing.service.GitHubService;


@Component ("basicFactory")
public class GitHubAppWorkerFactory extends GitHubAppWorkerFactoryAbstract implements WorkerFactory{


	@Autowired
	private GitHubService gitHubService;
	

	
	@Override
	public GitHubAppWorker getWorker (String bodyString) throws IOException
	{
		GitHubAppWorker response;
		JsonObject jsonObject = getJSonObjectFromBodyString(bodyString);
		String action = jsonObject.get("action").getAsString();
		
		if (action.equals(OPENED)) response =new GitHubAppWorkerOpened (this.gitHubService);
		
		else response = new GitHubAppWorkerError();
		
		response.setParameters(jsonObject,bodyString);
		return response;
		
	}


	
}
