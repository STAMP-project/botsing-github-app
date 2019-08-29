package eu.stamp.botsing.runner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenRunner {

	private Logger log = LoggerFactory.getLogger(MavenRunner.class);

	public boolean runMavenCommand(String commandString, File workingDir, Map<String, String> mandatoryParameters, 
			Map<String,String> optionalParameters) throws IOException {

		try {
			// mandatory parameters
			List<String> command = new ArrayList<String>(Arrays.asList("mvn",commandString));
			this.addParameters (mandatoryParameters,command);
			this.addParameters(optionalParameters, command);
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
	

	private void addParameters (Map<String, String> parameters, List<String> commands)
	{
		Iterator<String> namesIterator = parameters.keySet().iterator();
		
		while (namesIterator.hasNext())
		{
			String name = namesIterator.next();
			String value = parameters.get(name);
			commands.add("-D"+name+"="+value);
		}
	}
	


	private int executeProcess(File workDir, List<String> command) throws InterruptedException, IOException 
	{
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