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
		return "Test Rest Service";
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
					String issueStatus = jsonObject.get("issue").getAsJsonObject().get("state").getAsString();
					String issueNumber = jsonObject.get("issue").getAsJsonObject().get("number").getAsString();
					String issueTitle = jsonObject.get("issue").getAsJsonObject().get("title").getAsString();
					String issueBody = jsonObject.get("issue").getAsJsonObject().get("body").getAsString();

					// get bug information from issue body
					String bodyRepositoryURL = getParamFromBody("repositoryURL", issueBody);
					String bodyPomPath = getParamFromBody("pomPath", issueBody);
					String bodyBranch = getParamFromBody("branch", issueBody);

					String bodyTargetFrame = getParamFromBody("targetFrame", issueBody);
					String bodyPopulation = getParamFromBody("population", issueBody);
					String bodySearchBudget = getParamFromBody("search_budget", issueBody);
					String bodyGlobalTimeout = getParamFromBody("global_timeout", issueBody);
					//String bodyTestDir = getParamFromBody("test_dir", issueBody);

					String bodyCrashLog = getSectionFromBody("crashLog", issueBody);

					// get repository information
					String repositoryName = jsonObject.get("repository").getAsJsonObject().get("name").getAsString();
					String repositoryURL  = jsonObject.get("repository").getAsJsonObject().get("html_url").getAsString();
					String repositoryOwner = jsonObject.get("repository").getAsJsonObject().get("owner").getAsJsonObject().get("login").getAsString();

					handlePipeline(bodyBranch, repositoryName, repositoryURL, repositoryOwner, issueNumber,
							bodyCrashLog, bodyPomPath, bodyTargetFrame, bodyPopulation, bodySearchBudget,
							bodyGlobalTimeout);

					response.put("message", "Pull request created with reproduction tests!");

				} else if (action.equals("edited")) {

					JsonObject changes = jsonObject.get("changes").getAsJsonObject();

					// TODO: I could check if there is some change in the parameters that are used to run the reproduction

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

	public void handlePipeline(String branch, String repositoryName, String repositoryURL, String repositoryOwner,
			String issueNumber, String crashLog, String pomPath, String targetFrame, String population, String searchBudget,
			String globalTimeout) throws Exception {

		// checkout project from GitHub - src/main/java/eu/stamp/botsing/controller/GitHubAppController.java
		File repoFolder = gitService.cloneRepository(repositoryURL);

		// checkout branch of the push
		gitService.checkoutBranch(repoFolder, branch);

		// move to the module to build
		if (pomPath.endsWith("pom.xml")) {
			pomPath = pomPath.substring(0, pomPath.length()-7);
		}
		File projectFolder = new File(repoFolder.getAbsolutePath() + "/"+ pomPath);

		// compile project
		MavenRunner.compileWithoutTests(projectFolder);

		// create crashLog file
		File crashLogFile = new File(projectFolder + "/crash.log");
		FileUtils.writeStringToFile(crashLogFile, crashLog, Charset.defaultCharset());

		// execute Botsing to reproduce stacktrace
		MavenRunner.runBotsingReproduction(projectFolder, crashLogFile.getAbsolutePath(), targetFrame, population,
				searchBudget, globalTimeout);

		// copy new test to src folder
		File source = new File(projectFolder.getAbsolutePath() + "/crash-reproduction-tests");
		File dest = new File(projectFolder.getAbsolutePath() + "/src/test/java/");
		FileUtility.copyDirectory(source, dest);
		log.debug("New Tests added to source folder");

		// create new branch
		String newBranch = "botsing-reproduction-branch-" + issueNumber + "-" + System.currentTimeMillis();
		gitService.createNewBranch(repoFolder, newBranch, true);

		// commit new test
		gitService.addFolder(repoFolder, "src");
		gitService.commitAll(repoFolder, "Add reproduction test from issue "+issueNumber);

		// push
		gitService.push(repoFolder, newBranch);

		// pull request from branch to master
		githubService.createPullRequest(repositoryName, repositoryOwner, "Botsing reproduction",
				"Botsing reproduction pull request from issue " + issueNumber + " on " + newBranch, newBranch,
				"master");
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