package com.josenaves.gplus.app;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;

public class FriendsActivity extends ActionBarActivity 
	implements LoaderManager.LoaderCallbacks<Cursor> {
	
	  // Identifies a particular Loader being used in this component
    private static final int LOADER = 66;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		
        // Initializes the CursorLoader. 
		LoaderManager loaderManager = getSupportLoaderManager();
		loaderManager.initLoader(LOADER, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		
		String[] projection = {"name", "imageUrl"};
	    /*
	     * Takes action based on the ID of the Loader that's being created
	     
	    switch (loaderId) {
	        case LOADER:
	            // Returns a new CursorLoader
	            return new CursorLoader(
	                        getParent(),   // Parent activity context
	                        "friends",        // Table to query
	                        projection,     // Projection to return
	                        null,            // No selection clause
	                        null,            // No selection arguments
	                        null             // Default sort order
	        );
	        default:
	            // An invalid id was passed in
	            return null;
	    }
	    */
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

	}
}
