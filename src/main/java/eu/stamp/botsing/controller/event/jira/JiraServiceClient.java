package eu.stamp.botsing.controller.event.jira;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;

public class JiraServiceClient {

	private Logger log = LoggerFactory.getLogger(JiraServiceClient.class);
	private URI endpoint;
	
	public JiraServiceClient(String endpoint) throws URISyntaxException {
		this.endpoint = new URI(endpoint);
	}
	
	public boolean sendData (String issueKey, File testFile, File scaffoldingTestFile) throws IOException
	{
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("issueKey", issueKey);
		jsonObject.addProperty ("botsingTestBody",Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(testFile.getAbsolutePath()))));
		jsonObject.addProperty ("botsingScaffoldingTestBody",Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(scaffoldingTestFile.getAbsolutePath()))));
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> request = new HttpEntity<String>(jsonObject.toString(), headers);
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(this.endpoint, request, String.class);
		if (responseEntity.getStatusCode().is2xxSuccessful()) 
		{
			this.log.debug("Data sent");
			return true;
		}
		else
		{
			this.log.error("Received status "+responseEntity.getStatusCode());
			return false;
		}
	}
}
