package com.josenaves.gplus.app.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
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
	
	private ProgressDialog progress;
	
	private Activity context;
	
	public FriendsTask(Activity context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progress = ProgressDialog.show(context, "Please wait", "Getting your friends data ...", true);
	}

	@Override
	protected Void doInBackground(Void... params) {

		// ask for all visible people from user circles
		Plus.PeopleApi.loadVisible(GooglePlusApiHelper.getAPI(), null).setResultCallback(this);

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (progress.isShowing()) {
			progress.dismiss();
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
				Log.d(LOG_TAG, "Person (from G+) = " + person + ", id = " + person.getId() + ", displayname: " + person.getDisplayName());

				long id = addFriend(person.getId(), person.getDisplayName(), person.getImage().getUrl());
			}
		} 
		else {
			Log.e(LOG_TAG, "Error requesting visible circles: " + peopleData.getStatus());
		}
	}
	
	
	private long addFriend(String gid, String name, String imageUrl) {
		long prevId = previouslyInserted(gid);
		prevId = -1;
		if (prevId == -1) {
			Log.v(LOG_TAG, "Did not find it ! Gonna insert it now! ");
			Log.v(LOG_TAG, "inserting person with id: " + gid + ", name:" + name + ", url:" + imageUrl);

			ContentValues values = new ContentValues();
			values.put(FriendsEntry.COLUMN_NAME_GID, gid);
			values.put(FriendsEntry.COLUMN_NAME_NAME, name);
			values.put(FriendsEntry.COLUMN_NAME_IMAGE, imageUrl);
			
			Uri friendInsertUri = context.getContentResolver().insert(FriendsEntry.CONTENT_URI, values);
			Log.v(LOG_TAG, "URI " + friendInsertUri);

			return ContentUris.parseId(friendInsertUri); 
		}
		
		return prevId;
	}
	
	
	// FIXME: for some reason, this method is not working as it should. It always find the row, as the where clause never work.
	private long previouslyInserted(String gid) {
		// A "projection" defines the columns that will be returned for each row
		String[] projection = new String[]{FriendsEntry.COLUMN_NAME_ID, FriendsEntry.COLUMN_NAME_GID, FriendsEntry.COLUMN_NAME_NAME, FriendsEntry.COLUMN_NAME_IMAGE};
		
		// Defines a string to contain the selection clause
		String selectionClause = FriendsEntry.COLUMN_NAME_GID + " = ?";
		
		// Initializes an array to contain selection arguments
		String[] selectionArgs = {gid};
		
		// check if this friend already exists in the database
		Cursor cursor = context.getContentResolver().query(
				FriendsEntry.CONTENT_URI,
				projection,
				selectionClause, 
				selectionArgs,
				null);
		
		if (null != cursor && cursor.getCount() > 0 && cursor.moveToFirst()) {
			Log.v(LOG_TAG, "Cursor.getCount:" + cursor.getCount());

			Log.v(LOG_TAG, "Cursor.getColumnCount:" + cursor.getColumnCount());
			
			for (String column : cursor.getColumnNames()) {
				Log.v(LOG_TAG, "Cursor column:" + column + ", value: " + cursor.getString(cursor.getColumnIndex(column)) );
			}
			
			
			int columnIndex = cursor.getColumnIndex(FriendsEntry.COLUMN_NAME_ID);
			return cursor.getLong(columnIndex);
		}
		
		return -1;
	}

}
