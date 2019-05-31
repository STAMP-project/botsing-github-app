package eu.stamp.botsing.controller.event;

public interface GitHubEventFactory {

	public static final String 	ACTION = "action",
								EVENT = "event";
	
	public GitHubActionFactory getActionFactory (String eventName) throws InvalidEventException;
}
