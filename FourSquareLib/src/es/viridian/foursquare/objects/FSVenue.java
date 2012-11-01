package es.viridian.foursquare.objects;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Point;
import android.location.Location;

public class FSVenue {
	private JSONObject mJsonData;
	
	private String mVenueId;
	private String mName;
	private Location mLocation;
	
	public void fillVenue(JSONObject obj) {
		this.mJsonData = obj;
		
		try {
			mVenueId = this.mJsonData.getString("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
