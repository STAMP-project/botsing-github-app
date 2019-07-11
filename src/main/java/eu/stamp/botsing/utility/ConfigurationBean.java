package eu.stamp.botsing.utility;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties
public class ConfigurationBean {

	private String githubOAuth2Token;
	private String githubUsername;
	private String githubPassword;
	private String githubToken;
	private String githubAcceptedLabel;
	private String proxyHost;
	private Integer proxyPort;
	
	private String jiraURL;
	private String jiraUsername;
	private String jiraPassword;

	public String getGithubOAuth2Token() {
		return githubOAuth2Token;
	}

	public void setGithubOAuth2Token(String githubOAuth2Token) {
		this.githubOAuth2Token = githubOAuth2Token;
	}

	public String getGithubUsername() {
		return githubUsername;
	}

	public void setGithubUsername(String githubUsername) {
		this.githubUsername = githubUsername;
	}

	public String getGithubPassword() {
		return githubPassword;
	}

	public void setGithubPassword(String githubPassword) {
		this.githubPassword = githubPassword;
	}

	public String getGithubToken() {
		return githubToken;
	}

	public void setGithubToken(String githubToken) {
		this.githubToken = githubToken;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public Integer getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getGithubAcceptedLabel() {
		return githubAcceptedLabel;
	}

	public void setGithubAcceptedLabel(String githubAcceptedLabel) {
		this.githubAcceptedLabel = githubAcceptedLabel;
	}

	public String getJiraURL() {
		return jiraURL;
	}

	public void setJiraURL(String jiraURL) {
		this.jiraURL = jiraURL;
	}

	public String getJiraUsername() {
		return jiraUsername;
	}

	public void setJiraUsername(String jiraUsername) {
		this.jiraUsername = jiraUsername;
	}

	public String getJiraPassword() {
		return jiraPassword;
	}

	public void setJiraPassword(String jiraPassword) {
		this.jiraPassword = jiraPassword;
	}


	

	
}
