package es.viridian.foursquare.request;

import android.util.Log;
import es.viridian.foursquare.enums.FSCategory;
import es.viridian.foursquare.FourSquareMgr;

public class SearchVenuesRequest {
	private static final String URI_PREFIX = "https://api.foursquare.com/v2/venues/search";
	private StringBuilder mUrl;
	private double mLong;
	private double mLat;
	
	/* optional */
	private FSCategory mCategory = FSCategory.NONE;
	private int mLimit = -1;
	private double mRadius = -1;
	
	boolean mIsFirstParam;
	
	public SearchVenuesRequest(double lat, double lng) {
		mUrl = new StringBuilder();
		
		mLat = lat;
		mLong = lng;
	}
	
	public SearchVenuesRequest setLimit (int limit)	{
		if (limit > 50)
		{
			Log.w("FourSquare API", "VENUE - SEARCH: Only the last 50 results will be fetched!!");
		}
		mLimit = (limit > 50) ? 50 : limit ;
		
		return this;
	}
	
	public SearchVenuesRequest setCategory(FSCategory category) {
		mCategory = category;
		
		return this;
	}
	
	public SearchVenuesRequest setRadius(double radius)
	{
		mRadius = radius;
		
		return this;
	}
	
	public String getUrl() {
		mUrl.append(URI_PREFIX);
		mIsFirstParam = true;
		
		if (mLimit > 0)
		{
			mUrl.append(addUrlParam("limit", "" + mLimit));
		}
		
		if (mCategory != FSCategory.NONE) {
			mUrl.append(addUrlParam("categoryId", mCategory.getId()));
		}
		
		if (mRadius > 0)
		{
			mUrl.append(addUrlParam("radius", ""+ mRadius));
		}
		
		mUrl.append(addUrlParam("ll", mLat+"," + mLong));
		mUrl.append(FourSquareMgr.getInstance().getUrlAuthParams());
		
		
		
		return mUrl.toString();
	}
	
	private String addUrlParam(String name, String value)
	{
		if (mIsFirstParam) {
			mIsFirstParam = false;
			
			return "?" + name + "=" + value;
		}
		
		return "&" + name + "=" + value;
	}
	
	 
}
