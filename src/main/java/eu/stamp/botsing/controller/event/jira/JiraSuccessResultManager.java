package eu.stamp.botsing.controller.event.jira;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.actions.NotificationDataBean;
import eu.stamp.botsing.utility.ConfigurationBean;

public class JiraSuccessResultManager  extends JiraServiceClient implements BotsingResultManager {


	private byte[] 	testFileBytes;

	public JiraSuccessResultManager(ConfigurationBean configuration,byte[] testFileBytes) throws URISyntaxException {
		super (configuration);
		this.testFileBytes = testFileBytes;
		
	}

	private ResponseBean sendData (String callbackURL, String issueKey, byte[] testFileByte)
	{
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("issueKey", issueKey);
		jsonObject.addProperty("botsingTestBody", Base64.getEncoder().encodeToString(testFileByte));
		//jsonObject.addProperty("botsingScaffoldingTestBody", Base64.getEncoder().encodeToString(scaffoldingTestFileByte));
		return forwardJSonMessage(callbackURL, jsonObject,200,502,"Botsing executed succesfully with reproduction test.","Botsing executed succesfully but unable to send the files to jira");
	}

	
	
	@Override
	public ResponseBean notifyToServer(NotificationDataBean notificationDataBean) throws IOException{
		
		return sendData( ((JiraEndpointOwner) notificationDataBean).getServiceEndpoint(), notificationDataBean.getIssueParameters().getIssueNumber(), this.testFileBytes);
	}

	@Override
	public BotsingResult getBotsingResult() 
	{
		return BotsingResult.OK;
	}
	


}
