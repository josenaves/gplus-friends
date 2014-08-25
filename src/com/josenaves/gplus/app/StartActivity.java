package com.josenaves.gplus.app;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.josenaves.gplus.app.helper.GooglePlusApiHelper;

public class StartActivity extends Activity implements
		OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;

	private SignInButton signinButton;
	private Button revokeButton;

	private boolean intentInProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		signinButton = (SignInButton) findViewById(R.id.sign_in_button);
		signinButton.setOnClickListener(this);

		revokeButton = (Button) findViewById(R.id.revoke_button);
		revokeButton.setOnClickListener(this);

		configGplusApi();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
		configGplusApi();
	}

	protected void onStop() {
		super.onStop();

		if (GooglePlusApiHelper.isConnected()) {
			GooglePlusApiHelper.disconnect();
		}
	}

	private void configGplusApi() {
		GooglePlusApiHelper.init(this);
		GooglePlusApiHelper.addConnectionCallback(this);
		GooglePlusApiHelper.addConnectionFailedCallback(this);
	}

	@Override
	public void onClick(View view) {
		int viewId = view.getId();

		if (viewId == R.id.sign_in_button && !GooglePlusApiHelper.isConnecting()) {
			signinButton.setEnabled(false);
			GooglePlusApiHelper.connect();
		} 
		else if (viewId == R.id.revoke_button) {
			GooglePlusApiHelper.revokeAccess();
			signinButton.setEnabled(true);
		}
	}

	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
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
		Intent intent = new Intent(this, AboutMeActivity.class);
		startActivity(intent);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		GooglePlusApiHelper.connect();
	}
}
