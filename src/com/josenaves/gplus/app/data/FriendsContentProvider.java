package com.josenaves.gplus.app.data;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.josenaves.gplus.app.data.FriendsContract.FriendsEntry;

public class FriendsContentProvider extends ContentProvider {

	private static final String LOG_TAG = FriendsContentProvider.class.getSimpleName();
	
	// used for the UriMacher
	private static final int FRIEND = 10;
	private static final int FRIEND_ID = 11;

	// The URI Matcher used by this content provider.
	private static final UriMatcher URIMatcher = buildUriMatcher();

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = FriendsContract.CONTENT_AUTHORITY;
		
		// for each type of URI, here is a corresponding code
		matcher.addURI(authority, FriendsContract.PATH_FRIEND, FRIEND);
		matcher.addURI(authority, FriendsContract.PATH_FRIEND + "/#", FRIEND_ID);
		
		return matcher;
	}
	
	private FriendsDbHelper openHelper;

	@Override
	public boolean onCreate() {
		openHelper = new FriendsDbHelper(getContext());
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (URIMatcher.match(uri)) {
		case FRIEND_ID:
			return FriendsEntry.CONTENT_ITEM_TYPE;
			
		case FRIEND:
			return FriendsEntry.CONTENT_DIR_TYPE;
			
		default:
			throw new UnsupportedOperationException("Unknown URI: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		Cursor cursor;

		// check if the caller has requested a column which does not exists
		checkColumns(projection);

		switch (URIMatcher.match(uri)) {
			// "friend"
			case FRIEND:
				cursor = null;
				break;
				
			// "friend/#"
			case FRIEND_ID:
				cursor = openHelper.getReadableDatabase().query(FriendsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder); 
				break;
				
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		// make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}



	@Override
	public Uri insert(Uri uri, ContentValues values) {

		Uri retUri;
		
		switch (URIMatcher.match(uri)) {
		case FRIEND_ID:
			long id = openHelper.getWritableDatabase().insert(FriendsEntry.TABLE_NAME, null, values);
			if (id > 0) {
				retUri = FriendsEntry.buildFriendUri(id);
			}
			else {
				throw new SQLException("Failed to insert row into " + uri);
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		// notify all listeners
		getContext().getContentResolver().notifyChange(uri, null);
		
		return retUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		int rowsDeleted = 0; 
		
		switch (URIMatcher.match(uri)) {
			case FRIEND:
				rowsDeleted = openHelper.getWritableDatabase().delete(FriendsEntry.TABLE_NAME, selection, selectionArgs);
				break;
				
			default:	
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		// notify all listeners (if anything had changed)
		if (null == selection || 0 != rowsDeleted) getContext().getContentResolver().notifyChange(uri, null);
		
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int rowsUpdated = 0; 
		
		switch (URIMatcher.match(uri)) {
			case FRIEND:
				rowsUpdated = openHelper.getWritableDatabase().update(FriendsEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
				
			default:	
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		// notify all listeners (if anything had changed)
		if (0 != rowsUpdated) getContext().getContentResolver().notifyChange(uri, null);
		
		return rowsUpdated;
	}

	private void checkColumns(String[] projection) {
		
		String[] available = { FriendsEntry.COLUMN_NAME_NAME, FriendsEntry.COLUMN_NAME_IMAGE };
		
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			
			// check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
	
	@Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
		final SQLiteDatabase db = openHelper.getWritableDatabase();
		
		switch (URIMatcher.match(uri)) {
			case FRIEND:
				db.beginTransaction();
				int rowCount = 0;
				try {
					for (ContentValues value : values) {
						long id = db.insert(FriendsEntry.TABLE_NAME, null, value);
						if (id != -1) {
							rowCount++;
						}
					}
					db.setTransactionSuccessful();
				}
				catch (Exception e) {
					Log.e(LOG_TAG, "Error bulk inserting " + values);
				}
				finally {
					db.endTransaction();
				}
				
				// make sure that potential listeners are getting notified
				getContext().getContentResolver().notifyChange(uri, null);

				return rowCount;
			
			default:	
				return super.bulkInsert(uri, values);
		}

    }

	
}
