package es.viridian.foursquare;

import es.viridian.foursquare.enums.FourSquareError;

public interface FourSquareLoginListener {
	public void onSuccess(final String authToken);
	public void onError(FourSquareError error);
	public void onCancel();
}
