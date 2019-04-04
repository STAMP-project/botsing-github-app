package eu.stamp.botsing.runner;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenRunnerTestIT {

	Logger log = LoggerFactory.getLogger(MavenRunnerTestIT.class);

	@Test
	public void compileWithoutTestsTest() {
		try {

			// Prerequisite (need git installed)
			// git clone https://github.com/STAMP-project/botsing.git /tmp/botsing

			MavenRunner.compileWithoutTests(new File("/tmp/botsing"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void runBotsingReproductionTest() {
		try {

			// Prerequisite (need git installed)
			// git clone https://github.com/STAMP-project/botsing.git /tmp/botsing
			// cd /tmp/botsing
			// mvn compile -DskipTests

			MavenRunner.runBotsingReproduction(new File("/tmp/botsing"),
					"/tmp/botsing/botsing-examples/src/main/resources/Fraction.log", "1", "1", "1800", "1800");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
