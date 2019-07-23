package eu.stamp.botsing.controller.event.actions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import eu.stamp.botsing.controller.utils.Constants;
import eu.stamp.botsing.runner.MavenRunner;
import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.utility.FileUtility;


public class BotsingExecutor  extends MavenRunner{

	private Logger log = LoggerFactory.getLogger(BotsingExecutor.class);	
	private final String 	BOTSING_COMMAND = "eu.stamp-project:botsing-maven:1.0.6-SNAPSHOT:botsing";
	private BotsingParameters botsingParameters;
	private String issueBody;
	private File 	testFile,
					scaffoldingTestFile;
	
	public enum BotsingResult 
	{
		OK,
		NO_FILES,
		FAIL;
	}
	
	public BotsingExecutor (BotsingParameters botsingParameters, String issueBody)
	{
		this.botsingParameters = botsingParameters;
		this.issueBody = issueBody;
	}
	
	public BotsingResult runBotsing () throws Exception 
	{
		File workingDir = Files.createTempDir();
		this.log.debug("Running botsing...");
		// create dummy pom file
		File pomFile = new File(workingDir + (File.separator + "pom.xml"));
		FileUtils.writeStringToFile(pomFile, Constants.POM_FOR_BOTSING, Charset.defaultCharset());
		// create crashLog file
		File crashLogFile = new File(workingDir + (File.separator + "crash.log"));
		FileUtils.writeStringToFile(crashLogFile, this.issueBody, Charset.defaultCharset());
		boolean result = runMavenCommand(BOTSING_COMMAND, workingDir, this.botsingParameters.getMandatoryParameters(crashLogFile), this.botsingParameters.getOptionalParameters());
		this.log.debug("Botsing execution completed with result "+result);
		
		if (!result) return BotsingResult.FAIL;
		else return generateFiles (workingDir);

	}
	
	private BotsingResult generateFiles (File workingDir) throws IOException
	{
		Collection<File> testFiles = FileUtility.search(workingDir.getAbsolutePath(),
				".*EvoSuite did not generate any tests.*", new String[] { "java" });
		
		if (testFiles != null)
		{
			Iterator<File> filesIterator = testFiles.iterator();
			
			while (filesIterator.hasNext() && (this.testFile == null || this.scaffoldingTestFile == null))
			{
				File currentFile = filesIterator.next();
				String fileName = currentFile.getName();
				
				if (fileName.contains("scaffolding")) this.scaffoldingTestFile = currentFile;
				else this.testFile = currentFile;
				
			}
			
			if (this.testFile == null || this.scaffoldingTestFile == null) return BotsingResult.NO_FILES;
			else return BotsingResult.OK;
		}
		else return BotsingResult.NO_FILES;
				
	}

	public File getTestFile() {
		return testFile;
	}

	public File getScaffoldingTestFile() {
		return scaffoldingTestFile;
	}
	

	
	
}
