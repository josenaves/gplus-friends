package com.josenaves.gplus.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.josenaves.gplus.app.contentprovider.FriendsContract;

public class StartActivity extends Activity implements
		android.view.View.OnClickListener, ConnectionCallbacks,
		OnConnectionFailedListener {

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

		if (viewId == R.id.sign_in_button
				&& !GooglePlusApiHelper.isConnecting()) {
			signinButton.setEnabled(false);
			GooglePlusApiHelper.connect();
		} else if (viewId == R.id.revoke_button) {
			GooglePlusApiHelper.revokeAccess();
			signinButton.setEnabled(true);
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
				startIntentSenderForResult(result.getResolution()
						.getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
			} 
			catch (SendIntentException e) {
				// The intent was canceled before it was sent. Return to the
				// default
				// state and attempt to connect to get an updated
				// ConnectionResult.
				intentInProgress = false;
				GooglePlusApiHelper.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// load all friends
		new FriendsTask().execute();

		//		Intent intent = new Intent(this, FriendsActivity.class);
		//		startActivity(intent);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		GooglePlusApiHelper.connect();
	}

	private class FriendsTask extends AsyncTask<Void, Void, Void> implements ResultCallback<LoadPeopleResult> {
		
		private final String LOG_TAG = FriendsTask.class.getSimpleName();
		
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Getting friends data...");
			this.dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			// ask for all visible people from user circles
			Plus.PeopleApi.loadVisible(GooglePlusApiHelper.getAPI(), null).setResultCallback(this);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}

		@Override
		public void onResult(LoadPeopleResult peopleData) {
			if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
				PersonBuffer personBuffer = peopleData.getPersonBuffer();
				int count = personBuffer.getCount();
				
				Log.d(LOG_TAG, "Total people in circles = " + count);
				
				// get all friends and save them
				for (int i = 0; i < count; i++) {
					Person person = personBuffer.get(i);

					ContentValues values = new ContentValues();
					values.put(FriendsContract.Friends._ID, person.getId());
					values.put(FriendsContract.Friends.COLUMN_NAME_NAME, person.getDisplayName());
					values.put(FriendsContract.Friends.COLUMN_NAME_IMAGE, person.getImage().getUrl());

					Log.d(LOG_TAG, "Name = " + person.getDisplayName() + ", url = " + person.getImage().getUrl());
				}
			} 
			else {
				Log.e(LOG_TAG, "Error requesting visible circles: " + peopleData.getStatus());
			}

		}

	}

}
