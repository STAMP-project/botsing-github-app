package eu.stamp.botsing.controller.event.jira;

import java.net.URISyntaxException;
import java.util.Base64;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.actions.NotificationDataBean;
import eu.stamp.botsing.utility.ConfigurationBean;

public class JiraErrorResultManager extends JiraServiceClient implements BotsingResultManager {

	private byte [] crashLogData;
	private BotsingResult botsingResult; 
	
	public JiraErrorResultManager(ConfigurationBean configuration, byte[] crashLogData, BotsingResult botsingResult) throws URISyntaxException {
		super (configuration);
		this.crashLogData = crashLogData;
		this.botsingResult = botsingResult;
	}
	
	@Override
	public ResponseBean notifyToServer(NotificationDataBean notificationDataBean) 
	{
		
		return sendErrorMessage(((JiraEndpointOwner) notificationDataBean).getServiceEndpoint(),
				this.botsingResult.getStatus(), this.botsingResult.getMessage(),
				Base64.getEncoder().encodeToString(this.crashLogData));

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
