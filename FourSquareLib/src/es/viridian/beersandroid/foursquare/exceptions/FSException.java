package es.viridian.beersandroid.foursquare.exceptions;

public class FSException extends Exception {
	private static final long serialVersionUID = 1L;

	public FSException(String message)	{
		super(message);
	}
	
	public FSException(String message, Throwable exception)	{
		super(message, exception);
	}
}
