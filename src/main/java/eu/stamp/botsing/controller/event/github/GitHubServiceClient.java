package eu.stamp.botsing.controller.event.github;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import org.apache.activemq.util.ByteArrayInputStream;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.service.IssueParameters;
import eu.stamp.botsing.utility.ConfigurationBean;

@Service
public class GitHubServiceClient {

	Logger log = LoggerFactory.getLogger(GitHubServiceClient.class);
	private final String BOTSING_FILE= ".botsing";	
	
	private GitHubClient client;
	private String file;
	
	private final String GROUP_ID = "group_id";
	private final String ARTIFACT_ID = "artifact_id";
	private final String VERSION = "version";
	private final String SEARCH_BUDGET = "search_budget";
	private final String GLOBAL_TIMEOUT = "global_timeout";
	private final String POPULATION = "population";
	private final String PACKAGE_FILTER = "package_filter";
	
	public GitHubServiceClient(ConfigurationBean configuration) 
	{
		super();
		String gitHubURL = configuration.getGithubURL();
		
		client = gitHubURL == null || gitHubURL.trim().isEmpty() ? new GitHubClient() : new GitHubClient(gitHubURL);
		
		// set credentials
		if (configuration.getGithubOAuth2Token() != null || configuration.getGithubOAuth2Token().length() > 0) {
			client.setOAuth2Token(configuration.getGithubOAuth2Token());

		} else if (configuration.getGithubUsername() != null || configuration.getGithubUsername().length() > 0) {
			client.setCredentials(configuration.getGithubUsername(), configuration.getGithubPassword());
		} else {
			log.warn("No GitHub credentials provided");
		}

		client.setHeaderAccept("application/vnd.github.machine-man-preview+json");

		if (configuration.getProxyHost() != null && configuration.getProxyPort() != null) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP,
					new InetSocketAddress(configuration.getProxyHost(), configuration.getProxyPort()));
			client.setProxy(proxy);
		}
		
		this.file = BOTSING_FILE;
	}
	
	

	
	public BotsingParameters getBotsingParameters (IssueParameters issueParameters) throws IOException
	{
		Properties botsingProperties = getInputProperties(issueParameters);
		
		return new BotsingParameters(botsingProperties.getProperty(GROUP_ID), botsingProperties.getProperty(ARTIFACT_ID),
				botsingProperties.getProperty(VERSION), botsingProperties.getProperty(SEARCH_BUDGET), botsingProperties.getProperty(GLOBAL_TIMEOUT),
				botsingProperties.getProperty(POPULATION), botsingProperties.getProperty(PACKAGE_FILTER));		
	}
	
	private Properties getInputProperties(IssueParameters parameters) throws IOException {
		log.debug("Reading file '" + this.file + "'");
		Properties botsingProperties = null;

		RepositoryService repoService = new RepositoryService(client);
		Repository repository = repoService.getRepository(parameters.getRepositoryOwner(), parameters.getRepositoryName());

		ContentsService contentsService = new ContentsService(client);

		try 
		{
			List<RepositoryContents> contents = contentsService.getContents(repository, this.file);

			for (RepositoryContents c : contents) 
			{
				byte[] out = Base64.getDecoder().decode(c.getContent().replaceAll("\n", ""));
				botsingProperties = new Properties();
			    botsingProperties.load(new ByteArrayInputStream(out));
			
			}

			log.debug("File '" + this.file + "' read");

		} catch (RequestException e) 
		{

			if (e.getStatus() == 404) 
			{
				log.debug("File not found.");
			} else 
			{
				throw e;
			}
		}
		return botsingProperties;
	}
	
	
	public String sendDataString(IssueParameters parameters, String dataString) throws IOException {
		IssueService issueService = new IssueService(client);
		Comment issueComment = issueService.createComment(parameters.getRepositoryOwner(), parameters.getRepositoryName(), parameters.getIssueNumber(), dataString);

		return issueComment.getBody();
	}
	
	public String sendDataFile(IssueParameters parameters, File file) throws IOException 
	{
		log.debug("Creating new issue comment with file");
		String comment = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		IssueService issueService = new IssueService(client);
		Comment issueComment = issueService.createComment(parameters.getRepositoryOwner(), parameters.getRepositoryName(), parameters.getIssueNumber(), comment);
		log.debug("Issue comment created");
		return issueComment.getBody();
	}

	public String getIssueData(IssueParameters parameters) throws IOException {
		log.debug("Reading issue");
		String result = null;

		IssueService issueService = new IssueService(client);

		Issue issue = issueService.getIssue(parameters.getRepositoryOwner(), parameters.getRepositoryName(), parameters.getIssueNumber());
		result = issue.getBody();

		log.debug("Read issue");
		return result;
	}
	
	public void setFile (String file)
	{
		this.file = file;
	}


}
