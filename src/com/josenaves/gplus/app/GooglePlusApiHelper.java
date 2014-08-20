package com.josenaves.gplus.app;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

public final class GooglePlusApiHelper {

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
		Plus.AccountApi.clearDefaultAccount(API);
		Plus.AccountApi.revokeAccessAndDisconnect(API).setResultCallback(
				new ResultCallback<Status>() {
					@Override
					public void onResult(Status result) {
					}

				});
	}

}
