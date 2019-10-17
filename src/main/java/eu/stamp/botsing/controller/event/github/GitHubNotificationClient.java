package eu.stamp.botsing.controller.event.github;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.stamp.botsing.service.IssueParameters;

public abstract class GitHubNotificationClient 
{

	private Logger log = LoggerFactory.getLogger(GitHubNotificationClient.class);
	private GitHubClient client;
	
	public GitHubNotificationClient (GitHubClientManager clientManager)
	{
		this.client = clientManager.getGitHubClient();
	}
	
	protected String sendDataString(IssueParameters parameters, String dataString) throws IOException 
	{
		log.debug("Creating comment");
		IssueService issueService = new IssueService(client);
		Comment issueComment = issueService.createComment(parameters.getRepositoryOwner(), parameters.getRepositoryName(), parameters.getIssueNumber(), dataString);
		log.debug("Issue comment created");
		return issueComment.getBody();
	}
	
	protected String sendDataFile(IssueParameters parameters, File file) throws IOException 
	{
		log.debug("Creating new issue comment with file");
		String comment = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		return sendDataString(parameters, comment);
	}
	
}
