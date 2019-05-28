package eu.stamp.botsing.controller.worker.queues;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.utils.JsonMethods;
import eu.stamp.botsing.controller.worker.GitHubAppWorker;
import eu.stamp.botsing.controller.worker.GitHubAppWorkerFactory;

@Component
public class GitHubAppWorkerSubscriber implements Runnable, ExceptionListener{

	private MessageConsumer messageConsumer;
	Logger log = LoggerFactory.getLogger(GitHubAppWorkerSubscriber.class);
	private boolean started;
	
	@Autowired
	@Qualifier("basicFactory")
	private GitHubAppWorkerFactory basicFactory;

	public GitHubAppWorkerSubscriber() {
		this.started = false;
	}


	@Override
	public void onException(JMSException exception) {
		this.log.error("Fatal exception: "+exception.getMessage(),exception);
		
	}

	void setMessageConsumer (MessageConsumer messageConsumer)
	{
		this.messageConsumer = messageConsumer;
	}
	
	void stopProcessor () throws Exception
	{
		this.started = false;
		this.messageConsumer.close();
	}
	
	@Override
	public void run() {
		
		this.started = true;

		try
		{

			while (this.started)
			{
				Message message = this.messageConsumer.receive();

				if (message   instanceof TextMessage) {
					
					processMessage(((TextMessage) message).getText());
					
				}
				else
				{
					this.log.warn("Received unexpected message type "+message.getClass());
				}
				
			}


			
			
		} catch (Exception e)
		{
			this.log.error("Fatal error inside the cycle",e);
			this.started = false;
		}

	}
	
	private void processMessage (String message)
	{
		try
		{
			JsonObject jsonObject = JsonMethods.getJSonObjectFromBodyString(message);
			GitHubAppWorker worker =  this.basicFactory.getWorker(jsonObject);
			worker.getPullRequest(jsonObject,message);
			
		} catch (Exception e)
		{
			this.log.error("Unable to process the message",e);
		}
		
		
	}
	
}
