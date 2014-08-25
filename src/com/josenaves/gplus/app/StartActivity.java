package com.josenaves.gplus.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.common.SignInButton;

public class StartActivity extends GooglePlusActivity implements OnClickListener{

	private static final String LOG_TAG = StartActivity.class.getSimpleName();
	
	private SignInButton signinButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		signinButton = (SignInButton) findViewById(R.id.sign_in_button);
		signinButton.setOnClickListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (api.isConnected()) {
			api.disconnect();
		}
		signinButton.setEnabled(true);
	}
	
	@Override
	public void onClick(View view) {
		int viewId = view.getId();

		if (viewId == R.id.sign_in_button && !api.isConnecting()) {
			signinButton.setEnabled(false);
			api.connect();
		} 
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(LOG_TAG, "Connected - starting AboutMe");
		Intent intent = new Intent(this, AboutMeActivity.class);
		startActivity(intent);
	}

}
