package com.josenaves.gplus.app.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the friends database.
 * @author josenaves
 */
public final class FriendsContract implements BaseColumns {
	
	public static final String AUTHORITY = "com.josenaves.gplus.friends";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	private FriendsContract() {
	}

	 /* Inner class that defines the table contents of the friends table */
	public static final class FriendsEntry implements BaseColumns {
		
		protected static final String TABLE_NAME = "friends";

		public static final String COLUMN_NAME_ID = _ID;
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_IMAGE = "imageUrl";

		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
}
