package es.viridian.foursquare;

import es.viridian.foursquare.exceptions.FSException;

public interface FSResponseCallback <T> {
	public void onSuccess(T clazz);
	public void onError(FSException ex);
}
