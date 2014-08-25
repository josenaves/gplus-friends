package com.josenaves.gplus.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.common.SignInButton;
import com.josenaves.gplus.app.helper.GooglePlusApiHelper;

public class StartActivity extends GooglePlusActivity implements OnClickListener{

	private static final String LOG_TAG = StartActivity.class.getSimpleName();
	
	private SignInButton signinButton;
	private Button revokeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		signinButton = (SignInButton) findViewById(R.id.sign_in_button);
		signinButton.setOnClickListener(this);

		revokeButton = (Button) findViewById(R.id.revoke_button);
		revokeButton.setOnClickListener(this);
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

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(LOG_TAG, "Connected - starting AboutMe");
		Intent intent = new Intent(this, AboutMeActivity.class);
		startActivity(intent);
	}

}
