package eu.stamp.botsing.controller.event;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.ActionObject;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;

public interface Action {

	String getActionName ();
	
	String getQualifiedActionName ();
	
	ResponseBean execute (ActionObject actionObject) throws Exception;

	void applyFilter (JsonObject jsonObject) throws FilteredActionException;
	
	String getDescription ();	
}
