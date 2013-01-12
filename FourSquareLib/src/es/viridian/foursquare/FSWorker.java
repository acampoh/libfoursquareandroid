package es.viridian.foursquare;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import es.viridian.foursquare.exceptions.FSConnectionException;
import es.viridian.foursquare.exceptions.FSDataException;
import es.viridian.foursquare.exceptions.FSRuntimeException;

public class FSWorker extends AsyncTask<HttpUriRequest, Integer, JSONObject> {
	private static final String FOURSQUARE_LOG_TAG = "FOUR_SQUARE";
	
	private FSRequestCallback mCallback;

	public FSWorker(FSRequestCallback callback) {
		mCallback = callback;
	}

	@Override
	protected JSONObject doInBackground(HttpUriRequest... params) {
		HttpClient client = new DefaultHttpClient();

		if (params.length != 1)
			throw new FSRuntimeException("trying to fetch more than one URL");

		HttpResponse response = null;
		try {
			response = client.execute(params[0]);
		} catch (IOException e) {
			Log.e(FOURSQUARE_LOG_TAG,
					"there was a problem calling fourSquare... in getUser method");
			throw new FSConnectionException("cannot connecto to foursquare", e);
		}

		try {
			return FourSquareMgr.getInstance().parseFSResponse(response);
		} catch (FSDataException e) {
			Log.e(FOURSQUARE_LOG_TAG, "There was a problem fetching Data!!");
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		mCallback.setAsyncResult(result);
	}
}
