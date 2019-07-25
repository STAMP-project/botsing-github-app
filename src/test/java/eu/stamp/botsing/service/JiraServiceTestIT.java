package eu.stamp.botsing.service;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

import eu.stamp.botsing.controller.event.jira.JiraServiceClient;

@RunWith(SpringRunner.class)

public class JiraServiceTestIT {

	Logger log = LoggerFactory.getLogger(JiraServiceTestIT.class);

	private JiraServiceClient jiraServiceClient;
	private final String ADDRESS = "http://localhost:8080/jira/rest/botsing-config/1.0/reproduction/PL-23/add";
	private final String 	TEST_ID ="testID",
							TEST_FILE = "this is a test file",
							TEST_SCAFFOLDING_FILE = "this is a scaffolding file";
	


	@Before
	public void before() throws URISyntaxException {
		this.jiraServiceClient = new JiraServiceClient(ADDRESS);

	}
	
	@Test
	public void sendDataTest () throws IOException
	{
		boolean response= 	this.jiraServiceClient.sendData(TEST_ID, TEST_FILE.getBytes(), TEST_SCAFFOLDING_FILE.getBytes());

		assertTrue(response);
	}



}