package eu.stamp.botsing.controller.github;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.stamp.botsing.controller.ActionManager;
import eu.stamp.botsing.controller.event.EventFactory;
import eu.stamp.botsing.controller.event.InvalidEventException;
import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.controller.event.github.issues.InvalidActionException;

@RestController
public class GitHubAppController extends GitHubQueuedAppController{



	private Logger log = LoggerFactory.getLogger(GitHubAppController.class);

	@RequestMapping("/test")
	public String greeting() {
		return super.greeting();
	}


	@PostMapping(value = "/botsing-github-app")
	public ResponseEntity<String> getPullRequestFullBody(HttpServletRequest request,@RequestHeader(value = "X-GitHub-Event", defaultValue = "")  String eventType) {

		ResponseEntity<String> response = null;
		
		try 
		{
			log.debug("'" + eventType + "' Event received");
			ResponseBean responseBean =  null;
				
			try
			{
				ActionManager actionManager = super.getAction(request, eventType);
				responseBean =  actionManager.executeAction();
				
			} 
			catch (InvalidEventException ie)
			{
				log.debug("Invalid event");
				responseBean =   ie.geResponseBean();

			}
			catch (InvalidActionException iae)
			{
				this.log.debug("Invalid action "+iae.getActionName()+ " for event "+iae.getEventName());
				responseBean =   iae.geResponseBean();
			} catch (FilteredActionException fie)
			{
				this.log.debug("Action filtered");
				responseBean = fie.geResponseBean();
			}
				

			response = ResponseEntity.status(responseBean.getStatus()).body(responseBean.getMessage());

			
		} catch (Exception e) {
		
			log.error("Error parsing event", e);
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing event");

		}
		
		return response;

	}
	
	@Autowired 
	public void setEventFactory (EventFactory eventFactory)  throws Exception
	{
		super.setEventFactory(eventFactory);
	}

}