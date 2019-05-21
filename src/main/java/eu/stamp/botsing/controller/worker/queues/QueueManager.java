package eu.stamp.botsing.controller.worker.queues;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueManager 
{
	private Connection 	txConnection,
						rxConnection;
	private Session txSession,
					rxSession;
	private MessageProducer producer;
	
	private final String 	CONNECTION_NAME = "vm://localhost",
							QUEUE_NAME = "botsing";
	
	@Autowired
	private GitHubAppWorkerSubscriber subscriber;
	
	public QueueManager () throws Exception
	{
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(CONNECTION_NAME);
		this.txConnection = connectionFactory.createConnection();
		this.txConnection.start();
		this.rxConnection = connectionFactory.createConnection();
		this.rxConnection.start();
		this.txSession = this.txConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination txDestination = this.txSession.createQueue(QUEUE_NAME);
		this.producer = this.txSession.createProducer(txDestination);
		this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	}
	
	
	public void startSubscriber () throws Exception
	{
		Destination rxDestination = this.rxSession.createQueue(QUEUE_NAME);
		this.rxConnection.setExceptionListener(this.subscriber);
		this.subscriber.setMessageConsumer(this.rxSession.createConsumer(rxDestination));
		new Thread(this.subscriber).start();

	}

	public GitHubAppPublisherWorker getWorker ()
	{
		return new GitHubAppPublisherWorker(this.producer, this.txSession);
	}
	
}
