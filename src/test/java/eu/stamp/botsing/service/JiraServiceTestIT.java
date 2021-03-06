package eu.stamp.botsing.service;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.actions.BotsingResult;
import eu.stamp.botsing.controller.event.jira.JiraErrorResultManager;
import eu.stamp.botsing.controller.event.jira.JiraSuccessResultManager;
import eu.stamp.botsing.service.bean.TestJiraDataBean;
import eu.stamp.botsing.utility.ConfigurationBean;
import eu.stamp.botsing.utility.ConfigurationBeanForIntegrationTests;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {  ConfigurationBean.class,ConfigurationBeanForIntegrationTests.class })
public class JiraServiceTestIT {

	Logger log = LoggerFactory.getLogger(JiraServiceTestIT.class);

     private final String TEST_FILE = "this is a test file",
			TEST_ERROR_FILE ="this is a stack trace";



	@Autowired
	ConfigurationBean configuration;
	
	@Test
	public void sendDataTest() throws IOException, URISyntaxException {
		
		JiraSuccessResultManager jiraResultManager = new JiraSuccessResultManager(configuration, TEST_FILE.getBytes());
		ResponseBean response = jiraResultManager.notifyToServer(new TestJiraDataBean());
		assertTrue(response.getStatus() == 200);
	}
	
	@Test
	public void sendErrorTest() throws IOException, URISyntaxException {
		
		JiraErrorResultManager jiraResultManager = new JiraErrorResultManager(configuration, TEST_ERROR_FILE.getBytes(), BotsingResult.FAIL);
		ResponseBean response = jiraResultManager.notifyToServer(new TestJiraDataBean());
		assertTrue(response.getStatus() == 200);
	}

}