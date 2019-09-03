package eu.stamp.botsing.controller.event.filter.github;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.filter.ActionFilter;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.utility.ConfigurationBean;

@Configurable
@Component ("github.configuration")
public class GitHubConfigurationBasedLabelActionFilter implements ActionFilter {


	private Logger log = LoggerFactory.getLogger(GitHubConfigurationBasedLabelActionFilter.class);

	private final String 	ISSUE = "issue",
							LABELS = "labels",
							NAME = "name";
	
	private String acceptedLabel;
	
	public GitHubConfigurationBasedLabelActionFilter(ConfigurationBean configuration) {
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
