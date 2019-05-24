package eu.stamp.botsing.controller.worker;

import java.io.IOException;

import com.google.gson.JsonObject;

public interface WorkerFactory {

	final String 	OPENED ="opened",
					EDIT = "edit";
	
	public GitHubAppWorker getWorker (JsonObject jsonObject) throws IOException;


}
