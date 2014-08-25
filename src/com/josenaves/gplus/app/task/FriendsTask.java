package com.josenaves.gplus.app.task;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.josenaves.gplus.app.FriendsActivity;
import com.josenaves.gplus.app.data.FriendsContract.FriendsEntry;

public final class FriendsTask extends AsyncTask<Void, Void, Void> {
	
	private final String LOG_TAG = FriendsTask.class.getSimpleName();

	private GoogleApiClient api;
	private FriendsActivity view;
	
	private ProgressDialog progressDialog;
	
	public FriendsTask(FriendsActivity view, GoogleApiClient api) {
		this.view = view;
		this.api = api;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Log.d(LOG_TAG, "Asking for friends list...");
		progressDialog = ProgressDialog.show(view, "Please wait", "Getting your friends...", true);
	}

	@Override
	protected Void doInBackground(Void... params) {
		
		if (peopleCount() != 0) {
			Log.w(LOG_TAG, "There are people on db... done!");
			
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}

			return null;
		}
		
		if (api.isConnected()) {
			// ask for all visible people from user circles
			PendingResult<LoadPeopleResult> callback = Plus.PeopleApi.loadVisible(api, null);
			callback.setResultCallback(new ResultCallback<People.LoadPeopleResult>() {

				@Override
				public void onResult(LoadPeopleResult result) {
					if (result.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
						PersonBuffer personBuffer = result.getPersonBuffer();
						int count = personBuffer.getCount();
					
						Log.d(LOG_TAG, "Total people in circles = " + count);
						
						// get all friends and save them
						for (int i = 0; i < count; i++) {
							Person person = personBuffer.get(i);
							Log.d(LOG_TAG, "Person (from G+) = " + person + ", id = " + person.getId() + ", displayname: " + person.getDisplayName());

							long id = addFriend(person.getId(), person.getDisplayName(), person.getImage().getUrl());
							Log.d(LOG_TAG, "Person id: " + id + " created in db.");
						}
					} 
					else {
						Log.e(LOG_TAG, "Error requesting visible circles: " + result.getStatus());
					}
					
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
				}
				
			});
		}
		else {
			Log.w(LOG_TAG, "API is not connected!");
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
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
			
			Uri friendInsertUri = view.getContentResolver().insert(FriendsEntry.CONTENT_URI, values);
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
		Cursor cursor = view.getContentResolver().query(
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
	
	private int peopleCount() {
		// A "projection" defines the columns that will be returned for each row
		String[] projection = new String[] {"count(*)"};
		
		// Defines a string to contain the selection clause
		String selectionClause = null;
		
		// Initializes an array to contain selection arguments
		String[] selectionArgs = null;
		
		// check if this friend already exists in the database
		Cursor cursor = view.getContentResolver().query(
				FriendsEntry.CONTENT_URI,
				projection,
				selectionClause, 
				selectionArgs,
				null);
		
		if (cursor.getCount() == 0) {
			return 0;
		}

	    cursor.moveToFirst();
	    return cursor.getInt(0);
	}
	
}
