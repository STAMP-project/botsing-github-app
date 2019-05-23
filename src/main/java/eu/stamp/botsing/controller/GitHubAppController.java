package eu.stamp.botsing.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.stamp.botsing.controller.worker.GitHubAppWorker;
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
	public Map<String, String> getPullRequestFullBody(HttpServletRequest request,
			@RequestHeader(value = "X-GitHub-Event", defaultValue = "") String eventType) {


		// TODO use HTTP 400 Bad Request if mandatory parameters are missing

		Map<String, String> response = null;
		
		try 
		{
			log.debug("'" + eventType + "' Event received");

		


			// issues Event
			if (eventType.equals("issues")) 
			{
				// get body from request
				
				String bodyString = getBody(request);
				GitHubAppWorker worker = this.workerFactory.getWorker(bodyString);
				
				response =  worker.getPullRequest();
			} else 
			{
				
					response = new HashMap<String, String>();
					response.put("message", "Event '" + eventType + "' is not supported.");
			}
			
		} catch (Exception e) {
		
			log.error("Error parsing event", e);
			
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