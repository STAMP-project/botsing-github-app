package eu.stamp.botsing.runner;

import java.io.File;

import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.actions.NotificationDataBean;

public class TestBotsingResultManager implements BotsingResultManager {

	private File 	testFile = null,
					scaffoldingTestFile = null;
	
	private BotsingResult result;
	
	public TestBotsingResultManager(BotsingResult result, File [] testFiles) {
		this.result = result;
		

			if (testFiles != null)
			{
				
				if(testFiles.length>0) this.testFile = testFiles[0];
			
				if(testFiles.length>1) this.testFile = testFiles[1];
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

	public File getTestFile() {
		return testFile;
	}

	public File getScaffoldingTestFile() {
		return scaffoldingTestFile;
	}
	
	

}
