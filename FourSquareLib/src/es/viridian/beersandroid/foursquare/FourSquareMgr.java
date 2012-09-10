package es.viridian.beersandroid.foursquare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import es.viridian.beersandroid.foursquare.exceptions.FSConnectionException;
import es.viridian.beersandroid.foursquare.exceptions.FSDataException;
import es.viridian.beersandroid.foursquare.exceptions.FSRuntimeException;
import es.viridian.beersandroid.foursquare.objects.FSUser;

public class FourSquareMgr {
	private static final String FOURSQUARE_LOG_TAG = "FOUR_SQUARE";
	private static final String AUTH_TOKEN_PREFS_KEY = "AUTH_TOKEN";
	private static final int FOURSQUARE_REQUEST_CODE = 1;
	private static final int HTTP_OK_CODE = 200;

	private FSUser user;

	private String callbackUrl;
	private String clientId;
	private String authToken;

	private Context ctx;
	private FourSquareLoginListener listener;

	protected boolean isOk;
	private static FourSquareMgr instance;

	public static FourSquareMgr getInstance() {

		if (instance == null) {
			instance = new FourSquareMgr();
		}

		return instance;
	}

	private FourSquareMgr() {
		isOk = false;
		user = null;
	}

	public boolean init(Context ctx, String clientId, String callback) {
		this.isOk = true;
		this.ctx = ctx;

		this.callbackUrl = callback;
		this.clientId = clientId;

		SharedPreferences prefs = ctx.getSharedPreferences("fourSquare",
				Context.MODE_PRIVATE);

		authToken = prefs.getString("AUTH_TOKEN_PREFS_KEY", null);

		return this.isOk;
	}

	public void end() {
		if (this.isOk) {
			this.isOk = false;
		}
	}

	public boolean isLoggedIn() {
		return authToken != null;
	}

	public void doLogin(Activity activityCallback,
			FourSquareLoginListener listener) {
		if (!isLoggedIn()) {
			if (!isOk) {
				Log.e(FOURSQUARE_LOG_TAG,
						"trying to use the FS manager without initializing it first");
				throw new FSRuntimeException(
						"trying to use the FS manager without initializing it first");
			}
			this.listener = listener;

			Intent i = new Intent(ctx, FourSquareActivity.class);

			i.putExtra("clientId", clientId);
			i.putExtra("callbackUrl", callbackUrl);

			activityCallback.startActivityForResult(i, FOURSQUARE_REQUEST_CODE);
		} else {
			listener.onSuccess(authToken);
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

	public FSUser getUser() throws FSDataException {
		if (user == null) {
			JSONObject userResponse = retrieveFSData("https://api.foursquare.com/v2/users/self?oauth_token="
					+ authToken + "&v=v=20120910");
			user = new FSUser();
			user.fillUser(userResponse);
		}

		return user;
	}

	public void onLoginCallback(int requestCode, int resultCode, Intent data) {

		if (requestCode == FOURSQUARE_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				authToken = data.getStringExtra("oauthToken");

				Editor prefs = ctx.getSharedPreferences("fourSquare",
						Context.MODE_PRIVATE).edit();

				prefs.putString(AUTH_TOKEN_PREFS_KEY, authToken);

				prefs.commit();

				Log.d("FourSquare", "Received a valid OAUTH token: "
						+ authToken);
				listener.onSuccess(authToken);

			}
			if (resultCode == Activity.RESULT_CANCELED) {
				listener.onCancel();
			}
		}

	}

	private boolean checkResponse(JSONObject response) throws JSONException {
		if (response == null)
			return false;
		// check if there is an error or warning in the message.
		String errorType = response.getString("errorType");

		if (errorType != null) {
			Log.e(FOURSQUARE_LOG_TAG, "there was an error calling FourSquare");

			String details = response.getString("errorDetail");

			Log.e(FOURSQUARE_LOG_TAG, details);
		}

		int httpCode = response.getInt("code");

		return httpCode == HTTP_OK_CODE;
	}

	private JSONObject postFSData(String url, ContentValues params) {

		return null;
	}

	private JSONObject retrieveFSData(String url) throws FSDataException {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (IOException e) {
			Log.e(FOURSQUARE_LOG_TAG,
					"there was a problem calling fourSquare... in getUser method");
			throw new FSConnectionException("cannot connecto to foursquare", e);
		}

		return parseFSResponse(response);
	}

	private JSONObject parseFSResponse(HttpResponse response)
			throws FSDataException {
		JSONObject data = null;
		JSONObject fsResponse = null;
		try {
			if (response.getStatusLine().getStatusCode() == HTTP_OK_CODE) {
				data = getJsonFromStream(response.getEntity().getContent());
			}
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
