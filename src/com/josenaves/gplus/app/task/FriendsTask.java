package com.josenaves.gplus.app.task;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.josenaves.gplus.app.data.FriendsContract.FriendsEntry;
import com.josenaves.gplus.app.helper.GooglePlusApiHelper;

public final class FriendsTask extends AsyncTask<Void, Void, Void> implements ResultCallback<LoadPeopleResult> {
	
	private final String LOG_TAG = FriendsTask.class.getSimpleName();
	
	private ProgressDialog dialog;
	
	private Context context;
	
	public FriendsTask(Context context) {
		this.context = context;
	}

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
				Log.d(LOG_TAG, "Person (from G+) = " + person + ", id = " + person.getId());

				long id = addFriend(person.getDisplayName(), person.getImage().getUrl());
				Log.d(LOG_TAG, "Inserted person --> id (generated) = " + id);
			}
		} 
		else {
			Log.e(LOG_TAG, "Error requesting visible circles: " + peopleData.getStatus());
		}
	}
	
	
	private long addFriend(String name, String imageUrl) {
		Log.v(LOG_TAG, "inserting " + name + " with url " + imageUrl);
		
		// check if this friend already exists in the database
		Cursor cursor = context.getContentResolver().query(
				FriendsEntry.CONTENT_URI, 
				new String[]{FriendsEntry._ID},
				FriendsEntry.COLUMN_NAME_NAME + " = ?", 
				new String[]{name},
				null);
		
		if (cursor.moveToFirst()) {
			Log.v(LOG_TAG, "Found " + name + " in the database!");
			int  columnIndex = cursor.getColumnIndex(FriendsEntry._ID);
			return cursor.getLong(columnIndex);
		}
		else {
			Log.v(LOG_TAG, "Did not find it ! Gonna insert it nows! ");
			
			ContentValues values = new ContentValues();
			values.put(FriendsEntry.COLUMN_NAME_NAME, name);
			values.put(FriendsEntry.COLUMN_NAME_IMAGE, imageUrl);
			
			Uri friendInsertUri = context.getContentResolver().insert(FriendsEntry.CONTENT_URI, values);
			return ContentUris.parseId(friendInsertUri); 
		}
	}

}
