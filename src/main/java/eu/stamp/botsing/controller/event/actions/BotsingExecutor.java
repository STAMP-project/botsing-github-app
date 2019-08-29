package eu.stamp.botsing.controller.event.actions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import eu.stamp.botsing.controller.utils.Constants;
import eu.stamp.botsing.runner.MavenRunner;
import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.utility.FileUtility;

public abstract class BotsingExecutor extends MavenRunner {

	private Logger log = LoggerFactory.getLogger(BotsingExecutor.class);

	private final String BOTSING_COMMAND = "eu.stamp-project:botsing-maven:botsing";
	private BotsingParameters botsingParameters;
	private String issueBody;


	public BotsingExecutor(BotsingParameters botsingParameters, String issueBody) {
		this.botsingParameters = botsingParameters;
		this.issueBody = issueBody;
	}

	public BotsingResultManager runBotsing() throws Exception 
	{
		BotsingResultManager resultManager;
		File workingDir = Files.createTempDir();
		this.log.debug("Running botsing...");

		// create dummy pom file
		File pomFile = new File(workingDir + (File.separator + "pom.xml"));
		FileUtils.writeStringToFile(pomFile, Constants.POM_FOR_BOTSING, Charset.defaultCharset());

		// create crashLog file
		File crashLogFile = new File(workingDir + (File.separator + "crash.log"));
		FileUtils.writeStringToFile(crashLogFile, this.issueBody, Charset.defaultCharset());
		this.botsingParameters.setCrashLogFile(crashLogFile);
		
		// run Botsing using Maven
		if (!runMavenCommand(BOTSING_COMMAND, workingDir,this.botsingParameters.getMandatoryParameters(),this.botsingParameters.getOptionalParameters()))
		{
			resultManager = processFailResult(crashLogFile);

		}
		else
		{
			File [] testFiles = generateTestFiles(workingDir);
			resultManager = processSuccessResult(testFiles, crashLogFile);
		}
	
		
		this.log.debug("Botsing execution completed with result " + resultManager.getBotsingResult());
		
		return resultManager;

	}
	
	protected abstract BotsingResultManager processFailResult (File crashLogFile);
	
	protected abstract BotsingResultManager processSuccessResult (File [] testFiles, File crashLogFile);

	private File [] generateTestFiles(File workingDir) throws IOException 
	{
		File [] testFiles = null;
		
		try
		{
			Iterator<File> filesIterator = FileUtility.search(workingDir.getAbsolutePath(),".*EvoSuite did not generate any tests.*", new String[] { "java" }).iterator();
			testFiles = new File [2];

			
			while (filesIterator.hasNext() && (testFiles [0] == null || testFiles [1] == null)) 
			{
				File currentFile = filesIterator.next();
				String fileName = currentFile.getName();

				if (fileName.contains("scaffolding"))
					testFiles [1]  = currentFile;
				else
					testFiles [0]  = currentFile;

			}

			
		}
		catch (NullPointerException e)
		{
			log.error("Test files not found");
		}

		return testFiles;
		
	}



}
