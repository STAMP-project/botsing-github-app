package eu.stamp.botsing.controller.event.github;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eu.stamp.botsing.utility.ConfigurationBean;



@Service
public class GitHubClientManager {

	private Logger log = LoggerFactory.getLogger(GitHubClientManager.class);
	private GitHubClient client;
	
	public GitHubClientManager(ConfigurationBean configuration) {
		String gitHubURL = configuration.getGithubURL();
		
		client = gitHubURL == null || gitHubURL.trim().isEmpty() ? new GitHubClient() : new GitHubClient(gitHubURL);

		// set credentials
		if (configuration.getGithubOAuth2Token() != null || configuration.getGithubOAuth2Token().length() > 0) {
			client.setOAuth2Token(configuration.getGithubOAuth2Token());

		} else if (configuration.getGithubUsername() != null || configuration.getGithubUsername().length() > 0) {
			client.setCredentials(configuration.getGithubUsername(), configuration.getGithubPassword());
		} else {
			log.warn("No GitHub credentials provided");
		}

		client.setHeaderAccept("application/vnd.github.machine-man-preview+json");

		if (configuration.getProxyHost() != null && configuration.getProxyPort() != null) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP,
					new InetSocketAddress(configuration.getProxyHost(), configuration.getProxyPort()));
			client.setProxy(proxy);
		}
	}
	
	public GitHubClient getGitHubClient ()
	{
		return this.client;
	}
}
