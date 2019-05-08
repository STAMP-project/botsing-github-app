package eu.stamp.botsing.runner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eu.stamp.botsing.controller.GitHubAppController;
import eu.stamp.botsing.utility.ConfigurationBeanForIntegrationTests;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ConfigurationBeanForIntegrationTests.class })
public class MavenRunnerTestIT {

	Logger log = LoggerFactory.getLogger(MavenRunnerTestIT.class);

	@Autowired
	ConfigurationBeanForIntegrationTests configuration;

	private final String crashLog = "java.lang.RuntimeException: Failed to load XML schemas: [classpath:pdp.xsd]\n" +
			"	at org.ow2.authzforce.core.pdp.impl.SchemaHandler.createSchema(SchemaHandler.java:541)\n" +
			"	at org.ow2.authzforce.core.pdp.impl.PdpModelHandler.<init>(PdpModelHandler.java:159)\n" +
			"	at org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration.getInstance(PdpEngineConfiguration.java:682)\n" +
			"	at org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration.getInstance(PdpEngineConfiguration.java:699)\n" +
			"	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n" +
			"	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
			"	at java.lang.reflect.Method.invoke(Method.java:498)\n" +
			"	at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:162)";

	private final String groupId="org.ow2.authzforce";
	private final String artifactId="authzforce-ce-core-pdp-testutils";
	private final String version="13.3.1";
	private final String population="100";
	private final String searchBudget="60";
	private final String globalTimeout="90";

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	@Test
	public void runBotsingReproductionTest() throws IOException {

		File workingDir = tmpFolder.newFolder();

		// create dummy pom file
		File pomFile = new File(workingDir + (File.separator + "pom.xml"));
		FileUtils.writeStringToFile(pomFile, GitHubAppController.POM_FOR_BOTSING, Charset.defaultCharset());

		// create crashLog file
		File crashLogFile = new File(workingDir + (File.pathSeparatorChar + "crash.log"));
		FileUtils.writeStringToFile(crashLogFile, crashLog, Charset.defaultCharset());

		boolean  runnedWithoutErrors = MavenRunner.runBotsingReproductionWithMaxTargetFrame(workingDir, crashLogFile.getAbsolutePath(), groupId, artifactId,
				version, null, population, searchBudget, globalTimeout);

		assert(runnedWithoutErrors);
	}
}
