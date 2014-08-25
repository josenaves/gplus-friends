package com.josenaves.gplus.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.josenaves.gplus.app.task.AboutMeTask;

public class AboutMeActivity extends Activity {

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
		
		AboutMeTask task = new AboutMeTask(this);
		task.execute();
	}
	
	public void getFriends(View view) {
		Intent intent = new Intent(this, FriendsActivity.class);
		startActivity(intent);
	}
}
