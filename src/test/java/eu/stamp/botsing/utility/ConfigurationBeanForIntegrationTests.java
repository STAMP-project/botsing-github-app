package eu.stamp.botsing.utility;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties
public class ConfigurationBeanForIntegrationTests {

	private String remoteGitRepoUrl;
	private String remoteGitRepoName;
	private String remoteGitRepoOwner;

	public String getRemoteGitRepoUrl() {
		return remoteGitRepoUrl;
	}

	public void setRemoteGitRepoUrl(String remoteGitRepoUrl) {
		this.remoteGitRepoUrl = remoteGitRepoUrl;
	}

	public String getRemoteGitRepoName() {
		return remoteGitRepoName;
	}

	public void setRemoteGitRepoName(String remoteGitRepoName) {
		this.remoteGitRepoName = remoteGitRepoName;
	}

	public String getRemoteGitRepoOwner() {
		return remoteGitRepoOwner;
	}

	public void setRemoteGitRepoOwner(String remoteGitRepoOwner) {
		this.remoteGitRepoOwner = remoteGitRepoOwner;
	}

}
