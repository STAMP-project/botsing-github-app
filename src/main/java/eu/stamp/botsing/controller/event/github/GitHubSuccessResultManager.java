package eu.stamp.botsing.controller.event.github;

import java.io.File;
import java.io.IOException;

import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.actions.NotificationDataBean;

public class GitHubSuccessResultManager extends GitHubNotificationClient implements BotsingResultManager {

	private File [] testFiles;
	
	public GitHubSuccessResultManager(GitHubClientManager clientManager, File [] testFiles) {
		super (clientManager);
		this.testFiles = testFiles;
	}
	
	@Override
	public ResponseBean notifyToServer(NotificationDataBean notificationDataBean) {
		ResponseBean response = null;
		
		try
		{
			this.sendDataString(notificationDataBean.getIssueParameters(),"Botsing generate the following reproduction test.");
			this.sendDataFile(notificationDataBean.getIssueParameters(), this.testFiles[0]);
			this.sendDataFile(notificationDataBean.getIssueParameters(), this.testFiles[1]);
			response = new ResponseBean(200,"Botsing executed succesfully with reproduction test.");
		} catch (IOException e)
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
