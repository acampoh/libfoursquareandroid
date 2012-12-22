package es.viridian.foursquare.objects;

import org.json.JSONObject;

import es.viridian.foursquare.exceptions.FSDataException;

public abstract class FSAbstractClass {
	public abstract void fillFromJson(final JSONObject response) throws FSDataException;

}
