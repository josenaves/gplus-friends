package com.josenaves.gplus.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the friends database.
 * @author josenaves
 */
public final class FriendsContract implements BaseColumns {
	
	// The "content authority" is the name to use for all content provider uri's
	// It's similar a domain name and a website. 
	public static final String CONTENT_AUTHORITY = "com.josenaves.gplus.friends";
	
	// Now this is base for for all URIs which app will use
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
	
	// PATH
	public static final String PATH_FRIEND = "friend";
	
	// FRIEND (dir)
	// content://com.josenaves.gplus.friends/friend
	
	// FRIEND_ID
	// friend (item)
	// content://com.josenaves.gplus.friends/friend/[id]
	

	private FriendsContract() {
	}

	 /* Inner class that defines the table contents of the friends table */
	public static final class FriendsEntry implements BaseColumns {
		
		protected static final String TABLE_NAME = "friends";
		
		// base location for friends table
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_FRIEND).build();
		
		// special mime-types that indicate that URI return directory (list of item) or a single item
		public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_FRIEND;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_FRIEND;
		
		public static final String COLUMN_NAME_ID = _ID;
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_IMAGE = "imageUrl";

		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
		
		// This will return a URI like -> content://com.josenaves.gplus.friends/friend/[id]
		public static Uri buildFriendUri(long id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}
}
