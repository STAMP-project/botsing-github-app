package eu.stamp.botsing.controller.event.jira;

import java.net.URISyntaxException;

import eu.stamp.botsing.controller.event.actions.BotsingExecutor;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.actions.BotsingTestFiles;
import eu.stamp.botsing.controller.event.actions.UnableToNotifyResultManager;
import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.utility.ConfigurationBean;

public class JiraBotsingExecutor extends BotsingExecutor <BotsingTestFiles> {

	private ConfigurationBean configuration;
	
	public JiraBotsingExecutor (BotsingParameters botsingParameters, String issueBody,ConfigurationBean configuration) {
		super (botsingParameters,issueBody);
		this.configuration = configuration;
	}
	
	@Override
	protected BotsingResultManager processFailResult(String mavenLogData) {
		
		
		try
		{

			return new JiraErrorResultManager(this.configuration, mavenLogData.getBytes(), BotsingResult.FAIL);
		}catch (URISyntaxException e)
		{
			return new UnableToNotifyResultManager(BotsingResult.FAIL);
		}

	}

	@Override
	protected BotsingResultManager processSuccessResult(BotsingTestFiles testFiles, String mavenLogData) 
	{
		BotsingResultManager response = null;
		BotsingResult result = null;
		
		try
		{
			if (!testFiles.isCompleted())
			{
				result = BotsingResult.NO_FILES;
				response = new JiraErrorResultManager(this.configuration, mavenLogData.getBytes(), result);
			}
			else
			{
				result = BotsingResult.OK;
				response = new JiraSuccessResultManager(this.configuration, testFiles.getDataFileString().getBytes());

			}
		} catch (Exception e)
		{
			response = new UnableToNotifyResultManager(result);
		} 
		

		
		return response;

	}

	@Override
	protected BotsingTestFiles generateTestFilesStructure() 
	{
		return new BotsingTestFiles();
	}

}
