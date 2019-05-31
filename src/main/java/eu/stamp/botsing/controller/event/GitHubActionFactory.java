package eu.stamp.botsing.controller.event;

import java.io.IOException;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.issues.InvalidActionException;

public interface GitHubActionFactory {

	
	public String eventName ();
	
	public GitHubAction getAction (JsonObject jsonObject) throws IOException, InvalidActionException ;

	public GitHubAction getAction (String actionName) throws InvalidActionException ;

}
