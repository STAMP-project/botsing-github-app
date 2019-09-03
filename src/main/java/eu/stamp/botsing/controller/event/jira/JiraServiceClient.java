package eu.stamp.botsing.controller.event.jira;

import java.net.URISyntaxException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.utility.ConfigurationBean;

public abstract class JiraServiceClient {

	private Logger log = LoggerFactory.getLogger(JiraServiceClient.class);

	private String JiraUsername;
	private String JiraPassword;

	public JiraServiceClient(ConfigurationBean configuration) throws URISyntaxException {
		this.JiraUsername = configuration.getJiraUsername();
		this.JiraPassword = configuration.getJiraPassword();
	}

	
	protected ResponseBean forwardJSonMessage (String callbackURL,JsonObject jsonObject, int okStatus, int errorStatus, String okMessage, String errorMessage) 
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String plainClientCredentials = JiraUsername + ":" + JiraPassword;
		String base64ClientCredentials = new String(Base64.getEncoder().encode(plainClientCredentials.getBytes()));
		headers.add("Authorization", "Basic " + base64ClientCredentials);
		
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> request = new HttpEntity<String>(jsonObject.toString(), headers);

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(callbackURL, request, String.class);

		if (responseEntity.getStatusCode().is2xxSuccessful())
		{
			this.log.debug("Data sent");
			return new ResponseBean(okStatus, okMessage);
		}
					
		else
		{
			this.log.error("Received status "+responseEntity.getStatusCode());
			return new ResponseBean(errorStatus,errorMessage);
		}
	}
}
