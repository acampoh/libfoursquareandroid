package es.viridian.beersandroid.foursquare.exceptions;

public class FSDataException extends FSException {
	public FSDataException(String message) {
		super(message);
	}
	
	public FSDataException(String message, Throwable exception) {
		super(message, exception);
	}
}
