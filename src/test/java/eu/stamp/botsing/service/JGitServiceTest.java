package eu.stamp.botsing.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eu.stamp.botsing.utility.ConfigurationBean;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { JGitService.class, ConfigurationBean.class })
@Ignore
public class JGitServiceTest {

	/**
	 * TODO this test suite changes the remote repository used, this is not good.
	 * Also it need access to the repository.
	 *
	 * For now this test suite is DISABLED
	 */

	Logger log = LoggerFactory.getLogger(JGitServiceTest.class);

	@Autowired
	JGitService service;

	@Rule
	public TemporaryFolder repositoryFolder = new TemporaryFolder();

	/**
	 * Git repository to use for testing purpose
	 */
	String repositoryURL = "https://github.com/luandrea/testrepo-github-app.git";

	private File newRepoFolder;

	@Before
	public void before() throws IOException, IllegalStateException, GitAPIException {

		// initialize new local git repository
		newRepoFolder = repositoryFolder.newFolder();
		Git.init().setDirectory(newRepoFolder).call();

	}

	@Test
	public void cloneRepositoryTest() throws InvalidRemoteException, TransportException, GitAPIException, IOException {

		File repoFolder = service.cloneRepository(repositoryURL, repositoryFolder.newFolder());

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
	}

	@Test
	public void checkoutBranchTest() throws Exception {
		String branchToCheckOut = "jenkins_develop";

		Repository repo = service.checkoutBranch(repositoryFolder.newFolder(), branchToCheckOut);

		log.info("Branch checked out: " + repo.getBranch());

		assertEquals(branchToCheckOut, repo.getBranch());
	}

	@Test
	public void createNewBranchTest() throws Exception {

		String newBranch = "botsing-reproduction-branch-1-" + System.currentTimeMillis();
		Repository repo = service.createNewBranch(newRepoFolder, newBranch, true);

		log.info("Branch created: " + repo.getBranch());

		assertNotNull(repo.getBranch());
	}

	@Test
	public void addFolderTest() throws Exception {

		File existingRepoFolder = new File("/tmp/stamp-github-app-test");
		existingRepoFolder = new File("/tmp/1553616403110-0");

		service.addFolder(existingRepoFolder, "src");
	}

	@Test
	public void commitAllTest() {
		try {
			File existingRepoFolder = new File("/tmp/stamp-github-app-test");
			existingRepoFolder = new File("/tmp/1553616403110-0");

			Repository repo = service.commitAll(existingRepoFolder, "comment");

			log.info("Commit done! Repository status: " + repo.getRepositoryState().getDescription());

			assertNotNull(repo);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void pushTest() throws Exception {
		File existingRepoFolder = new File("/tmp/stamp-github-app-test");
		existingRepoFolder = new File("/tmp/1553618238645-0");

		Repository repo = service.push(existingRepoFolder, "botsing-reproduction-branch-7-1553618251964");

		log.info("Push done! Repository status: " + repo.getRepositoryState().getDescription());

		assertNotNull(repo);
	}
}
