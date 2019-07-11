package eu.stamp.botsing.service.github;

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
import eu.stamp.botsing.service.CICDService;
import eu.stamp.botsing.utility.ConfigurationBean;

@Service
public class GitHubService implements CICDService 
{
	Logger log = LoggerFactory.getLogger(GitHubService.class);
	private final String BOTSING_FILE= ".botsing";	
	private GitHubClient client;
	private String file;
	
	public GitHubService(ConfigurationBean configuration) 
	{
		super();
		client = new GitHubClient();

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
	public Properties getInputProperties(BotsingParameters parameters) throws IOException {
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


	@Override
	public String getData(BotsingParameters parameters) throws IOException {
		log.debug("Reading issue");
		String result = null;

		IssueService issueService = new IssueService(client);

		Issue issue = issueService.getIssue(parameters.getRepositoryOwner(), parameters.getRepositoryName(), parameters.getIssueNumber());
		result = issue.getBody();

		log.debug("Read issue");
		return result;
	}

	@Override
	public String sendDataString(BotsingParameters parameters, String dataString) throws IOException {
		IssueService issueService = new IssueService(client);
		Comment issueComment = issueService.createComment(parameters.getRepositoryOwner(), parameters.getRepositoryName(), parameters.getIssueNumber(), dataString);

		return issueComment.getBody();
	}

	@Override
	public String sendDataFile(BotsingParameters parameters, File file) throws IOException 
	{
		log.debug("Creating new issue comment with file");
		String comment = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		IssueService issueService = new IssueService(client);
		Comment issueComment = issueService.createComment(parameters.getRepositoryOwner(), parameters.getRepositoryName(), parameters.getIssueNumber(), comment);
		log.debug("Issue comment created");
		return issueComment.getBody();
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	

}
