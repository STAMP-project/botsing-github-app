package eu.stamp.botsing.controller.worker.queues;

import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.worker.GitHubAppWorker;
import eu.stamp.botsing.controller.worker.ResponseBean;

public class GitHubAppPublisherWorker implements GitHubAppWorker {

	private static final Logger log = LoggerFactory.getLogger(GitHubAppPublisherWorker.class);;
	private MessageProducer messageProducer;
	private Session session;
	
	GitHubAppPublisherWorker(MessageProducer messageProducer, Session session) 
	{
		this.messageProducer = messageProducer;
		this.session = session;
	}
	


	@Override
	public ResponseBean getPullRequest(JsonObject jsonObject, String bodyString) throws Exception {
		log.debug("Adding the request in queue");
		TextMessage messageObject = this.session.createTextMessage(bodyString);
		this.messageProducer.send(messageObject);
		return new ResponseBean(202,"Request forwarded to botsing");
	
	}

}
