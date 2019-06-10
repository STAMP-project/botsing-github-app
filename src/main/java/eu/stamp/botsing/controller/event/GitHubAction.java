package eu.stamp.botsing.controller.event;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.filter.FilteredActionException;

public interface GitHubAction {

	String getActionName ();
	
	String getQualifiedActionName ();
	
	ResponseBean execute (JsonObject jsonObject, String bodyString) throws Exception;

	void applyFilter (JsonObject jsonObject) throws FilteredActionException;
}
