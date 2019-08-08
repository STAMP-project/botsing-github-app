package eu.stamp.botsing.controller.event.jira;

import java.io.File;
import java.io.IOException;
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

import eu.stamp.botsing.utility.ConfigurationBean;

public class JiraServiceClient {

	private Logger log = LoggerFactory.getLogger(JiraServiceClient.class);

	private String JiraUsername;
	private String JiraPassword;

	public JiraServiceClient(ConfigurationBean configuration) throws URISyntaxException {
		this.JiraUsername = configuration.getJiraUsername();
		this.JiraPassword = configuration.getJiraPassword();
	}

	public boolean sendData (String callbackURL, String issueKey, byte[] testFileByte, byte [] scaffoldingTestFileByte) throws IOException
	{
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("issueKey", issueKey);
		jsonObject.addProperty("botsingTestBody", Base64.getEncoder().encodeToString(testFileByte));
		jsonObject.addProperty("botsingScaffoldingTestBody", Base64.getEncoder().encodeToString(scaffoldingTestFileByte));

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
			return true;
		}
		else
		{
			this.log.error("Received status "+responseEntity.getStatusCode());
			return false;
		}
	}

	public boolean sendData (String callbackURL, String issueKey, File testFile, File scaffoldingTestFile) throws IOException
	{
		return sendData(callbackURL, issueKey, Files.readAllBytes(Paths.get(testFile.getAbsolutePath())), Files.readAllBytes(Paths.get(scaffoldingTestFile.getAbsolutePath())));

	}
}
