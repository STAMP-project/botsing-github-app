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

import eu.stamp.botsing.utility.ConfigurationBean;
import eu.stamp.botsing.utility.ConfigurationBeanForIntegrationTests;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { GitHubService.class, ConfigurationBean.class, ConfigurationBeanForIntegrationTests.class})
public class GitHubServiceTestIT {

	Logger log = LoggerFactory.getLogger(GitHubServiceTestIT.class);

	@Autowired
	GitHubService service;

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
	public void shouldGetPomFileTest() throws IOException {
		String pom = service.getRawFile(repoName, owner, "pom.xml");
		assertNotNull(pom);
	}

	@Test
	public void shouldReturnNullFileTest() throws IOException {
		String nullfile = service.getRawFile(repoName, owner, "notexistingFile");
		assert(nullfile == null);
	}

	@Test
	public void getRawFileTest() throws IOException {
		String pom = service.getRawFile(repoName, owner, "pom.xml");
		assertNotNull(pom);
	}

	@Test
	public void createPullRequestTest() throws IOException {
		service.createPullRequest(repoName, owner, "Pull request title", "Pull request body", "nuovo-branch", "master");
	}

	@Test
	public void readIssueBodyIsNotNull() throws IOException {
		String body = service.getIssueBody(repoName, owner, "4");

		assertNotNull(body);
	}

	@Test
	public void createIssueCommentIsNotNull() throws IOException {
		String body = service.createIssueComment(repoName, owner, "7", "Comment by github app");

		assertNotNull(body);
	}
}