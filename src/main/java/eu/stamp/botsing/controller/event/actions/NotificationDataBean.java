package eu.stamp.botsing.controller.event.actions;

import com.google.gson.JsonObject;

import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.service.IssueParameters;

public interface NotificationDataBean {

	public JsonObject getJsonObject();

	public IssueParameters getIssueParameters();

	public BotsingParameters getBotsingParameters();


}
