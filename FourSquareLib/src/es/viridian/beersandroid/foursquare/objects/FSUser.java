package es.viridian.beersandroid.foursquare.objects;

import org.json.JSONException;
import org.json.JSONObject;

import es.viridian.beersandroid.foursquare.exceptions.FSDataException;

import android.util.Log;

public class FSUser {
	private String id;
	private String firstName;
	private String lastName;
	private String photoUrl;

	private JSONObject jsonData;

	public void fillUser(final JSONObject response) throws FSDataException {
		if (response != null) {
			try {
				jsonData = response.getJSONObject("user");

				id = jsonData.getString("id");
				firstName = jsonData.getString("firstName");
				lastName = jsonData.getString("lastName");
				JSONObject photoInfo = jsonData.getJSONObject("photo");
				photoUrl = photoInfo.getString("prefix")
						+ photoInfo.getString("suffix").substring(1);
			} catch (JSONException ex) {
				Log.e("FSUSER",
						"There was an error parsing FourSquareUser data");

				throw new FSDataException("There was an error", ex);
			}
		}
	}
	
	public final String getUserId() {
		return id;
	}

	public final String getFirstName() {
		return firstName;
	}

	public final String getLastName() {
		return lastName;
	}
	
	public final String getFullName() {
		return firstName + " " + lastName;
	}

	public final String getPhotoUrl() {
		return photoUrl;
	}
}
