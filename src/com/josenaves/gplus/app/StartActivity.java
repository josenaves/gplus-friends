package com.josenaves.gplus.app;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class StartActivity extends Activity implements
		android.view.View.OnClickListener, ConnectionCallbacks,
		OnConnectionFailedListener {

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;

	private SignInButton signinButton;
	private boolean intentInProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		signinButton = (SignInButton) findViewById(R.id.sign_in_button);
		signinButton.setOnClickListener(this);
	}

	protected void onStart() {
		super.onStart();
	}

	protected void onStop() {
		super.onStop();

		if (GooglePlusApiHelper.isConnected()) {
			GooglePlusApiHelper.disconnect();
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.sign_in_button && !GooglePlusApiHelper.isConnecting()) {
			signinButton.setEnabled(false);
			GooglePlusApiHelper.connect();
		}
	}


	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			intentInProgress = false;

			if (!GooglePlusApiHelper.isConnecting()) {
				GooglePlusApiHelper.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!intentInProgress && result.hasResolution()) {
			try {
				intentInProgress = true;
				startIntentSenderForResult(result.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
			} 
			catch (SendIntentException e) {
				// The intent was canceled before it was sent. Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				intentInProgress = false;
				GooglePlusApiHelper.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Intent intent = new Intent(this, ListActivity.class);
		startActivity(intent);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		GooglePlusApiHelper.connect();
	}
}
