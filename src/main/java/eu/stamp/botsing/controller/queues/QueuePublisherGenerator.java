package eu.stamp.botsing.controller.queues;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;


public class QueuePublisherGenerator 
{
	private Connection 	txConnection;
	private Session txSession;
	private MessageProducer producer;
	

	

	
	public QueuePublisherGenerator () throws Exception
	{

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(QueueManagerConstants.CONNECTION_NAME);
		this.txConnection = connectionFactory.createConnection();
		this.txConnection.start();
		this.txSession = this.txConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination txDestination = this.txSession.createQueue(QueueManagerConstants.QUEUE_NAME);
		this.producer = this.txSession.createProducer(txDestination);
		this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	}
	
	

	public QueuePublisher createPublisher (String toolName,String event, String action)
	{
		return new QueuePublisher(this.producer, this.txSession, toolName,event,action);
	}
	
}
