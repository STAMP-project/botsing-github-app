package eu.stamp.botsing.controller.event.issues;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.io.Files;
import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.filter.ActionFilter;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.controller.utils.Constants;
import eu.stamp.botsing.runner.MavenRunner;
import eu.stamp.botsing.service.GitHubService;
import eu.stamp.botsing.utility.FileUtility;

@Configurable
@Component
public class GitHubIssuesActionOpened  implements GitHubIssuesAction{

	Logger log = LoggerFactory.getLogger(GitHubIssuesActionOpened.class);;
	
	private final String BOTSING_FILE = ".botsing";
	
	private final String GROUP_ID = "group_id";
	private final String ARTIFACT_ID = "artifact_id";
	private final String VERSION = "version";
	private final String SEARCH_BUDGET = "search_budget";
	private final String GLOBAL_TIMEOUT = "global_timeout";
	private final String POPULATION = "population";
	private final String MAX_TARGET_FRAME = "max_target_frame";
	private final String PACKAGE_FILTER = "package_filter";
	
	@Autowired
	private GitHubService githubService;

	@Autowired
	@Qualifier ("configuration")
	private ActionFilter actionFilter;
	
	private final String 	ACTION_OPENED ="opened",
							QUALIFIED_ACTION_NAME = GitHubIssuesActionFactory.EVENT_NAME+"."+ACTION_OPENED;
	

	
	
	@Override
	public ResponseBean execute (JsonObject jsonObject, String bodyString) throws Exception
	{
		ResponseBean response = null;
		// get repository information
		String repositoryName = jsonObject.get("repository").getAsJsonObject().get("name").getAsString();
		String repositoryURL  = jsonObject.get("repository").getAsJsonObject().get("html_url").getAsString();
		String repositoryOwner = jsonObject.get("repository").getAsJsonObject().get("owner").getAsJsonObject().get("login").getAsString();

		// get issue information
		String issueNumber = jsonObject.get("issue").getAsJsonObject().get("number").getAsString();
		String issueBody = jsonObject.get("issue").getAsJsonObject().get("body").getAsString();
		
		// read .botsing file
		String botsingFile = githubService.getRawFile(repositoryName, repositoryOwner, BOTSING_FILE);

		if (botsingFile == null) {

			response = new ResponseBean(500,".botsing file not found");

		} else {

			// read botsing properties
			Properties botsingProperties = FileUtility.parsePropertiesString(botsingFile);

			if (issueBody.length() > 0) {
				// TODO find a way to understand if this issue is a stacktrace that can be used by botsing

				log.debug("Received issue " + issueNumber + " with a stacktrace");
				response = runBotsingAsExternalProcess(issueBody, issueNumber, botsingProperties, repositoryName, repositoryURL, repositoryOwner);
	

			} else {
				response = new ResponseBean(400,"Received issue " + issueNumber + " without stacktrace, issue will be ignored.");

			}
		}

		return response;

}

	private ResponseBean runBotsingAsExternalProcess(String crashLog, String issueNumber, Properties botsingProperties,
			String repositoryName, String repositoryURL, String repositoryOwner) throws IOException {
		ResponseBean result = null;

		log.info("Reading Botsing properties for issue " + issueNumber);

		// prepare folder
		File workingDir = Files.createTempDir();

		// create dummy pom file
		File pomFile = new File(workingDir + (File.separator + "pom.xml"));
		FileUtils.writeStringToFile(pomFile, Constants.POM_FOR_BOTSING, Charset.defaultCharset());

		// create crashLog file
		File crashLogFile = new File(workingDir + (File.separator + "crash.log"));
		FileUtils.writeStringToFile(crashLogFile, crashLog, Charset.defaultCharset());

		// read properties from .botsing file
		String groupId = botsingProperties.getProperty(GROUP_ID);
		String artifactId = botsingProperties.getProperty(ARTIFACT_ID);
		String version = botsingProperties.getProperty(VERSION);
		String searchBudget = botsingProperties.getProperty(SEARCH_BUDGET);
		String globalTimeout = botsingProperties.getProperty(GLOBAL_TIMEOUT);
		String population = botsingProperties.getProperty(POPULATION);
		String maxTargetFrame = botsingProperties.getProperty(MAX_TARGET_FRAME);
		String packageFilter = botsingProperties.getProperty(PACKAGE_FILTER);

		// add a comment before running botsing
		githubService.createIssueComment(repositoryName, repositoryOwner, issueNumber,
				"Start Botsing on artifact " + groupId + ":" + artifactId + ":" + version + " to reproduce the stacktrace in the issue.");

		// run Botsing
		log.info("Running Botsing on " + workingDir.getAbsolutePath());
		boolean noErrors = MavenRunner.runBotsingReproductionWithMaxTargetFrame(workingDir,
				crashLogFile.getAbsolutePath(), groupId, artifactId, version, maxTargetFrame, population, searchBudget,
				globalTimeout, packageFilter);

		if (noErrors == false) 
		{
			String comment = "Error executing Botsing";
			result = new ResponseBean(500,comment);
			githubService.createIssueComment(repositoryName, repositoryOwner, issueNumber, comment);

		} else {

			// get generated file as stringGitHubIssuesActionFactory.EVENT_NAME+"."+ACTION_OPENED;
			Collection<File> testFiles = FileUtility.search(workingDir.getAbsolutePath(),
					".*EvoSuite did not generate any tests.*", new String[] { "java" });

			if (testFiles != null) {
				result = new ResponseBean(200,"Botsing executed succesfully with reproduction test.");

				githubService.createIssueComment(repositoryName, repositoryOwner, issueNumber,
						"Botsing generate the following reproduction test.");

				for (File test : testFiles) {
					// Add comment to the issue with the test
					githubService.createIssueCommentWithFile(repositoryName, repositoryOwner, issueNumber, test);
				}

			} else {

				String comment = "Botsing did not generate any reproduction test.";
				result = new ResponseBean(304, comment);
				githubService.createIssueComment(repositoryName, repositoryOwner, issueNumber, comment);
			}
		}

		return result;
	}



	@Override
	public String getActionName() 
	{

		return ACTION_OPENED;
	}

	@Override
	public String getQualifiedActionName() 
	{
		return  QUALIFIED_ACTION_NAME;
	}

	@Override
	public void applyFilter(JsonObject jsonObject) throws FilteredActionException {

		this.actionFilter.apply(GitHubIssuesActionFactory.EVENT_NAME, ACTION_OPENED,jsonObject);
		
	}


	
}
