package eu.stamp.botsing.controller;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.ActionFactory;
import eu.stamp.botsing.controller.event.EventFactory;
import eu.stamp.botsing.controller.event.InvalidEventException;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.controller.event.github.issues.InvalidActionException;
import eu.stamp.botsing.controller.utils.JsonMethods;


public abstract class AppController {

	private Logger log = LoggerFactory.getLogger(AppController.class);
	
	private EventFactory eventFactory;




	protected ActionManager getAction (HttpServletRequest request,String toolName,String eventType) throws InvalidEventException, IOException, InvalidActionException, FilteredActionException 
	{
			log.debug("'" + eventType + "' Event received");
			ActionFactory actionFactory =  this.eventFactory.getActionFactory(toolName,eventType);
			String bodyString = getBody(request);
			JsonObject jsonObject = JsonMethods.getJSonObjectFromBodyString(bodyString);
			ActionObject actionObject = new ActionObject(jsonObject, bodyString);
			return new ActionManager(actionFactory.getAction(jsonObject), actionObject);

	}
	
	
	public void setEventFactory (EventFactory eventFactory) throws Exception
	{
		this.eventFactory = eventFactory;
	}
	
	protected EventFactory getEventFactory ()
	{
		return this.eventFactory;
	}




	/**
	 * Read body from request
	 *
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private String getBody(HttpServletRequest request) throws IOException {
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader = request.getReader();

		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}

		return buffer.toString();
	}


}