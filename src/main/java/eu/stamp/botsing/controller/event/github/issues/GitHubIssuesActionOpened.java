package eu.stamp.botsing.controller.event.github.issues;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import eu.stamp.botsing.controller.event.filter.ActionFilter;
import eu.stamp.botsing.controller.event.github.GitHubClientManager;

//@Configurable
//@Component
public class GitHubIssuesActionOpened extends GitHubIssuesActionBotsing implements GitHubIssuesAction{

	
	@Autowired
	public GitHubIssuesActionOpened(GitHubClientManager gitHubClientManager) 
	{
		super (gitHubClientManager,"opened");
	
	}
	
	
	@Autowired
	@Qualifier ("github.configuration.label")
	@Override
	public void setActionFilter(ActionFilter actionFilter) 
	{
		super.setActionFilter(actionFilter);
	}


	
}
