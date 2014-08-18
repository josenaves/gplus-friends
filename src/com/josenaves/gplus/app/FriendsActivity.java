package com.josenaves.gplus.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;

import com.josenaves.gplus.app.contentprovider.FriendsContract;
import com.josenaves.gplus.app.contentprovider.FriendsContract.Friends;

public class FriendsActivity extends ActionBarActivity 
	implements LoaderManager.LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter adapter;
	
    private static final int LOADER = 66;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		
		// creates an empty adapter we'll use to display data
		adapter = new SimpleCursorAdapter(getParent(), 
				android.R.layout.simple_list_item_1, null, 
				new String[] {FriendsContract.Friends.COLUMN_NAME_NAME},
				new int[] {android.R.id.text1},
				0);
		
        // Initializes the CursorLoader. 
		LoaderManager loaderManager = getSupportLoaderManager();
		loaderManager.initLoader(LOADER, null, this);
	}
	

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
	    // This is called when a new Loader needs to be created.  This
	    // sample only has one Loader, so we don't care about the ID.
	    // First, pick the base URI to use depending on whether we are
	    // currently filtering.
	    Uri baseUri = FriendsContract.CONTENT_URI;
	    
	    // Now create and return a CursorLoader that will take care of
	    // creating a Cursor for the data being displayed.
	    return new CursorLoader(getParent(), baseUri,null, null, null, Friends.COLUMN_NAME_NAME + " COLLATE LOCALIZED ASC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
	    // Swap the new cursor in.  (The framework will take care of closing the
	    // old cursor once we return.)
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	    // This is called when the last Cursor provided to onLoadFinished()
	    // above is about to be closed.  We need to make sure we are no
	    // longer using it.
		adapter.swapCursor(null);
	}
}
