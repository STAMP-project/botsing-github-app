package eu.stamp.botsing.controller.event.jira.issues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.ActionObject;
import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.controller.event.jira.JiraBotsingExecutor;
import eu.stamp.botsing.utility.ConfigurationBean;

@Configurable
@Component
public class JiraIssuesDefaultAction  implements JiraIssuesAction{

	Logger log = LoggerFactory.getLogger(JiraIssuesDefaultAction.class);
	private ConfigurationBean configuration;
	
	private final String 	DEFAULT_ACTION ="default",
							QUALIFIED_ACTION_NAME = EVENT_NAME+"."+DEFAULT_ACTION;

	private String description;
	

	public JiraIssuesDefaultAction(ConfigurationBean configuration) {
		this.configuration = configuration;
		this.description = "Jira generic action for "+EVENT_NAME;
	}


//	private ResponseBean parseResponse (BotsingJiraDataBean botsingJiraDataBean)
//	{
//
//		ResponseBean response = null;
//		int status = 0;
//		String message = null;
//		
//		try
//		{
//			switch (botsingExecutorResponse.getResult()) 
//			{
//
//			case OK:
//				response = client.sendData(botsingJiraDataBean.getServiceEndpoint(),
//							botsingJiraDataBean.getIssueParameters().getIssueNumber(), botsingExecutorResponse.getTestFile(),
//							botsingExecutorResponse.getScaffoldingTestFile());
//		
//
//				break;
//
//			case FAIL:
//				status = 500;
//				message = "Error executing Botsing";
//				response = client.sendErrorMessage(botsingJiraDataBean.getServiceEndpoint(), status,message, botsingExecutorResponse.getLog());
//				break;
//
//			case NO_FILES:
//				status = 304;
//				message = "Botsing did not generate any reproduction test";
//				response = client.sendErrorMessage(botsingJiraDataBean.getServiceEndpoint(), status,message, botsingExecutorResponse.getLog());
//				
//				break;
//			}
//		} catch (IOException e)
//		{
//			response = new ResponseBean (status,message+" unable to notify jira for an internal error");
//		}
//		
//
//
//
//		
//		
//		return response;
//	}
	
	@Override
	public ResponseBean execute (ActionObject actionObject) throws Exception {

		JsonObject jsonObject = actionObject.getJsonObject();
		JiraDataBean botsingJiraDataBean = new JiraDataBean(jsonObject);
		JiraBotsingExecutor botsingExecutor = new JiraBotsingExecutor (botsingJiraDataBean.getBotsingParameters(), botsingJiraDataBean.getIssueParameters().getIssueBody(), this.configuration);
		BotsingResultManager botsingResultManager = botsingExecutor.runBotsing();
		this.log.debug("Botsing executed with result "+botsingResultManager.getBotsingResult());
		return botsingResultManager.notifyToServer(botsingJiraDataBean);
	}

	@Override
	public String getActionName() {
		return DEFAULT_ACTION;
	}

	@Override
	public String getQualifiedActionName() {
		return QUALIFIED_ACTION_NAME;
	}

	@Override
	public void applyFilter(JsonObject jsonObject) throws FilteredActionException {
	}


	@Override
	public String getDescription() 
	{
		return this.description;
	}

}
