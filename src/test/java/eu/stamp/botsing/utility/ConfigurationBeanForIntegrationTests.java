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
	private String remoteGitRepoBranch;

	private String existingGitRepoPath;
	private String existingGitRepoCrashLog;
	private String existingGitRepoTargetFrame;
	private String existingGitRepoPopulation;
	private String existingGitRepoSearchBudget;
	private String existingGitRepoTimeout;

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

	public String getRemoteGitRepoBranch() {
		return remoteGitRepoBranch;
	}

	public void setRemoteGitRepoBranch(String remoteGitRepoBranch) {
		this.remoteGitRepoBranch = remoteGitRepoBranch;
	}

	public String getExistingGitRepoPath() {
		return existingGitRepoPath;
	}

	public void setExistingGitRepoPath(String existingGitRepoPath) {
		this.existingGitRepoPath = existingGitRepoPath;
	}

	public String getExistingGitRepoCrashLog() {
		return existingGitRepoCrashLog;
	}

	public void setExistingGitRepoCrashLog(String existingGitRepoCrashLog) {
		this.existingGitRepoCrashLog = existingGitRepoCrashLog;
	}

	public String getExistingGitRepoTargetFrame() {
		return existingGitRepoTargetFrame;
	}

	public void setExistingGitRepoTargetFrame(String existingGitRepoTargetFrame) {
		this.existingGitRepoTargetFrame = existingGitRepoTargetFrame;
	}

	public String getExistingGitRepoPopulation() {
		return existingGitRepoPopulation;
	}

	public void setExistingGitRepoPopulation(String existingGitRepoPopulation) {
		this.existingGitRepoPopulation = existingGitRepoPopulation;
	}

	public String getExistingGitRepoSearchBudget() {
		return existingGitRepoSearchBudget;
	}

	public void setExistingGitRepoSearchBudget(String existingGitRepoSearchBudget) {
		this.existingGitRepoSearchBudget = existingGitRepoSearchBudget;
	}

	public String getExistingGitRepoTimeout() {
		return existingGitRepoTimeout;
	}

	public void setExistingGitRepoTimeout(String existingGitRepoTimeout) {
		this.existingGitRepoTimeout = existingGitRepoTimeout;
	}

}
