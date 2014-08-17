package com.josenaves.gplus.app.contentprovider;

import android.provider.BaseColumns;

public final class FriendsContract {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "gplus.db";

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";

	private FriendsContract() {
	}

	public static abstract class Friends implements BaseColumns {
		protected static final String TABLE_NAME = "friends";

		public static final String COLUMN_NAME_ID = _ID;
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_IMAGE = "imageUrl";

		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + " (" 
				+ COLUMN_NAME_ID + " INTEGER PRIMARY KEY,"
				+ COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP 
				+ COLUMN_NAME_IMAGE + TEXT_TYPE + " )";

		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
}
