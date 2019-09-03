package eu.stamp.botsing.controller.event.actions;

public enum BotsingResult 
{
	OK ("OK",200), 
	NO_FILES ("Botsing did not generate any reproduction test",304),
	FAIL ("Error executing Botsing",500);
	
	private String message;
	private int status;
	
	BotsingResult (String message, int status)
	{
		this.message = message;
		this.status = status;
	}
	
	public String getMessage ()
	{
		return this.message;
		
	}
	
	public int getStatus ()
	{
		return this.status;
	}
}
