package eu.stamp.botsing.controller.event.jira;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import eu.stamp.botsing.controller.event.actions.BotsingExecutor;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.actions.UnableToNotifyResultManager;
import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.utility.ConfigurationBean;

public class JiraBotsingExecutor extends BotsingExecutor {

	private ConfigurationBean configuration;
	
	public JiraBotsingExecutor (BotsingParameters botsingParameters, String issueBody,ConfigurationBean configuration) {
		super (botsingParameters,issueBody);
		this.configuration = configuration;
	}
	
	@Override
	protected BotsingResultManager processFailResult(File crashLogFile) {
		
		
		try
		{
			String crashLogFileString = FileUtils.readFileToString(crashLogFile, Charset.defaultCharset());
			return new JiraErrorResultManager(this.configuration, crashLogFileString, BotsingResult.FAIL);
		}catch (URISyntaxException | IOException ioe)
		{
			return new UnableToNotifyResultManager(BotsingResult.FAIL);
		}

	}

	@Override
	protected BotsingResultManager processSuccessResult(File[] testFiles, File crashLogFile) {
		BotsingResultManager response = null;
		BotsingResult result = null;
		try
		{
			if (testFiles == null || testFiles.length<2 || testFiles[0] == null || testFiles [1] == null)
			{
				result = BotsingResult.NO_FILES;
				String crashLogFileString = FileUtils.readFileToString(crashLogFile, Charset.defaultCharset());
				response = new JiraErrorResultManager(this.configuration, crashLogFileString, result);
			}
			else
			{
				result = BotsingResult.OK;
				response = new JiraSuccessResultManager(this.configuration, Files.readAllBytes(Paths.get(testFiles[0].getAbsolutePath())), Files.readAllBytes(Paths.get(testFiles[1].getAbsolutePath())));

			}
		} catch (URISyntaxException | IOException e)
		{
			response = new UnableToNotifyResultManager(result);
		} 
		

		
		return response;

	}

}
