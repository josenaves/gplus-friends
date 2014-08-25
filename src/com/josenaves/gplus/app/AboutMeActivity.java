package com.josenaves.gplus.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
	
	@Override
	protected void onResume() {
		Log.d(LOG_TAG, "onResume");
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		Log.d(LOG_TAG, "onPause");
		super.onPause();
	}
	
	public void revokeAccess(View view) {
		int viewId = view.getId();
		if (viewId == R.id.revoke_button) {
			revoke();
			
			// go back to login screen
			Intent intent = new Intent(this, StartActivity.class);
			startActivity(intent);
		}
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
