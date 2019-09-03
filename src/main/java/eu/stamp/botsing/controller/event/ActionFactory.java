package eu.stamp.botsing.controller.event;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.filter.FilteredActionException;

public interface ActionFactory 
{	
	public String getEventName();
	
	public String getQualifiedEventName ();

	public Action getAction (JsonObject jsonObject) throws InvalidActionException, FilteredActionException;


}
