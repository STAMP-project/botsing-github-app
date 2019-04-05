package eu.stamp.botsing.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.junit.Before;
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
import eu.stamp.botsing.utility.ConfigurationBeanForIntegrationTests;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { JGitService.class, ConfigurationBean.class, ConfigurationBeanForIntegrationTests.class})
public class JGitServiceTestIT {

	Logger log = LoggerFactory.getLogger(JGitServiceTestIT.class);

	@Autowired
	JGitService service;

	@Autowired
	ConfigurationBeanForIntegrationTests configuration;

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	private String repositoryURL;
	private File existingGitRepoPath;
	private String repositoryBranch;

	@Before
	public void before() throws IOException, IllegalStateException, GitAPIException {

		repositoryURL = configuration.getRemoteGitRepoUrl();
		repositoryBranch = configuration.getRemoteGitRepoBranch();
		existingGitRepoPath = new File (configuration.getExistingGitRepoPath());
	}

	@Test
	public void cloneRepositoryTest() throws InvalidRemoteException, TransportException, GitAPIException, IOException {

		File repoFolder = service.cloneRepository(repositoryURL, tmpFolder.newFolder());

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

		Repository repo = service.checkoutBranch(tmpFolder.newFolder(), repositoryBranch);

		log.info("Branch checked out: " + repo.getBranch());

		assertEquals(repositoryBranch, repo.getBranch());
	}

	@Test
	public void createNewBranchTest() throws Exception {

		String newBranch = "botsing-reproduction-branch-1-" + System.currentTimeMillis();
		Repository repo = service.createNewBranch(existingGitRepoPath, newBranch, true);

		log.info("Branch created: " + repo.getBranch());

		assertNotNull(repo.getBranch());
	}

	@Test
	public void addFolderTest() throws Exception {

		// Add sample java file
		File tempJavaFile = new File(existingGitRepoPath + File.separator + "src", "Sample.java");
		FileUtils.writeStringToFile(tempJavaFile, "public class HelloWorld {\n" +
				"\n" +
				"    public static void main(String[] args) {\n" +
				"        System.out.println(\"Hello, World\");\n" +
				"    }\n" +
				"\n" +
				"}", "UTF-8");

		service.addFolder(existingGitRepoPath, "src");
	}

	@Test
	public void commitAllTest() throws Exception {

		Repository repo = service.commitAll(existingGitRepoPath, "comment");

		log.info("Commit done! Repository status: " + repo.getRepositoryState().getDescription());

		assertNotNull(repo);
	}

	@Test
	public void pushTest() throws Exception {

		Repository repo = service.push(existingGitRepoPath, "botsing-reproduction-branch-7-1553618251964");

		log.info("Push done! Repository status: " + repo.getRepositoryState().getDescription());

		assertNotNull(repo);
	}
}
