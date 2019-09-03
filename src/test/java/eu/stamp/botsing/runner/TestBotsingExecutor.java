package eu.stamp.botsing.runner;

import java.io.File;

import eu.stamp.botsing.controller.event.actions.BotsingExecutor;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.service.BotsingParameters;

public class TestBotsingExecutor extends BotsingExecutor {


	
	public TestBotsingExecutor(BotsingParameters botsingParameters, String issueBody ) {
		super (botsingParameters,issueBody);
	}
	
	@Override
	protected BotsingResultManager processFailResult(String mavenLogData) 
	{

		return new TestBotsingResultManager(BotsingResult.FAIL,null);
	}

	@Override
	protected BotsingResultManager processSuccessResult(File[] testFiles, String mavenLogData) {


		return new TestBotsingResultManager (BotsingResult.OK,testFiles);
	}


}
