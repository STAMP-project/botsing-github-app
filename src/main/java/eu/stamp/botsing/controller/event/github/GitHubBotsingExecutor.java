package eu.stamp.botsing.controller.event.github;

import eu.stamp.botsing.controller.event.actions.BotsingExecutor;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.actions.BotsingTestFiles;
import eu.stamp.botsing.service.BotsingParameters;

public class GitHubBotsingExecutor extends BotsingExecutor <BotsingTestFiles>{

	private GitHubClientManager clientManager;
	
	public GitHubBotsingExecutor (BotsingParameters botsingParameters, String issueBody,GitHubClientManager clientManager) {
		super (botsingParameters,issueBody);
		this.clientManager = clientManager;
	}
	
	@Override
	protected BotsingResultManager processFailResult(String mavenLogData) 
	{
		return new GitHubErrorResultManager(this.clientManager, BotsingResult.FAIL);
	}

	@Override
	protected BotsingResultManager processSuccessResult(BotsingTestFiles testFiles, String mavenLogData) 
	{
		BotsingResultManager response = null;
		
		if (!testFiles.isCompleted())
		{

			response = new GitHubErrorResultManager(this.clientManager, BotsingResult.NO_FILES);
		}
		else
		{

			response = new GitHubSuccessResultManager(this.clientManager, testFiles);

		}
		
		return response;
	}

	@Override
	protected BotsingTestFiles generateTestFilesStructure() 
	{
		return new BotsingTestFiles();
	}



}
