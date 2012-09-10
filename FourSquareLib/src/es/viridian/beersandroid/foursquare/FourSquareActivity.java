package es.viridian.beersandroid.foursquare;

import es.viridian.foursquarelib.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class FourSquareActivity extends Activity {
	private static final int RESULT_ERROR = RESULT_FIRST_USER+1;
	
	private String loginUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Log.d("FOURSQUARE", "Attemping to logIn foursquare....");

		setContentView(R.layout.foursquare_login);
		
		String clientId = getIntent().getStringExtra("clientId");
		String callbackUrl = getIntent().getStringExtra("callbackUrl");

		loginUrl = getString(R.string.login_url, clientId, callbackUrl);

		// If authentication works, we'll get redirected to a url with a pattern
		// like:
		//
		// http://YOUR_REGISTERED_REDIRECT_URI/#access_token=ACCESS_TOKEN
		//
		// We can override onPageStarted() in the web client and grab the token
		// out.
		WebView webview = (WebView) findViewById(R.id.fourSquareView);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient() {
			public void onPageStarted(WebView view, String url, Bitmap favicon) {

				String fragment = "#access_token=";
				int start = url.indexOf(fragment);
				if (start > -1) {
					// You can use the accessToken for api calls now.
					String accessToken = url.substring(
							start + fragment.length(), url.length());

					Intent i = new Intent();
					i.putExtra("oauthToken", accessToken);

					setResult(RESULT_OK, i);

					finish();
				}
			}
			
			
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Intent i = new Intent();
				
				i.putExtra("errorCode", errorCode);
				i.putExtra("errorDesc", description);
				
				setResult(RESULT_ERROR, i);
				
				finish();
				
				
			}
		});
		webview.loadUrl(loginUrl);
	}
}