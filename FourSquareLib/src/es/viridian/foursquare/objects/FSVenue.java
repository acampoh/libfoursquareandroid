package es.viridian.foursquare.objects;


import org.json.JSONException;
import org.json.JSONObject;

import es.viridian.foursquare.exceptions.FSDataException;

import android.location.Location;

public class FSVenue extends FSAbstractClass {
	private JSONObject mJsonData;
	
	private String mVenueId;
	private String mName;
	private double mLatitude;
	private double mlongitude;
	private FSVenueCategory mCategory;
	
	@Override
	public void fillFromJson (JSONObject obj) throws FSDataException {
		this.mJsonData = obj;
		
		try {
			mVenueId = this.mJsonData.getString("id");
		} catch (JSONException ex) {
			throw new FSDataException("There was a problem reading Venue Data", ex);
		}
		
		this.mName = this.mJsonData.optString("name");
		
		
		JSONObject location = this.mJsonData.optJSONObject("location");
		
		if (location != null)
		{
			mLatitude = location.optDouble("lat");
			mlongitude = location.optDouble("lng");
		}
		
//		JSONArray categories = mJsonData.optJSONArray("categories");
//		if (categories != null && categories.length() > 0 )
//		{
//			JSONObject category;
//			try {
//				category = (JSONObject) categories.get(0);
//			} catch (JSONException ex) {
//				throw new FSDataException("There was a problem reading Venue Data", ex);
//			}
//			mCategory.fillFromJson(category);
//		}
	}

	public JSONObject getJsonData() {
		return mJsonData;
	}

	public void setJsonData(JSONObject mJsonData) {
		this.mJsonData = mJsonData;
	}

	public String getVenueId() {
		return mVenueId;
	}

	public void setVenueId(String mVenueId) {
		this.mVenueId = mVenueId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}
	
	public double getLatitude() {
		return mLatitude;
	}

	public double getlongitude() {
		return mlongitude;
	}
}
