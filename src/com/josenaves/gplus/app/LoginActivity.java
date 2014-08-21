package com.josenaves.gplus.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.josenaves.gplus.app.data.FriendsContract;
import com.josenaves.gplus.app.data.FriendsContract.FriendsEntry;

public class LoginActivity extends Activity implements
		OnConnectionFailedListener, ConnectionCallbacks,
		ResultCallback<LoadPeopleResult> {

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;

	private static final String TAG = LoginActivity.class.getSimpleName();

	/* Client used to interact with Google APIs. */
	private GoogleApiClient googleApiClient;

	/*
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean intentInProgress;

	/*
	 * Track whether the sign-in button has been clicked so that we know to
	 * resolve all issues preventing sign-in without waiting.
	 */
	private boolean signInClicked;

	/*
	 * Store the connection result from onConnectionFailed callbacks so that we
	 * can resolve them when the user clicks sign-in.
	 */
	private ConnectionResult connectionResult;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		googleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
	}

	@Override
	protected void onStart() {
		super.onStart();
		googleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (googleApiClient.isConnected()) {
			googleApiClient.disconnect();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// We've resolved any connection errors. googleApiClient can be used to
		// access Google APIs on behalf of the user.
		signInClicked = false;
		
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

		// ask for all visible people from user circles
		Plus.PeopleApi.loadVisible(googleApiClient, null).setResultCallback(this);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		googleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
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
				googleApiClient.connect();
			}
		}
	}

	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			intentInProgress = false;
			if (!googleApiClient.isConnecting()) {
				googleApiClient.connect();
			}
		}
	}

	public void onClick(View view) {
		if (view.getId() == R.id.sign_in_button && !googleApiClient.isConnecting()) {
			signInClicked = true;
			resolveSignInError();
		}
	}

	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
		if (connectionResult.hasResolution()) {
			try {
				intentInProgress = true;
				startIntentSenderForResult(connectionResult.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
			} 
			catch (SendIntentException e) {
				// The intent was canceled before it was sent. Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				intentInProgress = false;
				googleApiClient.connect();
			}
		}
	}

	@Override
	public void onResult(LoadPeopleResult peopleData) {
		if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
			PersonBuffer personBuffer = peopleData.getPersonBuffer();
			int count = personBuffer.getCount();
			
			if (count > 0) {
				// TODO Recreate DB
				//new FriendsDbHelper(this);
			}
			
			// get all friends and save them
			for (int i = 0; i < count; i++) {
				Person person = personBuffer.get(i);
				
				ContentValues values = new ContentValues();
				values.put(FriendsEntry._ID, person.getId());
				values.put(FriendsEntry.COLUMN_NAME_NAME, person.getDisplayName());
				values.put(FriendsEntry.COLUMN_NAME_IMAGE, person.getImage().getUrl());

				
				Uri uri = new Uri.Builder().scheme("content")
						.authority(FriendsContract.AUTHORITY)
						.appendPath("/friends")
						.build();
						
				uri = getContentResolver().insert(uri, values);
				Log.d(TAG, "Uri inserted = " + uri);
			}
		} 
		else {
			Log.e(TAG, "Error requesting visible circles: " + peopleData.getStatus());
		}

	}
	
	public void getFriends(View view) {
		Log.d(TAG, "getFriends");
		Intent intent = new Intent(this, FriendsActivity.class);
		startActivity(intent);
	}

}
