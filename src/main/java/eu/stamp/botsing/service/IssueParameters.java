package eu.stamp.botsing.service;

public class IssueParameters 
{
	private  String issueNumber, 
					repositoryName,
					repositoryURL,
					repositoryOwner,
					issueBody;

	public IssueParameters(String issueNumber, String repositoryName,
			String repositoryURL,String repositoryOwner,String issueBody) {

		this.issueNumber  = issueNumber;
		this.repositoryName = repositoryName;
		this.repositoryURL = repositoryURL;
		this.repositoryOwner = repositoryOwner;
		this.issueBody = issueBody;
		
	}

	public String getIssueNumber() {
		return issueNumber;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public String getRepositoryURL() {
		return repositoryURL;
	}

	public String getRepositoryOwner() {
		return repositoryOwner;
	}

	public String getIssueBody() {
		return issueBody;
	}

}
