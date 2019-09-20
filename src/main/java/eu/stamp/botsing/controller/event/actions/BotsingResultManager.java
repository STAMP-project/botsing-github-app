package eu.stamp.botsing.controller.event.actions;

import java.io.IOException;

import eu.stamp.botsing.controller.event.ResponseBean;

public interface BotsingResultManager 
{
	
	public ResponseBean notifyToServer (NotificationDataBean notificationDataBean) throws IOException;
	
	public BotsingResult getBotsingResult ();
}
