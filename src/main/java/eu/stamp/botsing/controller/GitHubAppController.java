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

import eu.stamp.botsing.controller.utils.JsonMethods;
import eu.stamp.botsing.controller.worker.GitHubAppWorker;
import eu.stamp.botsing.controller.worker.ResponseBean;
import eu.stamp.botsing.controller.worker.WorkerFactory;

@RestController
public class GitHubAppController {

	Logger log = LoggerFactory.getLogger(GitHubAppController.class);

	@Autowired 
	@Qualifier ("queueFactory")
	private WorkerFactory workerFactory;



	@RequestMapping("/test")
	public String greeting() {
		return "This is the Botsing GitHub App Test Service. More informations can be found here: https://github.com/STAMP-project/botsing-github-app";
	}

	@PostMapping(value = "/botsing-github-app")
	public ResponseEntity<String> getPullRequestFullBody(HttpServletRequest request,
			@RequestHeader(value = "X-GitHub-Event", defaultValue = "") String eventType) {


		// TODO use HTTP 400 Bad Request if mandatory parameters are missing

		ResponseEntity<String> response = null;
		
		try 
		{
			log.debug("'" + eventType + "' Event received");

			// issues Event
			if (eventType.equals("issues")) 
			{
				// get body from request
				
				String bodyString = getBody(request);
				JsonObject jsonObject = JsonMethods.getJSonObjectFromBodyString(bodyString);
				GitHubAppWorker worker = this.workerFactory.getWorker(jsonObject);
				ResponseBean responseBean =  worker.getPullRequest(jsonObject,bodyString);
				response = ResponseEntity.status(responseBean.getStatus()).body(responseBean.getMessage());
				log.debug("Event processed");
				
			} else 
			{
				log.debug("Invalid event");
				response = ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Event '" + eventType + "' is not supported.");
			}
			
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