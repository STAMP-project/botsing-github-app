package eu.stamp.botsing.controller.event.github;

import java.io.IOException;

import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.actions.NotificationDataBean;

public class GitHubErrorResultManager extends GitHubNotificationClient implements BotsingResultManager {

	private BotsingResult result;
	
	public GitHubErrorResultManager(GitHubClientManager clientManager,BotsingResult result) {
		super (clientManager);
		this.result = result;
	}
	
	@Override
	public ResponseBean notifyToServer(NotificationDataBean notificationDataBean) {
		ResponseBean response = null;
		
		try
		{
		this.sendDataString(notificationDataBean.getIssueParameters(), result.getMessage());
		response = new ResponseBean(this.result.getStatus(),result.getMessage());
		
		} catch (IOException e)
		{
			response = new ResponseBean(this.result.getStatus(),result.getMessage()+",  unable to send the details to GitHub");
		}
		return response;
	}

	@Override
	public BotsingResult getBotsingResult() {
		return this.result;
	}

}
