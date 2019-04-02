package eu.stamp.botsing.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.stamp.botsing.runner.MavenRunner;
import eu.stamp.botsing.service.GitHubService;
import eu.stamp.botsing.service.JGitService;
import eu.stamp.botsing.utility.FileUtility;

@RestController
public class GitHubAppController {

	Logger log = LoggerFactory.getLogger(GitHubAppController.class);

	@Autowired
	private JGitService gitService;

	@Autowired
	private GitHubService githubService;

	@RequestMapping("/test")
	public String greeting() {
		return "This is the Botsing GitHub App Test Service. More informations can be found here: https://github.com/STAMP-project/botsing-github-app";
	}

	@PostMapping(value = "/botsing-github-app")
	public Map<String, String> getPullRequestFullBody(HttpServletRequest request,
			@RequestHeader(value = "X-GitHub-Event", defaultValue = "") String eventType) {
		HashMap<String, String> response = new HashMap<>();

		try {
			log.debug("'" + eventType + "' Event received");

			// get JSON Object from request
			JsonObject jsonObject = getJSonObjectFromRequest(request);

			// issues Event
			if (eventType.equals("issues")) {
				String action = jsonObject.get("action").getAsString();

				if (action.equals("opened")) {
					// get issue information
					String issueNumber = jsonObject.get("issue").getAsJsonObject().get("number").getAsString();
					String issueBody = jsonObject.get("issue").getAsJsonObject().get("body").getAsString();

					log.info("Received new issue event " + issueNumber);

					// get bug information from issue body
					String bodyBuildType = getParamFromBody("buildType", issueBody);
					String bodyProjectPath = getParamFromBody("projectPath", issueBody);
					String bodyVersionReference = getParamFromBody("versionReference", issueBody);

					String bodyTargetFrame = getParamFromBody("targetFrame", issueBody);
					String bodyPopulation = getParamFromBody("population", issueBody);
					String bodySearchBudget = getParamFromBody("search_budget", issueBody);
					String bodyGlobalTimeout = getParamFromBody("global_timeout", issueBody);

					String bodyCrashLog = getSectionFromBody("crashLog", issueBody);

					// get repository information
					String repositoryName = jsonObject.get("repository").getAsJsonObject().get("name").getAsString();
					String repositoryURL  = jsonObject.get("repository").getAsJsonObject().get("html_url").getAsString();
					String repositoryOwner = jsonObject.get("repository").getAsJsonObject().get("owner").getAsJsonObject().get("login").getAsString();

					if (bodyCrashLog.length()>0) {
						log.info("Received issue " + issueNumber + " with a stacktrace");

						String message = handlePipeline(bodyVersionReference, repositoryName, repositoryURL, repositoryOwner, issueNumber,
								bodyBuildType, bodyCrashLog, bodyProjectPath, bodyTargetFrame, bodyPopulation,
								bodySearchBudget, bodyGlobalTimeout);

						response.put("message", message);

					} else {
						response.put("message", "Received issue " + issueNumber + " without stacktrace");
					}

				} else if (action.equals("edited")) {

					JsonObject changes = jsonObject.get("changes").getAsJsonObject();

					// TODO: I could check if there is some change in the parameters that are used to run the reproduction

					// TODO: add comment in the issue
					response.put("message", "Issue action '"+action+"' is not a target.");

				} else {
					response.put("message", "Issue action '"+action+"' is not a target.");
				}
			}

		} catch (Exception e) {
			log.error("Error parsing event", e);
		}

		return response;
	}

	public String handlePipeline(String branch, String repositoryName, String repositoryURL, String repositoryOwner,
			String issueNumber, String buildType, String crashLog, String projectPath, String targetFrame,
			String population, String searchBudget, String globalTimeout) throws Exception {

		log.info("Start pipeline for '" + repositoryName + "' due to issue " + issueNumber);

		// clone project from GitHub
		log.info("Cloning repository '" + repositoryName + "'");
		File repoFolder = gitService.cloneRepository(repositoryURL);

		// checkout branch
		gitService.checkoutBranch(repoFolder, branch);

		// move to the module to build
		if (projectPath.endsWith("pom.xml")) {
			projectPath = projectPath.substring(0, projectPath.length()-7);
		}
		File projectFolder = new File(repoFolder.getAbsolutePath() + File.separator + projectPath);

		// create crashLog file
		File crashLogFile = new File(projectFolder + "/crash.log");
		FileUtils.writeStringToFile(crashLogFile, crashLog, Charset.defaultCharset());

		// compile project
		if (buildType.equalsIgnoreCase("maven")) {
			log.info("Compiling repository '" + repositoryName + "' with maven");
			MavenRunner.compileWithoutTests(projectFolder);

			// execute Botsing to reproduce stacktrace
			log.info("Running Botsing");
			boolean noErrors = MavenRunner.runBotsingReproduction(projectFolder, crashLogFile.getAbsolutePath(), targetFrame, population,
					searchBudget, globalTimeout);

			if (noErrors == false) {
				log.error("Error running Botsing");
				return "Error running Botsing";
			}

		} else if (buildType.equalsIgnoreCase("gradle")) {
			return "Build type '"+buildType+"' under development.";

		} else {
			return "Build type '"+buildType+"' not supported.";
		}

		// copy new test to src folder
		File source = new File(projectFolder.getAbsolutePath() + File.separator + "crash-reproduction-tests");
		File dest = new File(projectFolder.getAbsolutePath() + "/src/test/java/");
		FileUtility.copyJavaFile(source, dest);
		log.debug("New Tests added to source folder");

		// create new branch
		String newBranch = "bug-reproduction-" + issueNumber + "-" + System.currentTimeMillis();
		log.info("Creating branch '"+newBranch+"'");
		gitService.createNewBranch(repoFolder, newBranch, true);

		// commit new test
		gitService.addFolder(repoFolder, projectPath.length() > 0 ? projectPath + File.separator + "src" : "src");
		gitService.commitAll(repoFolder, "Add reproduction test from issue " + issueNumber);

		// push
		log.info("Pushing to '"+repositoryURL+"'");
		gitService.push(repoFolder, newBranch);

		// pull request from branch to master
		log.info("Creating Pull Request on '"+repositoryName+"'");
		githubService.createPullRequest(repositoryName, repositoryOwner, "Botsing reproduction from issue " + issueNumber,
				"Botsing reproduction pull request from issue " + issueNumber + " on branch " + newBranch, newBranch,
				"master");

		return "Pull request created with reproduction test!";
	}

	private String getSectionFromBody(String sectionName, String body) {
		String result = null;

		String start = "#### " + sectionName;
		String end = "####";

		if (body.indexOf(start) > 0) {
			result = body.substring(body.indexOf(start) + start.length());
			if (result.indexOf(end) > 0) {
				result = result.substring(0, result.indexOf(end));
			}
			result = result.trim();
		}

		return result;
	}

	private String getParamFromBody(String paramName, String body) {
		String result = null;

		String start = "- **" + paramName + "**:";
		String end = "\n";

		if (body.indexOf(start) > 0) {
			result = body.substring(body.indexOf(start) + start.length());
			result = result.substring(0, result.indexOf(end));
			result = result.trim();
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