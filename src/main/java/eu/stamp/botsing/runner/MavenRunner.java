package eu.stamp.botsing.runner;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenRunner {

	static Logger log = LoggerFactory.getLogger(MavenRunner.class);

	public static void compileWithoutTests(File projectFolder) throws IOException {
		log.debug("Compiling project with Maven");

		try {
			executeProcess(projectFolder, "mvn", "clean", "compile", "-DskipTests");
			log.debug("Project compiled with Maven");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void runBotsingReproduction(File projectFolder, String crashLogFile, String targetFrame, String population, String searchBudget, String globalTimeout) throws IOException {
		// mvn eu.stamp-project:botsing-maven:1.0.4-SNAPSHOT:botsing -Dcrash_log=LANG-9b.log -Dtarget_frame=2 -Dorg.slf4j.simpleLogger.log.org.evosuite=off -Dorg.slf4j.simpleLogger.showLogName=true
		log.debug("Executing Botsing");

		try {
			// TODO remove SNAPSHOT version
			// TODO add optional parameters (population, searchBudget, globalTimeout, testDir)
			executeProcess(projectFolder, "mvn", "eu.stamp-project:botsing-maven:1.0.5-SNAPSHOT:botsing",
					"-Dcrash_log=" + crashLogFile, "-Dtarget_frame=targetFrame",
					"-Dorg.slf4j.simpleLogger.log.org.evosuite=off", "-Dorg.slf4j.simpleLogger.showLogName=true");

			log.debug("Botsing executed successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void executeProcess(File workDir, String... command) throws InterruptedException, IOException {
		ProcessBuilder builder = new ProcessBuilder(command);

		builder.directory(workDir.getAbsoluteFile());
		builder.redirectErrorStream(true);

		Process process = builder.start();

		Scanner s = new Scanner(process.getInputStream());
		StringBuilder text = new StringBuilder();
		while (s.hasNextLine()) {
			text.append(s.nextLine());
			text.append("\n");
		}
		s.close();

		int result = process.waitFor();

		log.debug("Process exited with result {} and output {} ", result, text);
	}

}