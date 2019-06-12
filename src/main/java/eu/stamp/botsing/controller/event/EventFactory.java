package eu.stamp.botsing.controller.event;

public interface EventFactory {

	public static final String 	ACTION = "action",
								EVENT = "event";
	
	public ActionFactory getActionFactory (String eventName) throws InvalidEventException;
}
