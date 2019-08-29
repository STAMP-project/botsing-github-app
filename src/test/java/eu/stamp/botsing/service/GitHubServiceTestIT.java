package eu.stamp.botsing.service;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eu.stamp.botsing.controller.event.github.GitHubClientManager;
import eu.stamp.botsing.controller.event.github.GitHubCommentManager;
import eu.stamp.botsing.controller.event.github.GitHubDataManager;
import eu.stamp.botsing.utility.ConfigurationBean;
import eu.stamp.botsing.utility.ConfigurationBeanForIntegrationTests;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { GitHubClientManager.class, ConfigurationBean.class, ConfigurationBeanForIntegrationTests.class})
public class GitHubServiceTestIT {

	Logger log = LoggerFactory.getLogger(GitHubServiceTestIT.class);

	@Autowired
	GitHubClientManager clientManager;

	@Autowired
	ConfigurationBeanForIntegrationTests configuration;

	private String owner;
	private String repoName;

	@Before
	public void before() {
		owner = configuration.getRemoteGitRepoOwner();
		repoName = configuration.getRemoteGitRepoName();
	}


	@Test
	public void shouldGetPomFileTest() throws Exception {
		GitHubDataManager dataManager = new GitHubDataManager(clientManager, new IssueParameters(null, repoName, null, owner, null));
		dataManager.setFile("pom.xml");
		BotsingParameters properties = dataManager.getBotsingParameters();
		assertNotNull(properties);
	}

	@Test
	public void shouldReturnNullFileTest() throws IOException {
		GitHubDataManager dataManager = new GitHubDataManager(clientManager, new IssueParameters(null, repoName, null, owner, null));
		dataManager.setFile("notexistingFile");
		BotsingParameters properties = dataManager.getBotsingParameters();
		assert(properties == null);
	}

	@Test
	public void getRawFileTest() throws IOException {
		GitHubDataManager dataManager = new GitHubDataManager(clientManager, new IssueParameters(null, repoName, null, owner, null));
		dataManager.setFile("pom.xml");
		BotsingParameters properties = dataManager.getBotsingParameters();
		assertNotNull(properties);
	}

//	@Test
//	public void createPullRequestTest() throws IOException {
//		service.createPullRequest(repoName, owner, "Pull request title", "Pull request body", "nuovo-branch", "master");
//	}

	@Test
	public void readIssueBodyIsNotNull() throws IOException {
		GitHubDataManager dataManager = new GitHubDataManager(clientManager, new IssueParameters("4", repoName, null, owner, null));
		String body = dataManager.getIssueData();
		assertNotNull(body);
	}

	@Test
	public void createIssueCommentIsNotNull() throws IOException {
		GitHubCommentManager commentManager = new GitHubCommentManager(clientManager);
		String body = commentManager.sendComment(new IssueParameters("4", repoName, null, owner, null), "Comment by github app");

		assertNotNull(body);
	}
}