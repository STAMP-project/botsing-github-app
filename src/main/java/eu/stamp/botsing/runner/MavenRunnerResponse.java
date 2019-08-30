package eu.stamp.botsing.runner;

public class MavenRunnerResponse {

	private int internalStatus;
	private String log;
	
	MavenRunnerResponse(int internalStatus, String log ) 
	{
		this.internalStatus = internalStatus;
		this.log = log;
	}

	public boolean isSuccess ()
	{
		return this.internalStatus == 0;
	}

	public String getLog() {
		return log;
	}
	
	
	@Override
	public String toString() {

		return String.format("result %d, output %s", this.internalStatus, this.log);
		
	}
	
}
