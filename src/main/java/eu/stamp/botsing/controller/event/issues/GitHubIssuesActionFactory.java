package eu.stamp.botsing.controller.event.issues;

import eu.stamp.botsing.controller.event.GitHubActionFactory;

public interface GitHubIssuesActionFactory extends GitHubActionFactory{

	final String EVENT_NAME ="issues";
}
