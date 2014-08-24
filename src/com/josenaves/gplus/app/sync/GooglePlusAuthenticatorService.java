package com.josenaves.gplus.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GooglePlusAuthenticatorService extends Service {

	private GooglePlusAuthenticator authenticator;
	
	@Override
	public void onCreate() {
		authenticator = new GooglePlusAuthenticator(this);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return authenticator.getIBinder();
	}

}
