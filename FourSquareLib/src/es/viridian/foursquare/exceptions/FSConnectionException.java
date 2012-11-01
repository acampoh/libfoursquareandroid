package es.viridian.foursquare.exceptions;

public class FSConnectionException extends FSRuntimeException {

	public FSConnectionException(String message) {
		super(message);
	}
	
	public FSConnectionException(String message, Throwable exception) {
		super(message, exception);
	}

}
