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
import eu.stamp.botsing.runner.MavenRunnerResponse;
import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.utility.FileUtility;

public abstract class BotsingExecutor <T extends TestFiles>  extends MavenRunner {

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
		
		MavenRunnerResponse mavenRunnerResponse = runMavenCommand(BOTSING_COMMAND, workingDir,this.botsingParameters.getMandatoryParameters(),this.botsingParameters.getOptionalParameters());
		// run Botsing using Maven
		if (!mavenRunnerResponse.isSuccess())
		{
			resultManager = processFailResult(mavenRunnerResponse.getLog());

		}
		else
		{
			T testFiles = generateTestFiles(workingDir);
			resultManager = processSuccessResult(testFiles, mavenRunnerResponse.getLog());
		}
	
		
		this.log.debug("Botsing execution completed with result " + resultManager.getBotsingResult());
		
		return resultManager;

	}
	
	protected abstract T generateTestFilesStructure ();
	
	protected abstract BotsingResultManager processFailResult (String mavenLog);
	
	protected abstract BotsingResultManager processSuccessResult (T testFiles, String mavenLog);

	private T generateTestFiles(File workingDir) throws IOException 
	{
		T testFiles = generateTestFilesStructure ();
		
		try
		{
			Iterator<File> filesIterator = FileUtility.search(workingDir.getAbsolutePath(),".*EvoSuite did not generate any tests.*", new String[] { "java" }).iterator();
			
			while (filesIterator.hasNext() && !testFiles.isCompleted()) 
			{
				File currentFile = filesIterator.next();
				String fileName = currentFile.getName();

				if (fileName.contains("scaffolding"))
					testFiles.setScaffoldingFile(fileName,currentFile);
				else
					testFiles.setDataFile(fileName,currentFile);
			}

		}
		catch (NullPointerException e)
		{
			log.error("Test files not found");
		}

		return testFiles;
		
	}



}
