package es.viridian.beersandroid.foursquare.exceptions;

public class FSRuntimeException extends RuntimeException {
	public FSRuntimeException(String message)	{
		super(message);
	}
	
	public FSRuntimeException(String message, Throwable exception)	{
		super(message, exception);
	}
}
