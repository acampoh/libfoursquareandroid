package es.viridian.beersandroid.foursquare;

import es.viridian.beersandroid.foursquare.enums.FourSquareError;

public interface FourSquareLoginListener {
	public void onSuccess(final String authToken);
	public void onError(FourSquareError error);
	public void onCancel();
}
