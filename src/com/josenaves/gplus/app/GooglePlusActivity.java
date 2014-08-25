package com.josenaves.gplus.app;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.josenaves.gplus.app.helper.GooglePlusApiHelper;

public abstract class GooglePlusActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

	private static final String LOG_TAG = GooglePlusActivity.class.getSimpleName();
	
	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;
	
	private boolean intentInProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		configGplusApi();
	}
	
	@Override
	protected void onResume() {
		Log.d(LOG_TAG, "onResume");
		super.onResume();
		GooglePlusApiHelper.connect();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(LOG_TAG, "onStop");

		if (GooglePlusApiHelper.isConnected()) {
			GooglePlusApiHelper.disconnect();
		}
	}
	
	private void configGplusApi() {
		Log.d(LOG_TAG, "configGplusApi");

		GooglePlusApiHelper.init(this);
		GooglePlusApiHelper.addConnectionCallback(this);
		GooglePlusApiHelper.addConnectionFailedCallback(this);
	}

	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		Log.d(LOG_TAG, "onActivityResult");

		if (requestCode == RC_SIGN_IN) {
			intentInProgress = false;

			if (!GooglePlusApiHelper.isConnecting()) {
				GooglePlusApiHelper.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d(LOG_TAG, "onConnectionFailed");
		
		if (!intentInProgress && result.hasResolution()) {
			try {
				intentInProgress = true;
				startIntentSenderForResult(result.getResolution()
						.getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
			} 
			catch (SendIntentException e) {
				// The intent was canceled before it was sent. 
				// Return to the default state and attempt to 
				// connect to get an updated ConnectionResult.
				intentInProgress = false;
				GooglePlusApiHelper.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(LOG_TAG, "onConnected");
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.d(LOG_TAG, "onConnectionSuspended");

		GooglePlusApiHelper.connect();
	}
}
