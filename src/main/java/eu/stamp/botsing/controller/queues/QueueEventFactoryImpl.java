package eu.stamp.botsing.controller.queues;

import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

import eu.stamp.botsing.controller.event.ActionFactory;
import eu.stamp.botsing.controller.event.EventFactoryImpl;

@Component 
public class QueueEventFactoryImpl extends EventFactoryImpl implements QueueEventFactory {

	private QueueSubscriber subscriber;
	private Connection 	rxConnection;
	private Session rxSession;
	private boolean isStarted;
	
	public QueueEventFactoryImpl(List<ActionFactory> actionFactories) throws Exception
	{
		super (actionFactories);
		this.subscriber = new QueueSubscriber(this);
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(QueueManagerConstants.CONNECTION_NAME);
		this.rxConnection = connectionFactory.createConnection();
		this.rxConnection.start();
		this.rxSession = this.rxConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.isStarted = false;
		
	}
	
	


	@Override
	public synchronized void startSubscriber() throws Exception 
	{
		if (!isStarted)
		{
			Destination rxDestination = this.rxSession.createQueue(QueueManagerConstants.QUEUE_NAME);
			this.rxConnection.setExceptionListener(this.subscriber);
			this.subscriber.setMessageConsumer(this.rxSession.createConsumer(rxDestination));
			new Thread(this.subscriber).start();
			this.isStarted = true;
		}

		
	}


}
