package eu.stamp.botsing.runner;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eu.stamp.botsing.utility.ConfigurationBeanForIntegrationTests;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ConfigurationBeanForIntegrationTests.class })
public class MavenRunnerTestIT {

	Logger log = LoggerFactory.getLogger(MavenRunnerTestIT.class);

	@Autowired
	ConfigurationBeanForIntegrationTests configuration;

	private String existingGitRepoCrashLog;
	private String existingGitRepoTargetFrame;
	private String existingGitRepoPopulation;
	private String existingGitRepoSearchBudget;
	private String existingGitRepoTimeout;

	private File existingGitRepoPathFile;

	@Before
	public void before() throws IOException {

		existingGitRepoCrashLog = configuration.getExistingGitRepoCrashLog();
		existingGitRepoTargetFrame = configuration.getExistingGitRepoTargetFrame();
		existingGitRepoPopulation = configuration.getExistingGitRepoPopulation();
		existingGitRepoSearchBudget = configuration.getExistingGitRepoSearchBudget();
		existingGitRepoTimeout = configuration.getExistingGitRepoTimeout();

		existingGitRepoPathFile = new File(configuration.getExistingGitRepoPath());
	}

	@Test
	public void compileWithoutTestsTest() throws IOException {
		MavenRunner.compileWithoutTests(existingGitRepoPathFile);
	}

	@Test
	public void runBotsingReproductionTest() throws IOException {

		MavenRunner.runBotsingReproduction(existingGitRepoPathFile, existingGitRepoCrashLog, existingGitRepoTargetFrame,
				existingGitRepoPopulation, existingGitRepoSearchBudget, existingGitRepoTimeout);
	}
}
