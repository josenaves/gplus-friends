package com.josenaves.gplus.app;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

public abstract class GooglePlusActivity extends Activity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private static final String LOG_TAG = GooglePlusActivity.class.getSimpleName();

	protected GoogleApiClient api;

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;

	private boolean intentInProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		configApi();
	}

//	@Override
//	protected void onResume() {
//		Log.d(LOG_TAG, "onResume");
//		super.onResume();
//		api.connect();
//	}

//	@Override
//	protected void onStop() {
//		super.onStop();
//		Log.d(LOG_TAG, "onStop");
//
//		if (api.isConnected()) {
//			api.disconnect();
//		}
//	}

	private void configApi() {
		Log.d(LOG_TAG, "configGplusApi");
		
		api = new GoogleApiClient.Builder(this)
				.addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN)
				.build();
		
		api.registerConnectionCallbacks(this);
		api.registerConnectionFailedListener(this);
	}

	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		Log.d(LOG_TAG, "onActivityResult");
		if (requestCode == RC_SIGN_IN) {
			intentInProgress = false;
			if (!api.isConnecting()) {
				api.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d(LOG_TAG, "onConnectionFailed");

		if (!intentInProgress && result.hasResolution()) {
			try {
				intentInProgress = true;
				startIntentSenderForResult(result.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
			} 
			catch (SendIntentException e) {
				// The intent was canceled before it was sent.
				// Return to the default state and attempt to
				// connect to get an updated ConnectionResult.
				intentInProgress = false;
				api.connect();
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
		api.connect();
	}
	
	public void revoke() {
		if (api.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(api);
			Plus.AccountApi.revokeAccessAndDisconnect(api).setResultCallback( new ResultCallback<Status>() {
				@Override
				public void onResult(Status result) {
					Log.d(LOG_TAG, "Revoking access... result = " + result.getStatusMessage());
				}
			});
		}
		else {
			Log.d(LOG_TAG, "Client not connected.");
		}
	}
	
}
