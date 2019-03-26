package eu.stamp.botsing.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.eclipse.jgit.lib.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eu.stamp.botsing.utility.ConfigurationBean;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JGitService.class, ConfigurationBean.class})
public class JGitServiceTest {

	Logger log = LoggerFactory.getLogger(JGitServiceTest.class);

	@Autowired
	JGitService service;

	@Before
	public void before() {

	}

	@Test
	public void cloneRepositoryTest() {
		try {
			String repositoryURL = "https://github.com/luandrea/testrepo-github-app.git";
			File newRepoFolder = new File("/tmp/stamp-github-app-test");

			File repoFolder = service.cloneRepository(repositoryURL, newRepoFolder);

			log.info("Repository cloned in: " + repoFolder.getAbsolutePath());

			File found = null;
			File[] files = repoFolder.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.exists()) {
						found = file;
					}
				}
			}
			assertNotNull(found);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void checkoutBranchTest() {
		try {
			String branchToCheckOut = "jenkins_develop";

			File repoFolder = new File("/tmp/1545388542865-0");

			Repository repo = service.checkoutBranch(repoFolder, branchToCheckOut);

			log.info("Branch checked out: " +repo.getBranch());

			assertEquals(branchToCheckOut, repo.getBranch());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void createNewBranchTest() {
		try {
			File existingRepoFolder = new File("/tmp/stamp-github-app-test");

			Repository repo = service.createNewBranch(existingRepoFolder, "nuovo-branch", true);

			log.info("Branch created: " +repo.getBranch());

			assertNotNull(repo);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void commitAllTest() {
		try {
			File existingRepoFolder = new File("/tmp/stamp-github-app-test");

			Repository repo = service.commitAll(existingRepoFolder, "comment");

			log.info("Commit done! Repository status: " +repo.getRepositoryState().getDescription());

			assertNotNull(repo);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void pushTest() {
		try {
			File existingRepoFolder = new File("/tmp/stamp-github-app-test");

			Repository repo = service.push(existingRepoFolder, "nuovo-branch");

			log.info("Push done! Repository status: " +repo.getRepositoryState().getDescription());

			assertNotNull(repo);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
