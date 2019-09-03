package eu.stamp.botsing.controller.event.github;

import java.io.IOException;

import eu.stamp.botsing.service.IssueParameters;

public class GitHubCommentManager extends GitHubNotificationClient
{

	
	public GitHubCommentManager(GitHubClientManager clientManager) {
		super (clientManager);

	}
	
	public String sendComment (IssueParameters parameters, String comment) throws IOException
	{
		return sendDataString(parameters, comment);
	}


}
