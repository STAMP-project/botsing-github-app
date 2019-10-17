package eu.stamp.botsing.controller.event.filter.github;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.filter.ActionFilter;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.utility.ConfigurationBean;

@Configurable
@Component ("github.configuration.change.label")
public class GitHubConfigurationBasedChangeLabelActionFilter implements ActionFilter {


	private Logger log = LoggerFactory.getLogger(GitHubConfigurationBasedChangeLabelActionFilter.class);

	private final String 	LABEL = "label",
							NAME = "name";
	
	private final String acceptedLabel;
	
	public GitHubConfigurationBasedChangeLabelActionFilter(ConfigurationBean configuration) {
		this.acceptedLabel = configuration.getGithubAcceptedLabel();
	}
	
	@Override
	public void apply(String eventName,String actionName, JsonObject jsonObject) throws FilteredActionException 
	{
		
		if (acceptedLabel != null)
		{
			try
			{
				String label = jsonObject.getAsJsonObject(LABEL).get(NAME).getAsString();
				this.log.debug("Added Label "+label);
				
				if (label == null || !label.equals(this.acceptedLabel)) throw new FilteredActionException(eventName, actionName,LABEL);
				
				
			} 
			catch (NullPointerException e)
			{
				this.log.error("Invalid message: changed label section not found");
				throw new FilteredActionException(eventName, actionName,LABEL);
			}
			
			
		}

	}
	

}
