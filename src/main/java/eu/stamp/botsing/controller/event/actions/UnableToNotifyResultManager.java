package eu.stamp.botsing.controller.event.actions;

import eu.stamp.botsing.controller.event.ResponseBean;

public class UnableToNotifyResultManager implements BotsingResultManager {

	private BotsingResult result;
	public UnableToNotifyResultManager(BotsingResult result) {
		this.result = result;
	}
	
	@Override
	public ResponseBean notifyToServer(NotificationDataBean notificationDataBean) {
		
		return new ResponseBean (result.getStatus(),result.getMessage()+" unable to notify jira for an internal error");
	}

	@Override
	public BotsingResult getBotsingResult() 
	{
		return result;
	}

}
