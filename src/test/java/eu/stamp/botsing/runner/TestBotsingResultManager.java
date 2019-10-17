package eu.stamp.botsing.runner;

import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.actions.BotsingTestFiles;
import eu.stamp.botsing.controller.event.actions.NotificationDataBean;

public class TestBotsingResultManager implements BotsingResultManager {

	private String 	testFileString = null;
	
	private BotsingResult result;
	
	public TestBotsingResultManager(BotsingResult result, BotsingTestFiles testFiles) 
	{
		this.result = result;
		
		try
		{
			this.testFileString = testFiles.getDataFileString();
		} catch (Exception e)
		{
			
		}
		


	}
	
	@Override
	public ResponseBean notifyToServer(NotificationDataBean notificationDataBean) {

		return new ResponseBean (result.getStatus(),result.getMessage()+" notification not implemented");

	}

	@Override
	public BotsingResult getBotsingResult() {

		return this.result;
	}

	public String getTestFileString() {
		return testFileString;
	}

	
	

}
