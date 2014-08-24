package com.josenaves.gplus.app;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.josenaves.gplus.app.data.FriendsContract.FriendsEntry;
import com.josenaves.gplus.app.task.FriendsTask;

public class FriendsActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter friendsAdapter;

	private static final int FRIENDS_LOADER = 0;

	private static final String[] FRIENDS_COLUMNS = {
			FriendsEntry.COLUMN_NAME_ID, FriendsEntry.COLUMN_NAME_NAME,
			FriendsEntry.COLUMN_NAME_IMAGE };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		
		friendsAdapter = new SimpleCursorAdapter(
				this, 
				R.layout.list_item_friend, 
				null, 
				new String[] { FriendsEntry.COLUMN_NAME_NAME }, 
				new int[] { R.id.list_item_name },
				0);
		
		// get a reference to the listview and attach our adapter to it
		ListView listView = (ListView) findViewById(R.id.listview_friends);
		listView.setAdapter(friendsAdapter);

		// Initializes the CursorLoader. 
		getLoaderManager().initLoader(FRIENDS_LOADER, null, this);
	}

	@Override
	public void onStart() {
		super.onStart();
		updateFriendsList();
	}

	private void updateFriendsList() {
		FriendsTask task = new FriendsTask(this);
		task.execute();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.

		String sortOrder = FriendsEntry.COLUMN_NAME_NAME + " COLLATE LOCALIZED ASC";

		return new CursorLoader(this, FriendsEntry.CONTENT_URI, FRIENDS_COLUMNS, null, null, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in.
		friendsAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
		friendsAdapter.swapCursor(null);
	}
}
