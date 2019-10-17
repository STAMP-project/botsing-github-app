package eu.stamp.botsing.controller.event.github;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import org.apache.activemq.util.ByteArrayInputStream;
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

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.actions.NotificationDataBean;
import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.service.IssueParameters;

public class GitHubDataManager implements NotificationDataBean{

	Logger log = LoggerFactory.getLogger(GitHubDataManager.class);
	private final String BOTSING_FILE= ".botsing";	
	private final String GROUP_ID = "group_id";
	private final String ARTIFACT_ID = "artifact_id";
	private final String VERSION = "version";
	private final String SEARCH_BUDGET = "search_budget";
	private final String GLOBAL_TIMEOUT = "global_timeout";
	private final String POPULATION = "population";
	private final String PACKAGE_FILTER = "package_filter";

	private String file;
	private GitHubClient client;
	private JsonObject jsonObject;
	
	private IssueParameters issueParameters;
	private BotsingParameters botsingParameters;
	
	
	public GitHubDataManager(GitHubClientManager clientManager, JsonObject jsonObject)
	{
		this.client = clientManager.getGitHubClient();
		this.jsonObject = jsonObject;
		this.issueParameters = null; 
		this.botsingParameters = null; 
		this.file = BOTSING_FILE;
	}
	
	public GitHubDataManager(GitHubClientManager clientManager, IssueParameters issueParameters)
	{
		this.client = clientManager.getGitHubClient();
		this.issueParameters = issueParameters;
		this.botsingParameters = null;
		this.file = BOTSING_FILE;
	}
	
	private IssueParameters generateIssueParameters (JsonObject jsonObject)
	{
		IssueParameters response = new IssueParameters(jsonObject.get("issue").getAsJsonObject().get("number").getAsString(), 
				jsonObject.get("repository").getAsJsonObject().get("name").getAsString(),jsonObject.get("repository").getAsJsonObject().get("html_url").getAsString(),
				jsonObject.get("repository").getAsJsonObject().get("owner").getAsJsonObject().get("login").getAsString(), 
				jsonObject.get("issue").getAsJsonObject().get("body").getAsString());
		
		return response;
	}
	
	@Override
	public IssueParameters getIssueParameters () throws IOException
	{
		if (this.issueParameters == null) this.issueParameters = generateIssueParameters(this.jsonObject);
		
		return this.issueParameters;
	}
	

	public String getIssueData() throws IOException {
		log.debug("Reading issue");
		String result = null;

		IssueService issueService = new IssueService(client);

		Issue issue = issueService.getIssue(issueParameters.getRepositoryOwner(), issueParameters.getRepositoryName(), issueParameters.getIssueNumber());
		result = issue.getBody();

		log.debug("Read issue");
		return result;
	}

	
	private BotsingParameters generateBotsingParameters (IssueParameters issueParameters) throws IOException
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
	
	public void setFile (String file)
	{
		this.file = file;
		this.issueParameters = null;
		this.botsingParameters = null;
	}

	@Override
	public JsonObject getJsonObject() 
	{
		return this.jsonObject;
	}



	@Override
	public BotsingParameters getBotsingParameters()  throws IOException{

		if (this.botsingParameters == null) this.botsingParameters = generateBotsingParameters(getIssueParameters());
		
		return this.botsingParameters;
	}

}
