package eu.stamp.botsing.controller.event.filter;

import com.google.gson.JsonObject;

public interface ActionFilter {

		public void apply (String eventName,String actionName, JsonObject jsonObject) throws FilteredActionException; 
		
		public String getDescription ();
}
