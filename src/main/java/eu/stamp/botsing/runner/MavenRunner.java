package eu.stamp.botsing.runner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenRunner {

	static Logger log = LoggerFactory.getLogger(MavenRunner.class);

	public static boolean runBotsingReproductionWithMaxTargetFrame(File workingDir, String crashLogFile, String groupId,
			String artifactId, String version, String maxTargetFrame, String population, String searchBudget,
			String globalTimeout) throws IOException {

		try {
			// mandatory parameters
			List<String> command = new ArrayList<String>(Arrays.asList("mvn",
					"eu.stamp-project:botsing-maven:botsing", "-Dcrash_log=" + crashLogFile,
					"-Dgroup_id=" + groupId, "-Dartifact_id=" + artifactId, "-Dversion=" + version));

			// optional parameters
			addOptionaParameter("search_budget", searchBudget, command);
			addOptionaParameter("global_timeout", globalTimeout, command);
			addOptionaParameter("population", population, command);

			int exitCode = executeProcess(workingDir, command);

			if (exitCode != 0) {
				return false;
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static void addOptionaParameter(String name, String value, List<String> list) {
		if (value != null) {
			list.add("-D"+name+"="+value);
		}
	}

	private static int executeProcess(File workDir, List<String> command) throws InterruptedException, IOException {
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
		return result;
	}

}