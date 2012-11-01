package es.viridian.foursquare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Activity which implements onActivityResult to manage automatically the callback from the foursquare login. 
 * 
 * @author Acampoh
 *
 */
public abstract class AbstractFourSquareActivity extends Activity {

	protected FourSquareMgr mFSMgr;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFSMgr = FourSquareMgr.getInstance();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);	
		mFSMgr.onLoginCallback(requestCode, resultCode, data);    		
    		
    }
}
