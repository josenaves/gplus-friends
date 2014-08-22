package com.josenaves.gplus.app;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.josenaves.gplus.app.data.FriendsContract.FriendsEntry;

class FriendsTask extends AsyncTask<Void, Void, Void> implements ResultCallback<LoadPeopleResult> {
	
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
				values.put(FriendsEntry._ID, person.getId());
				values.put(FriendsEntry.COLUMN_NAME_NAME, person.getDisplayName());
				values.put(FriendsEntry.COLUMN_NAME_IMAGE, person.getImage().getUrl());

				Log.d(LOG_TAG, "Name = " + person.getDisplayName() + ", url = " + person.getImage().getUrl());
			}
		} 
		else {
			Log.e(LOG_TAG, "Error requesting visible circles: " + peopleData.getStatus());
		}

	}

}
