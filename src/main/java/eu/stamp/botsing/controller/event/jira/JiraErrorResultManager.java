package eu.stamp.botsing.controller.event.jira;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.actions.NotificationDataBean;
import eu.stamp.botsing.controller.event.jira.issues.JiraDataBean;
import eu.stamp.botsing.utility.ConfigurationBean;

public class JiraErrorResultManager extends JiraServiceClient implements BotsingResultManager {

	private File crashLogFile;
	private BotsingResult botsingResult; 
	
	public JiraErrorResultManager(ConfigurationBean configuration, File crashLogFile, BotsingResult botsingResult) throws URISyntaxException {
		super (configuration);
		this.crashLogFile = crashLogFile;
		this.botsingResult = botsingResult;
	}
	
	@Override
	public ResponseBean notifyToServer(NotificationDataBean notificationDataBean) {

		String stackTrace = null;
		ResponseBean response = null;
		
		try
		{
			stackTrace = FileUtils.readFileToString(this.crashLogFile, Charset.defaultCharset());
			response = sendErrorMessage( ((JiraDataBean) notificationDataBean).getServiceEndpoint(), this.botsingResult.getStatus(), this.botsingResult.getMessage(), stackTrace);
			
			
		} catch (IOException e)
		{
			response = new ResponseBean (botsingResult.getStatus(),this.botsingResult.getMessage()+" unable to notify jira for an internal error");
		}
		
		
		
		
		return response;

	
	}
	
	public ResponseBean sendErrorMessage (String callbackURL, int status, String errorMessage, String stackTrace)
	{
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("errorMessage", errorMessage);
		
		if (stackTrace != null) jsonObject.addProperty("stackTrace", stackTrace);
		
		return forwardJSonMessage(callbackURL, jsonObject,status, status, errorMessage,errorMessage+": unable to add a comment in jira");

	}

	@Override
	public BotsingResult getBotsingResult() {
		return this.botsingResult;
	}

}
