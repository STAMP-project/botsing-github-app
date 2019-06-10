package eu.stamp.botsing.controller;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.GitHubAction;
import eu.stamp.botsing.controller.event.GitHubActionFactory;
import eu.stamp.botsing.controller.event.GitHubEventFactory;
import eu.stamp.botsing.controller.event.InvalidEventException;
import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.controller.event.issues.InvalidActionException;
import eu.stamp.botsing.controller.utils.JsonMethods;

@RestController
public class GitHubAppController {

	private Logger log = LoggerFactory.getLogger(GitHubAppController.class);

	@Autowired 
	@Qualifier ("queuedEventFactory")
	private GitHubEventFactory eventFactoryFactory;



	@RequestMapping("/test")
	public String greeting() {
		return "This is the Botsing GitHub App Test Service. More information can be found here: https://github.com/STAMP-project/botsing-github-app";
	}


	@PostMapping(value = "/botsing-github-app")
	public ResponseEntity<String> getPullRequestFullBody(HttpServletRequest request,
			@RequestHeader(value = "X-GitHub-Event", defaultValue = "") String eventType) {

		ResponseEntity<String> response = null;
		
		try 
		{
			log.debug("'" + eventType + "' Event received");
			ResponseBean responseBean =  null;
			try
			{
				
				GitHubActionFactory actionFactory =  this.eventFactoryFactory.getActionFactory(eventType);
				String bodyString = getBody(request);
				JsonObject jsonObject = JsonMethods.getJSonObjectFromBodyString(bodyString);
				
				try
				{
					GitHubAction action = actionFactory.getAction(jsonObject);
					responseBean =  action.execute(jsonObject,bodyString);
					
				} catch (InvalidActionException iae)
				{
					this.log.debug("Invalid action "+iae.getActionName()+ " for event "+iae.getEventName());
					responseBean =   iae.geResponseBean();
				} catch (FilteredActionException fie)
				{
					this.log.debug("Action filtered");
					responseBean = fie.geResponseBean();
				}
				
			}

			catch (InvalidEventException ie)
			{
				log.debug("Invalid event");
				responseBean =   ie.geResponseBean();

			}

			response = ResponseEntity.status(responseBean.getStatus()).body(responseBean.getMessage());

			
		} catch (Exception e) {
		
			log.error("Error parsing event", e);
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing event");

		}
		
		return response;

	}


	/**
	 * Read body from request
	 *
	 * @param request
	 * @return
	 * @throws IOException
	 */
	protected String getBody(HttpServletRequest request) throws IOException {
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader = request.getReader();

		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}

		return buffer.toString();
	}


}