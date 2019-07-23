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

import eu.stamp.botsing.controller.event.github.GitHubServiceClient;
import eu.stamp.botsing.utility.ConfigurationBean;
import eu.stamp.botsing.utility.ConfigurationBeanForIntegrationTests;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { GitHubServiceClient.class, ConfigurationBean.class, ConfigurationBeanForIntegrationTests.class})
public class GitHubServiceTestIT {

	Logger log = LoggerFactory.getLogger(GitHubServiceTestIT.class);

	@Autowired
	GitHubServiceClient serviceClient;

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
		serviceClient.setFile("pom.xml");
		BotsingParameters properties = serviceClient.getBotsingParameters(new IssueParameters(null, repoName, null, owner, null));
		assertNotNull(properties);
	}

	@Test
	public void shouldReturnNullFileTest() throws IOException {
		serviceClient.setFile("notexistingFile");
		BotsingParameters properties = serviceClient.getBotsingParameters(new IssueParameters(null, repoName, null, owner, null));
		assert(properties == null);
	}

	@Test
	public void getRawFileTest() throws IOException {
		serviceClient.setFile("pom.xml");
		BotsingParameters properties = serviceClient.getBotsingParameters(new IssueParameters(null, repoName, null, owner, null));
		assertNotNull(properties);
	}

//	@Test
//	public void createPullRequestTest() throws IOException {
//		service.createPullRequest(repoName, owner, "Pull request title", "Pull request body", "nuovo-branch", "master");
//	}

	@Test
	public void readIssueBodyIsNotNull() throws IOException {
		String body = serviceClient.getIssueData(new IssueParameters("4", repoName, null, owner, null));
		assertNotNull(body);
	}

	@Test
	public void createIssueCommentIsNotNull() throws IOException {
		String body = serviceClient.sendDataString(new IssueParameters("4", repoName, null, owner, null), "Comment by github app");

		assertNotNull(body);
	}
}