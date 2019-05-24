package eu.stamp.botsing.controller.worker;

public class ResponseBean {

	private int status;
	private String message;
	
	
	public ResponseBean(int status, String message) {

		this.status = status;
		this.message = message;
	}

	
	public int getStatus() {
		return status;
	}
	public String getMessage() {
		return message;
	}
	
	
}
