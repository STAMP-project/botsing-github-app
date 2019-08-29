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
import eu.stamp.botsing.controller.event.jira.JiraSuccessResultManager;
import eu.stamp.botsing.service.bean.TestJiraDataBean;
import eu.stamp.botsing.utility.ConfigurationBean;
import eu.stamp.botsing.utility.ConfigurationBeanForIntegrationTests;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {  ConfigurationBean.class,ConfigurationBeanForIntegrationTests.class })
public class JiraServiceTestIT {

	Logger log = LoggerFactory.getLogger(JiraServiceTestIT.class);

     private final String TEST_FILE = "this is a test file",
			TEST_SCAFFOLDING_FILE = "this is a scaffolding file";



	@Autowired
	ConfigurationBean configuration;
	
	@Test
	public void sendDataTest() throws IOException, URISyntaxException {
		
		JiraSuccessResultManager jiraResultManager = new JiraSuccessResultManager(configuration, TEST_FILE.getBytes(),TEST_SCAFFOLDING_FILE.getBytes());
		ResponseBean response = jiraResultManager.notifyToServer(new TestJiraDataBean());
		assertTrue(response.getStatus() == 200);
	}

}