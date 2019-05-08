package eu.stamp.botsing.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.stamp.botsing.runner.MavenRunner;
import eu.stamp.botsing.service.GitHubService;
import eu.stamp.botsing.utility.FileUtility;

@RestController
public class GitHubAppController {

	Logger log = LoggerFactory.getLogger(GitHubAppController.class);

	private final String BOTSING_FILE = ".botsing";
	private final String GROUP_ID = "group_id";
	private final String ARTIFACT_ID = "artifact_id";
	private final String VERSION = "version";
	private final String SEARCH_BUDGET = "search_budget";
	private final String GLOBAL_TIMEOUT = "global_timeout";
	private final String POPULATION = "population";
	private final String MAX_TARGET_FRAME = "max_target_frame";

	@Autowired
	private GitHubService githubService;

	public static final String POM_FOR_BOTSING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
			"    <modelVersion>4.0.0</modelVersion>\n" +
			"    <groupId>eu.stamp-project</groupId>\n" +
			"    <artifactId>botsing-maven-working-project</artifactId>\n" +
			"    <version>1.0.0-SNAPSHOT</version>\n" +
			"    <packaging>pom</packaging>\n" +
			"    <name>Project to run Botsing Maven</name>\n" +
			"    <description>Project to run botsing-maven.</description>\n" +
			"</project>";

	@RequestMapping("/test")
	public String greeting() {
		return "This is the Botsing GitHub App Test Service. More informations can be found here: https://github.com/STAMP-project/botsing-github-app";
	}

	@PostMapping(value = "/botsing-github-app")
	public Map<String, String> getPullRequestFullBody(HttpServletRequest request,
			@RequestHeader(value = "X-GitHub-Event", defaultValue = "") String eventType) {
		HashMap<String, String> response = new HashMap<>();

		// TODO use HTTP 400 Bad Request if mandatory parameters are missing

		try {
			log.debug("'" + eventType + "' Event received");

			// get JSON Object from request
			JsonObject jsonObject = getJSonObjectFromRequest(request);

			// issues Event
			if (eventType.equals("issues")) {
				String action = jsonObject.get("action").getAsString();

				if (action.equals("opened")) {

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
						response.put("message", ".botsing file not found");

					} else {

						// read botsing properties
						Properties botsingProperties = FileUtility.parsePropertiesString(botsingFile);

						if (issueBody.length() > 0) {
							// TODO find a way to understand if this issue is a stacktrace that can be used by botsing

							log.debug("Received issue " + issueNumber + " with a stacktrace");

							String message = runBotsingAsExternalProcess(issueBody, issueNumber, botsingProperties, repositoryName, repositoryURL, repositoryOwner);

							response.put("message", message);

						} else {
							response.put("message", "Received issue " + issueNumber + " without stacktrace, issue will be ignored.");
						}
					}

				} else if (action.equals("edited")) {

//					JsonObject changes = jsonObject.get("changes").getAsJsonObject();

					// TODO: I could check if there is some change in the parameters that are used to run the reproduction

					response.put("message", "Issue action '"+action+"' is not supported.");

				} else {
					response.put("message", "Issue action '"+action+"' is not supported.");
				}

			} else {
				response.put("message", "Event '" + eventType + "' is not supported.");
			}

		} catch (Exception e) {
			log.error("Error parsing event", e);
		}

		return response;
	}

	public String runBotsingAsExternalProcess(String crashLog, String issueNumber, Properties botsingProperties,
			String repositoryName, String repositoryURL, String repositoryOwner) throws IOException {
		String result = null;

		log.info("Reading Botsing properties for issue " + issueNumber);

		// prepare folder
		File workingDir = Files.createTempDir();

		// create dummy pom file
		File pomFile = new File(workingDir + (File.separator + "pom.xml"));
		FileUtils.writeStringToFile(pomFile, POM_FOR_BOTSING, Charset.defaultCharset());

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

		// add a comment before running botsing
		githubService.createIssueComment(repositoryName, repositoryOwner, issueNumber,
				"Start Botsing on artifact " + groupId + ":" + artifactId + ":" + version + " to reproduce the stacktrace in the issue.");

		// run Botsing
		log.info("Running Botsing on " + workingDir.getAbsolutePath());
		boolean noErrors = MavenRunner.runBotsingReproductionWithMaxTargetFrame(workingDir,
				crashLogFile.getAbsolutePath(), groupId, artifactId, version, maxTargetFrame, population, searchBudget,
				globalTimeout);

		if (noErrors == false) {
			result = "Error executing Botsing";
			githubService.createIssueComment(repositoryName, repositoryOwner, issueNumber, result);

		} else {

			// get generated file as string
			Collection<File> testFiles = FileUtility.search(workingDir.getAbsolutePath(),
					".*EvoSuite did not generate any tests.*", new String[] { "java" });

			if (testFiles != null) {
				result = "Botsing executed succesfully with reproduction test.";

				githubService.createIssueComment(repositoryName, repositoryOwner, issueNumber,
						"Botsing generate the following reproduction test.");

				for (File test : testFiles) {
					// Add comment to the issue with the test
					githubService.createIssueCommentWithFile(repositoryName, repositoryOwner, issueNumber, test);
				}

			} else {

				result = "Botsing did not generate any reproduction test.";
				githubService.createIssueComment(repositoryName, repositoryOwner, issueNumber, result);
			}
		}

		return result;
	}

	private static JsonObject getJSonObjectFromRequest(HttpServletRequest request) throws IOException {
		JsonObject jsonObject = null;

		// get body from request
		String body = getBody(request);

		Gson gson = new Gson();
		if (body.startsWith("[")) {

			JsonArray entries = (JsonArray) new JsonParser().parse(body);
			jsonObject = ((JsonObject)entries.get(0));

		} else {
			jsonObject = gson.fromJson(body, JsonElement.class).getAsJsonObject();
		}

		return jsonObject;
	}

	/**
	 * Read body from request
	 *
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private static String getBody(HttpServletRequest request) throws IOException {
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader = request.getReader();

		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}

		return buffer.toString();
	}

}