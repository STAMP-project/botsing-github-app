package eu.stamp.botsing.controller.event.actions;

import eu.stamp.botsing.controller.event.ResponseBean;

public interface BotsingResultManager {

	
	public ResponseBean notifyToServer (NotificationDataBean notificationDataBean);
	
	public BotsingResult getBotsingResult ();
}
