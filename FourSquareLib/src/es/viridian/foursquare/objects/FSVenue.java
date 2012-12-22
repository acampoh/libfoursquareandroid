package es.viridian.foursquare.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.viridian.foursquare.exceptions.FSDataException;

import android.location.Location;

public class FSVenue extends FSAbstractClass {
	private JSONObject mJsonData;
	
	private String mVenueId;
	private String mName;
	private Location mLocation;
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
			Location l = new Location("FourSquare");
			l.setLatitude(location.optDouble("lat"));
			l.setLongitude(location.optDouble("lng"));
			
			mLocation = l;
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

	public JSONObject getmJsonData() {
		return mJsonData;
	}

	public void setmJsonData(JSONObject mJsonData) {
		this.mJsonData = mJsonData;
	}

	public String getmVenueId() {
		return mVenueId;
	}

	public void setmVenueId(String mVenueId) {
		this.mVenueId = mVenueId;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public Location getmLocation() {
		return mLocation;
	}

	public void setmLocation(Location mLocation) {
		this.mLocation = mLocation;
	}
	
	
	
}
