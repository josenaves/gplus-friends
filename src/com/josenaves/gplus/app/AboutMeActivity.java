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
		
		AboutMeTask task = new AboutMeTask(this);
		task.execute();
	}

	@Override
	public void onStart() {
		Log.d(LOG_TAG, "onStart");
		super.onStart();
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
	
	@Override
	protected void onRestart() {
		Log.d(LOG_TAG, "onRestart");
		super.onRestart();
	}

	public void getFriends(View view) {
		Intent intent = new Intent(this, FriendsActivity.class);
		startActivity(intent);
	}
}
