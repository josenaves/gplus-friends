package com.josenaves.gplus.app.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class FriendsContentProvider extends ContentProvider {

	// used for the UriMacher
	private static final int FRIENDS = 10;
	private static final int FRIEND_ID = 20;

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE 	+ "/friends";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/friend";

	private static final String BASE_PATH = "friends";

	private static final UriMatcher URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static {
		URIMatcher.addURI(FriendsContract.AUTHORITY, BASE_PATH, FRIENDS);
		URIMatcher.addURI(FriendsContract.AUTHORITY, BASE_PATH + "/#", FRIEND_ID);
	}

	private GPlusOpenHelper openHelper;

	@Override
	public boolean onCreate() {
		openHelper = new GPlusOpenHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		// Using SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(FriendsContract.Friends.TABLE_NAME);

		int uriType = URIMatcher.match(uri);
		
		switch (uriType) {
			case FRIENDS:
				break;
				
			case FRIEND_ID:
				// adding the ID to the original query
				queryBuilder.appendWhere(FriendsContract.Friends.COLUMN_NAME_ID + "=" + uri.getLastPathSegment());
				break;
				
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		
		// make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch (URIMatcher.match(uri)) {
		case FRIEND_ID:
			return CONTENT_ITEM_TYPE;
			
		case FRIENDS:
			return CONTENT_TYPE;
			
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);

		}

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		int uriType = URIMatcher.match(uri);
		SQLiteDatabase sqlDB = openHelper.getWritableDatabase();
		long id = 0;

		switch (uriType) {
		case FRIEND_ID:
			id = sqlDB.insert(FriendsContract.Friends.TABLE_NAME, null, values);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	private void checkColumns(String[] projection) {
		
		String[] available = { FriendsContract.Friends.COLUMN_NAME_NAME, FriendsContract.Friends.COLUMN_NAME_IMAGE };
		
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			
			// check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
}
