package eu.stamp.botsing.service;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.PullRequestMarker;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eu.stamp.botsing.utility.ConfigurationBean;

@Service
public class GitHubService implements CICDService 
{
	Logger log = LoggerFactory.getLogger(GitHubService.class);

	private GitHubClient client;

	public GitHubService(ConfigurationBean configuration) {
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
	}

	public String createPullRequest(String repositoryName, String repositoryOwner, String pullRequestTitle,
			String pullRequestBody, String branchSource, String branchDestination) throws IOException {
		log.debug("Creating pull request");

		RepositoryService repoService = new RepositoryService(client);
		Repository repository = repoService.getRepository(repositoryOwner, repositoryName);

		PullRequestService service = new PullRequestService(client);

		PullRequest request = new PullRequest();
		request.setTitle(pullRequestTitle);
		request.setBody(pullRequestBody);

		request.setHead(new PullRequestMarker().setRef(branchSource).setLabel(branchSource));
		request.setBase(new PullRequestMarker().setRef(branchDestination).setLabel(branchDestination));

		PullRequest pr = service.createPullRequest(repository, request);
		log.debug("Pull request '" + pr.getId() + "' created");

		return pr.getHtmlUrl();
	}

	@Override
	public String getRawFile(String repositoryName, String repositoryOwner, String filePath) throws IOException {
		log.debug("Reading file '" + filePath + "'");
		String result = null;

		RepositoryService repoService = new RepositoryService(client);
		Repository repository = repoService.getRepository(repositoryOwner, repositoryName);

		ContentsService contentsService = new ContentsService(client);

		try {
			List<RepositoryContents> contents = contentsService.getContents(repository, filePath);

			for (RepositoryContents c : contents) {

				byte[] out = Base64.getDecoder().decode(c.getContent().replaceAll("\n", ""));
				result = new String(out);
			}

			log.debug("File '" + filePath + "' read");

		} catch (RequestException e) {

			if (e.getStatus() == 404) {
				log.debug("File not found.");
			} else {
				throw e;
			}
		}

		return result;
	}

	@Override
	public String getIssueBody(String repositoryName, String repositoryOwner, String issueNumber) throws IOException {
		log.debug("Reading issue");
		String result = null;

		IssueService issueService = new IssueService(client);

		Issue issue = issueService.getIssue(repositoryOwner, repositoryName, issueNumber);
		result = issue.getBody();

		log.debug("Read issue");
		return result;
	}

	@Override
	public String createIssueComment(String repositoryName, String repositoryOwner, String issueNumber, String comment)
			throws IOException {

		IssueService issueService = new IssueService(client);
		Comment issueComment = issueService.createComment(repositoryOwner, repositoryName, issueNumber, comment);

		return issueComment.getBody();
	}

	@Override
	public String sendFile(String repositoryName, String repositoryOwner, String issueNumber, File file)
			throws IOException {
		log.debug("Creating new issue comment with file");

		String comment = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

		IssueService issueService = new IssueService(client);
		Comment issueComment = issueService.createComment(repositoryOwner, repositoryName, issueNumber, comment);

		log.debug("Issue comment created");
		return issueComment.getBody();
	}

}
