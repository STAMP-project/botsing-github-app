package eu.stamp.botsing.controller.queues;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;


public class QueueManager 
{
	private Connection 	txConnection,
						rxConnection;
	private Session txSession,
					rxSession;
	private MessageProducer producer;
	
	private final String 	CONNECTION_NAME = "vm://localhost",
							QUEUE_NAME = "botsing";
	

	
	private QueueSubscriber subscriber;
	
	public QueueManager (QueueSubscriber subscriber) throws Exception
	{
		this.subscriber = subscriber;
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(CONNECTION_NAME);
		this.txConnection = connectionFactory.createConnection();
		this.txConnection.start();
		this.rxConnection = connectionFactory.createConnection();
		this.rxConnection.start();
		this.txSession = this.txConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.rxSession = this.rxConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
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

	public QueuePublisher createPublisher (String toolName,String event, String action)
	{
		return new QueuePublisher(this.producer, this.txSession, toolName,event,action);
	}
	
}
