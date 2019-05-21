package eu.stamp.botsing.controller.worker.queues;

import java.util.HashMap;
import java.util.Map;

import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.worker.GitHubAppWorker;

public class GitHubAppPublisherWorker implements GitHubAppWorker {

	private static final Logger log = LoggerFactory.getLogger(GitHubAppPublisherWorker.class);;
	private String bodyString;
	private MessageProducer messageProducer;
	private Session session;
	
	GitHubAppPublisherWorker(MessageProducer messageProducer, Session session) 
	{
		this.messageProducer = messageProducer;
		this.session = session;
	}
	
	@Override
	public void setParameters(JsonObject jsonObject, String bodyString) {
		this.bodyString = bodyString;
	}

	@Override
	public Map<String, String> getPullRequest() throws Exception {
		log.debug("Adding the request in queue");
		TextMessage messageObject = this.session.createTextMessage(this.bodyString);
		this.messageProducer.send(messageObject);
		HashMap<String, String> response = new HashMap<String, String>();
		response.put("message", "Request forwarded to botsing");
		return response;
	}

}
