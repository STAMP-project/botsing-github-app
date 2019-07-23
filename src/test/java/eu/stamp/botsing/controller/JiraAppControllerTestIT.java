package eu.stamp.botsing.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import eu.stamp.botsing.controller.event.EventFactoryImpl;
import eu.stamp.botsing.controller.event.jira.issues.JiraIssuesActionFactoryImpl;
import eu.stamp.botsing.controller.event.jira.issues.JiraIssuesDefaultAction;
import eu.stamp.botsing.controller.jira.JiraAppController;
import eu.stamp.botsing.utility.ConfigurationBean;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { JiraAppController.class,EventFactoryImpl.class, JiraIssuesActionFactoryImpl.class, 
		JiraIssuesDefaultAction.class,  EventFactoryImpl.class,
		ConfigurationBean.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@AutoConfigureMockMvc
public class JiraAppControllerTestIT {

	Logger log = LoggerFactory.getLogger(JiraAppControllerTestIT.class);

	@Autowired
	private MockMvc mockMvc;
	String defaultJson = "{\"botsingProjectConfig\":{\"projectKey\":\"PL\",\"groupId\":\"org.ow2.authzforce\",\"artifactId\":\"authzforce-ce-core-pdp-testutils\",\"version\":\"13.3.1\",\"searchBudget\":60,\"globalTimeout\":90,\"population\":100,\"packageFilter\":\"org.ow2.authzforce\",\"enabled\":true},\"botsingIssueConfig\":{\"issueKey\":\"PL-23\",\"testFileBody\":\"java.lang.RuntimeException: Failed to load XML schemas: [classpath:pdp.xsd]\nat org.ow2.authzforce.core.pdp.impl.SchemaHandler.createSchema(SchemaHandler.java:541)\nat org.ow2.authzforce.core.pdp.impl.PdpModelHandler.(PdpModelHandler.java:159)\nat org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration.getInstance(PdpEngineConfiguration.java:682)\nat org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration.getInstance(PdpEngineConfiguration.java:699)\nat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\nat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\nat java.lang.reflect.Method.invoke(Method.java:498)\nat org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:162)\"},\"jiraCallbackUrl\":\"http://localhost:8080/jira/rest/botsing-config/1.0/reproduction/PL-23/add\"}";
	String wringdefaultJson = "{\"botsingProjectConfig\":{\"projectKey\":\"PL\",\"groupId\":\"org.ow2.authzforce\",\"artifactId\":\"authzforce-ce-core-pdp-testutils\",\"version\":\"13.3.1\",\"searchBudget\":60,\"globalTimeout\":90,\"population\":100,\"packageFilter\":\"org.ow2.authzforce\",\"enabled\":true},\"botsingIssueConfig\":{\"testFileBody\":\"java.lang.RuntimeException: Failed to load XML schemas: [classpath:pdp.xsd]\nat org.ow2.authzforce.core.pdp.impl.SchemaHandler.createSchema(SchemaHandler.java:541)\nat org.ow2.authzforce.core.pdp.impl.PdpModelHandler.(PdpModelHandler.java:159)\nat org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration.getInstance(PdpEngineConfiguration.java:682)\nat org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration.getInstance(PdpEngineConfiguration.java:699)\nat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\nat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\nat java.lang.reflect.Method.invoke(Method.java:498)\nat org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:162)\"},\"jiraCallbackUrl\":\"http://localhost:8080/jira/rest/botsing-config/1.0/reproduction/PL-23/add\"}";

	@Test
	public void mockIssueOpened() throws Exception {


		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/jira/botsing-jira-app")
				.header("X-Jira-Event", "issues")
				.accept(MediaType.APPLICATION_JSON)
				.content(defaultJson)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();
		log.info(response.getContentAsString());
	}

	@Test
	public void mockIssueEdited() throws Exception {

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/jira/botsing-jira-app")
				.header("X-Jira-Event", "push")
				.accept(MediaType.APPLICATION_JSON)
				.content(wringdefaultJson)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();
		log.info(response.getContentAsString());
	}
}
