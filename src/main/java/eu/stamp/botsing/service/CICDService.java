package eu.stamp.botsing.service;

import java.io.File;
import java.io.IOException;

public interface CICDService 
{

	String getRawFile(String repositoryName, String repositoryOwner, String filePath) throws IOException;

	String getIssueBody(String repositoryName, String repositoryOwner, String issueNumber) throws IOException;

	String createIssueComment(String repositoryName, String repositoryOwner, String issueNumber, String comment)
			throws IOException;

	String sendFile(String repositoryName, String repositoryOwner, String issueNumber, File file)
			throws IOException;

}