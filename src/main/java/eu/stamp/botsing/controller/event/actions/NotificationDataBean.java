package eu.stamp.botsing.controller.event.actions;

import java.io.IOException;

import com.google.gson.JsonObject;

import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.service.IssueParameters;

public interface NotificationDataBean {

	public JsonObject getJsonObject();

	public IssueParameters getIssueParameters() throws IOException;

	public BotsingParameters getBotsingParameters()  throws IOException;


}
