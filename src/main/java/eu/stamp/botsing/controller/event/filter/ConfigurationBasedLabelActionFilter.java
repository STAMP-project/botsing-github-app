package eu.stamp.botsing.controller.event.filter;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import eu.stamp.botsing.utility.ConfigurationBean;

@Configurable
@Component ("configuration")
public class ConfigurationBasedLabelActionFilter implements ActionFilter {


	private Logger log = LoggerFactory.getLogger(ConfigurationBasedLabelActionFilter.class);

	private final String 	ISSUE = "issue",
							LABELS = "labels",
							NAME = "name";
	
	private String acceptedLabel;
	
	public ConfigurationBasedLabelActionFilter(ConfigurationBean configuration) {
		this.acceptedLabel = configuration.getGithubAcceptedLabel();
	}
	
	@Override
	public void apply(String eventName,String actionName, JsonObject jsonObject) throws FilteredActionException 
	{
		
		if (acceptedLabel != null)
		{
			try
			{
				if (!findLabel(jsonObject.getAsJsonObject(ISSUE).getAsJsonArray(LABELS), this.acceptedLabel))throw new FilteredActionException(eventName, actionName,LABELS);
				
				
			} 
			catch (NullPointerException e)
			{
				this.log.error("Invalid message: labels section not found");
				throw new FilteredActionException(eventName, actionName,LABELS);
			}
			
			
		}

	}
	
	private boolean findLabel (JsonArray labels, String acceptedLabel)
	{
		boolean found = false;
		Iterator<JsonElement> labelElements = labels.iterator(); 
		
		while (labelElements.hasNext() && ! found)
		{
			found = acceptedLabel.equals(labelElements.next().getAsJsonObject().get(NAME).getAsString());
		}
		
		return found;
	}

}
