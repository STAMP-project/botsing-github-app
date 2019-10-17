package eu.stamp.botsing.controller.queues;

import eu.stamp.botsing.controller.event.EventFactory;

public interface QueueEventFactory extends EventFactory {

	public void startSubscriber () throws Exception;
	
}
