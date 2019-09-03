package eu.stamp.botsing.controller.event;

public interface EventFactory {

	public static final String 	ACTION = "action",
								EVENT = "event",
								TOOL = "tool";
	
	public ActionFactory getActionFactory (String toolName,String eventName) throws InvalidEventException;
}
