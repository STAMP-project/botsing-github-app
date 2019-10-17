package eu.stamp.botsing.controller.event.github;

import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.actions.BotsingTestFiles;
import eu.stamp.botsing.controller.event.actions.NotificationDataBean;

public class GitHubSuccessResultManager extends GitHubNotificationClient implements BotsingResultManager {

	private BotsingTestFiles testFiles;
	
	public GitHubSuccessResultManager(GitHubClientManager clientManager, BotsingTestFiles testFiles) {
		super (clientManager);
		this.testFiles =testFiles;

	}
	
	@Override
	public ResponseBean notifyToServer(NotificationDataBean notificationDataBean) 
	{
		ResponseBean response = null;
		
		try
		{
			this.sendDataString(notificationDataBean.getIssueParameters(),"Botsing generated the following reproduction test.");
			this.sendDataString(notificationDataBean.getIssueParameters(), this.testFiles.getDataFileString());
			response = new ResponseBean(200,"Botsing executed succesfully with reproduction test.");
		} 
		catch (Exception e)
		{
			response = new ResponseBean(500,"Botsing executed succesfully, but unable to send the files to GitHub");
		}

		return response;
	}

	@Override
	public BotsingResult getBotsingResult() {
		return BotsingResult.OK;
	}

}
