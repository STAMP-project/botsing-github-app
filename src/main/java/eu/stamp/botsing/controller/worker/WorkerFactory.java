package eu.stamp.botsing.controller.worker;

import java.io.IOException;

public interface WorkerFactory {

	public GitHubAppWorker getWorker (String bodyString) throws IOException;
}
