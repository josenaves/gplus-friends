package com.josenaves.gplus.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.Plus;
import com.josenaves.gplus.app.task.AboutMeTask;

public class AboutMeActivity extends GooglePlusActivity {

	private static final String LOG_TAG = AboutMeActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_me);
	}

	@Override
	public void onStart() {
		Log.d(LOG_TAG, "onStart");
		super.onStart();
		
		api.connect();
	}
	
	public void revokeAccess(View view) {
		
		Log.d(LOG_TAG, "revokeAccess");
		
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				if (api.isConnected()) {
					Plus.AccountApi.clearDefaultAccount(api);
					PendingResult<com.google.android.gms.common.api.Status> pending =  Plus.AccountApi.revokeAccessAndDisconnect(api);
					pending.setResultCallback(new ResultCallback<com.google.android.gms.common.api.Status>() {
						@Override
						public void onResult(com.google.android.gms.common.api.Status result) {
							Log.d(LOG_TAG, "Revoking access... result = " + result.getStatusMessage());
						}
					});
				} 
				else {
					Log.d(LOG_TAG, "Client not connected.");
				}
	
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				// go back to login screen
				Intent intent = new Intent(getApplicationContext(), StartActivity.class);
				startActivity(intent);
			}
		}.execute();
	}

	public void getFriends(View view) {
		Intent intent = new Intent(this, FriendsActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(LOG_TAG, "Connected - starting AboutMe");
		AboutMeTask task = new AboutMeTask(this, api);
		task.execute();
	}	
}
