package eu.stamp.botsing.service;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eu.stamp.botsing.controller.event.jira.JiraServiceClient;
import eu.stamp.botsing.utility.ConfigurationBean;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { JiraServiceClient.class,  ConfigurationBean.class })
public class JiraServiceTestIT {

	Logger log = LoggerFactory.getLogger(JiraServiceTestIT.class);

	private final String ADDRESS = "http://localhost:8080/jira/rest/botsing-config/1.0/reproduction/PL-23/add";
	private final String TEST_ID = "testID",
			TEST_FILE = "this is a test file",
			TEST_SCAFFOLDING_FILE = "this is a scaffolding file";

	@Autowired
	private JiraServiceClient jiraServiceClient;

	@Test
	public void sendDataTest() throws IOException {
		boolean response = this.jiraServiceClient.sendData(ADDRESS, TEST_ID, TEST_FILE.getBytes(),
				TEST_SCAFFOLDING_FILE.getBytes());

		assertTrue(response);
	}

}