package eu.stamp.botsing.service.jira;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.service.CICDService;
import eu.stamp.botsing.utility.ConfigurationBean;

@Service
public class JiraService implements CICDService 
{
	Logger log = LoggerFactory.getLogger(JiraService.class);
	private String jiraURL;

	public JiraService(ConfigurationBean configuration) 
	{
		this.jiraURL = configuration.getJiraURL();
	}

//	public String createPullRequest(String repositoryName, String repositoryOwner, String pullRequestTitle,
//			String pullRequestBody, String branchSource, String branchDestination) throws IOException 
//	{
//		log.debug("Creating pull request");
//
//		RepositoryService repoService = new RepositoryService(client);
//		Repository repository = repoService.getRepository(repositoryOwner, repositoryName);
//
//		PullRequestService service = new PullRequestService(client);
//
//		PullRequest request = new PullRequest();
//		request.setTitle(pullRequestTitle);
//		request.setBody(pullRequestBody);
//
//		request.setHead(new PullRequestMarker().setRef(branchSource).setLabel(branchSource));
//		request.setBase(new PullRequestMarker().setRef(branchDestination).setLabel(branchDestination));
//
//		PullRequest pr = service.createPullRequest(repository, request);
//		log.debug("Pull request '" + pr.getId() + "' created");
//
//		return pr.getHtmlUrl();
//	}

	@Override
	public Properties getInputProperties(BotsingParameters parameters) throws IOException 
	{
		Properties botsingProperties = new Properties();

		return botsingProperties;
	}


	@Override
	public String getData(BotsingParameters parameters) throws IOException {

		return null;
	}

	@Override
	public String sendDataString(BotsingParameters parameters, String dataString) throws RestClientException, URISyntaxException {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(new URI (this.jiraURL), dataString, String.class);
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) 
		{
			return responseEntity.getBody();
		}
		else
		{
			return null;
		}
		
	}

	@Override
	public String sendDataFile(BotsingParameters parameters, File file) throws RestClientException, URISyntaxException, IOException  
	{
		log.debug("Creating new issue comment with file");
		String base64Data = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		return sendDataString (parameters, base64Data);
	}

}
