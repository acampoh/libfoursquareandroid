package es.viridian.foursquare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.util.Log;
import es.viridian.foursquare.exceptions.FSConnectionException;
import es.viridian.foursquare.exceptions.FSDataException;
import es.viridian.foursquare.exceptions.FSRuntimeException;
import es.viridian.foursquare.objects.FSUser;
import es.viridian.foursquare.objects.FSVenue;

public class FourSquareMgr {
	private static final String FOURSQUARE_LOG_TAG = "FOUR_SQUARE";
	private static final String AUTH_TOKEN_PREFS_KEY = "AUTH_TOKEN";
	private static final int FOURSQUARE_REQUEST_CODE = 1;
	private static final int HTTP_OK_CODE = 200;
	private static final String FOURSQUARE_URL = "https://api.foursquare.com/v2/";
	private static final String FOURSQUARE_API_VERSION = "v=20120930";
	private FSUser mCurrentUser;

	private String mCallbackUrl;
	private String mClientId;
	private String mAuthToken;

	private Context mCtx;
	private FourSquareLoginListener mListener;
	private FSWorker mWorker;

	protected boolean mIsOk;
	private static FourSquareMgr mInstance;

	public static FourSquareMgr getInstance() {

		if (mInstance == null) {
			mInstance = new FourSquareMgr();
		}

		return mInstance;
	}

	private FourSquareMgr() {
		mIsOk = false;
		mCurrentUser = null;
	}

	public boolean init(Context ctx, String clientId, String callback) {
		this.mIsOk = true;
		this.mCtx = ctx;

		this.mCallbackUrl = callback;
		this.mClientId = clientId;
		this.mWorker = new FSWorker();

		SharedPreferences prefs = ctx.getSharedPreferences("fourSquare",
				Context.MODE_PRIVATE);

		mAuthToken = prefs.getString(AUTH_TOKEN_PREFS_KEY, null);

		return this.mIsOk;
	}

	public void end() {
		if (this.mIsOk) {
			this.mIsOk = false;
		}
	}

	public boolean isLoggedIn() {
		return mAuthToken != null;
	}

	public void doLogin(Activity activityCallback,
			FourSquareLoginListener listener) {
		if (!isLoggedIn()) {
			if (!mIsOk) {
				Log.e(FOURSQUARE_LOG_TAG,
						"trying to use the FS manager without initializing it first");
				throw new FSRuntimeException(
						"trying to use the FS manager without initializing it first");
			}
			this.mListener = listener;

			Intent i = new Intent(mCtx, FourSquareLoginActivity.class);

			i.putExtra("clientId", mClientId);
			i.putExtra("callbackUrl", mCallbackUrl);

			activityCallback.startActivityForResult(i, FOURSQUARE_REQUEST_CODE);
		} else {
			listener.onSuccess(mAuthToken);
		}
	}

	private JSONObject getJsonFromStream(InputStream stream)
			throws IOException, JSONException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream), 4096);
		StringBuilder builder = new StringBuilder();
		String line;

		JSONObject obj = null;

		line = reader.readLine();
		while (line != null) {
			builder.append(line);
			line = reader.readLine();
		}

		obj = new JSONObject(builder.toString());

		return obj;
	}

	public FSUser getCurrentUser() throws FSDataException {
		if (mCurrentUser == null) {
			JSONObject userResponse = retrieveFSData(FOURSQUARE_URL + "users/self?oauth_token="
					+ mAuthToken + "&v=" + FOURSQUARE_API_VERSION);
			mCurrentUser = new FSUser();
			mCurrentUser.fillUser(userResponse);
		}

		return mCurrentUser;
	}

	public FSVenue checkIn(String venueId) throws FSDataException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("venueId", venueId));

		JSONObject response = postFSData(FOURSQUARE_URL + "checkins/add", params, true);
		FSVenue venue = new FSVenue();
		venue.fillVenue(response);

		return venue;
	}
	
	public List<FSVenue> searchVenues(Location location, int limit) throws FSDataException {
		JSONObject result = retrieveFSData(FOURSQUARE_URL + "venues/search?ll="+ location.getLatitude()+"," + location.getLongitude() + "&limit=" + limit + getUrlAuthParams());
		
		ArrayList<FSVenue> venues = new ArrayList<FSVenue>();
		JSONArray jsonVenues = null;
		
		try {
			jsonVenues = result.getJSONArray("venues");
		} catch (JSONException e) {
			throw new FSDataException("There was a problem parsing the response from FourSquare! method: searchVenues", e);
		}
		
		for (int i = 0; i < jsonVenues.length(); i++) {
			if (jsonVenues.optJSONObject(i) != null) {
				FSVenue venue = new FSVenue();
				try {
					venue.fillVenue(jsonVenues.getJSONObject(i));
				} catch (JSONException e) {
					throw new FSDataException(
							"There was a problem parsing the response from FourSquare! method: searchVenues", e);
				}
			
				venues.add(venue);
			}
		}
		
		return venues;
	}
	
	private String getUrlAuthParams()
	{
		return "&oauth_token=" + mAuthToken + "&"+ FOURSQUARE_API_VERSION;
	}

	public void onLoginCallback(int requestCode, int resultCode, Intent data) {

		if (requestCode == FOURSQUARE_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				mAuthToken = data.getStringExtra("oauthToken");

				Editor prefs = mCtx.getSharedPreferences("fourSquare",
						Context.MODE_PRIVATE).edit();

				prefs.putString(AUTH_TOKEN_PREFS_KEY, mAuthToken);

				prefs.commit();

				Log.d("FourSquare", "Received a valid OAUTH token: "
						+ mAuthToken);
				mListener.onSuccess(mAuthToken);

			}
			if (resultCode == Activity.RESULT_CANCELED) {
				mListener.onCancel();
			}
		}
	}

	private boolean checkResponse(JSONObject response) throws JSONException {
		if (response == null)
			return false;
		
		int httpCode = response.getInt("code");
		
		if (httpCode != HTTP_OK_CODE)
		{
			// check if there is an error or warning in the message.
			String errorType = response.optString("errorType");

			if (errorType != null) {
				Log.e(FOURSQUARE_LOG_TAG, "there was an error calling FourSquare");

				String details = response.optString("errorDetail");
				Log.e(FOURSQUARE_LOG_TAG, details);
			}	
		}

		return httpCode == HTTP_OK_CODE;
	}

	private JSONObject postFSData(String url, List<NameValuePair> params, boolean needsAuth)
			throws FSDataException {

		if (needsAuth)
		{
			params.add(new BasicNameValuePair("oauth_token", mAuthToken));
			params.add(new BasicNameValuePair("v", FOURSQUARE_API_VERSION));
		}
		
		HttpPost request = new HttpPost(url);
		try {
			request.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException ex) {
			throw new FSDataException(
					"There was a problem parsing request params", ex);
		}

		return fetchURL(request);
	}

	private JSONObject retrieveFSData(String url) throws FSDataException {
		return fetchURL(new HttpGet(url));
	}
	
	private JSONObject fetchURL(HttpUriRequest request) throws FSDataException {
		mWorker.execute(request);
		JSONObject result = null;

		try {
			result = mWorker.get();
		} catch (InterruptedException e) {// DO nothing
		} catch (ExecutionException e) {
			throw new FSDataException(
					"There was some problems in async task", e);
		}

		return result;
	}
	

	public JSONObject parseFSResponse(HttpResponse response)
			throws FSDataException {
		JSONObject data = null;
		JSONObject fsResponse = null;
		try {
			data = getJsonFromStream(response.getEntity().getContent());
		} catch (IOException e) {
			Log.e(FOURSQUARE_LOG_TAG,
					"there was a problem calling fourSquare... in getUser method");
			throw new FSConnectionException("cannot connecto to foursquare", e);
		} catch (JSONException e) {
			Log.e(FOURSQUARE_LOG_TAG,
					"there was a problem calling fourSquare... in getUser method");
			throw new FSConnectionException("cannot connecto to foursquare", e);
		}

		if (data != null) {
			try {
				checkResponse(data.getJSONObject("meta"));
				fsResponse = data.getJSONObject("response");
			} catch (JSONException e) {
				Log.e(FOURSQUARE_LOG_TAG,
						"there was a problem parsing FourSquare messages...");
				throw new FSDataException(
						"There was something wrong parsing JSON from fourSquare",
						e);
			}
		}

		return fsResponse;

	}

}
