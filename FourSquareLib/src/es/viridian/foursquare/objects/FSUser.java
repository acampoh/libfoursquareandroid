package es.viridian.foursquare.objects;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import es.viridian.foursquare.exceptions.FSDataException;

public class FSUser extends FSAbstractClass {
	private String mId;
	private String mFirstName;
	private String mLastName;
	private String mPhotoUrl;

	private JSONObject mJsonData;

	@Override
	public void fillFromJson(final JSONObject response) throws FSDataException {
		if (response != null) {
			try {
				mJsonData = response.getJSONObject("user");

				mId = mJsonData.getString("id");
				mFirstName = mJsonData.getString("firstName");
				mLastName = mJsonData.getString("lastName");
				JSONObject photoInfo = mJsonData.getJSONObject("photo");
				mPhotoUrl = photoInfo.getString("prefix")
						+ photoInfo.getString("suffix").substring(1);
			} catch (JSONException ex) {
				Log.e("FSUSER",
						"There was an error parsing FourSquareUser data");

				throw new FSDataException("There was an error", ex);
			}
		}
	}
	
	public final String getUserId() {
		return mId;
	}

	public final String getFirstName() {
		return mFirstName;
	}

	public final String getLastName() {
		return mLastName;
	}
	
	public final String getFullName() {
		return mFirstName + " " + mLastName;
	}

	public final String getPhotoUrl() {
		return mPhotoUrl;
	}
}
