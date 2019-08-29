package eu.stamp.botsing.controller.event.github;

import java.io.File;

import eu.stamp.botsing.controller.event.actions.BotsingExecutor;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.service.BotsingParameters;

public class GitHubBotsingExecutor extends BotsingExecutor {

	private GitHubClientManager clientManager;
	
	public GitHubBotsingExecutor (BotsingParameters botsingParameters, String issueBody,GitHubClientManager clientManager) {
		super (botsingParameters,issueBody);
		this.clientManager = clientManager;
	}
	
	@Override
	protected BotsingResultManager processFailResult(File crashLogFile) {
		return new GitHubErrorResultManager(this.clientManager, BotsingResult.FAIL);
	}

	@Override
	protected BotsingResultManager processSuccessResult(File[] testFiles, File crashLogFile) {
		BotsingResultManager response = null;
		
		if (testFiles == null || testFiles.length<2 || testFiles[0] == null || testFiles [1] == null)
		{

			response = new GitHubErrorResultManager(this.clientManager, BotsingResult.NO_FILES);
		}
		else
		{

			response = new GitHubSuccessResultManager(this.clientManager, testFiles);

		}
		
		return response;
	}

}
