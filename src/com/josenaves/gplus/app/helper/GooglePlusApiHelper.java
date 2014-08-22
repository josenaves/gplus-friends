package com.josenaves.gplus.app.helper;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

public final class GooglePlusApiHelper {
	
	private static final String LOG_TAG = GooglePlusApiHelper.class.getSimpleName();

	private GooglePlusApiHelper() {
	}

	/* Client used to interact with Google APIs. */
	private static GoogleApiClient API;

	public static void init(Context context) {
		API = new GoogleApiClient.Builder(context)
			.addApi(Plus.API)
			.addScope(Plus.SCOPE_PLUS_LOGIN)
			.build();
	}
	
	public static void addConnectionCallback(GoogleApiClient.ConnectionCallbacks listener) {
		API.registerConnectionCallbacks(listener);
	}
	
	public static void addConnectionFailedCallback(GoogleApiClient.OnConnectionFailedListener listener) {
		API.registerConnectionFailedListener(listener);
	}

	public static void connect() {
		API.connect();
	}

	public static void disconnect() {
		API.disconnect();
	}

	public static boolean isConnecting() {
		return API.isConnecting();
	}
	
	public static boolean isConnected() {
		return API.isConnected();
	}

	public static void revokeAccess() {
		
		if (API.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(API);
			Plus.AccountApi.revokeAccessAndDisconnect(API).setResultCallback( new ResultCallback<Status>() {
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
	
	public static GoogleApiClient getAPI() {
		return API;
	}
	

}
