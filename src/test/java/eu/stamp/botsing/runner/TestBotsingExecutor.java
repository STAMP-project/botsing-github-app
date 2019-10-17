package eu.stamp.botsing.runner;

import eu.stamp.botsing.controller.event.actions.BotsingExecutor;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.actions.BotsingTestFiles;
import eu.stamp.botsing.service.BotsingParameters;

public class TestBotsingExecutor extends BotsingExecutor<BotsingTestFiles> {


	
	public TestBotsingExecutor(BotsingParameters botsingParameters, String issueBody ) {
		super (botsingParameters,issueBody);
	}
	
	@Override
	protected BotsingResultManager processFailResult(String mavenLogData) 
	{

		return new TestBotsingResultManager(BotsingResult.FAIL,null);
	}

	@Override
	protected BotsingResultManager processSuccessResult(BotsingTestFiles testFiles, String mavenLogData) {


		return new TestBotsingResultManager (BotsingResult.OK,testFiles);
	}

	@Override
	protected BotsingTestFiles generateTestFilesStructure() 
	{

		return new BotsingTestFiles();
	}


}
