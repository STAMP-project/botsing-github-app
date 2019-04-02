package eu.stamp.botsing.service;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eu.stamp.botsing.utility.ConfigurationBean;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { GitHubService.class, ConfigurationBean.class })
@Ignore
public class GitHubServiceTest {

	/**
	 * TODO this test suite changes the remote repository used, this is not good.
	 * Also it need access to the repository.
	 *
	 * For now this test suite is DISABLED
	 */

	Logger log = LoggerFactory.getLogger(GitHubServiceTest.class);

	@Autowired
	GitHubService service;

	@Before
	public void before() {
	}

	@Test
	public void createPullRequestTest() throws IOException {
		String owner = "luandrea";
		String repoName = "testrepo-github-app";

		service.createPullRequest(repoName, owner, "Pull request title", "Pull request body", "nuovo-branch", "master");
	}

	@Test
	public void readIssueBodyIsNotNull() throws IOException {
		String owner = "luandrea";
		String repoName = "testrepo-github-app";

		String body = service.getIssueBody(repoName, owner, "4");

		assertNotNull(body);
	}

	@Test
	public void createIssueCommentIsNotNull() throws IOException {
		String owner = "luandrea";
		String repoName = "testrepo-github-app";

		String body = service.createIssueComment(repoName, owner, "7", "Comment by github app");

		assertNotNull(body);
	}
}