package eu.stamp.botsing.controller.event.github.issues;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import eu.stamp.botsing.controller.event.filter.ActionFilter;
import eu.stamp.botsing.controller.event.github.GitHubClientManager;


@Component
public class GitHubIssuesActionLabeled extends GitHubIssuesActionBotsing implements GitHubIssuesAction{


	
	@Autowired
	public GitHubIssuesActionLabeled(GitHubClientManager gitHubClientManager) {
		super (gitHubClientManager,"labeled");

	}
	

	
	@Autowired
	@Qualifier ("github.configuration.change.label")
	@Override
	public void setActionFilter(ActionFilter actionFilter) {
		super.setActionFilter(actionFilter);
	}

	
}
