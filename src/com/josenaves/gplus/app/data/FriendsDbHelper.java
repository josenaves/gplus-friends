package com.josenaves.gplus.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.josenaves.gplus.app.data.FriendsContract.FriendsEntry;

public class FriendsDbHelper extends SQLiteOpenHelper {
    
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "friends.db";

    public FriendsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		final String SQL_CREATE_FRIENDS_TABLE = 
				"CREATE TABLE " + FriendsEntry.TABLE_NAME + " (" 
				+ FriendsEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ FriendsEntry.COLUMN_NAME_GID + " TEXT, "
				+ FriendsEntry.COLUMN_NAME_NAME + " TEXT, "
				+ FriendsEntry.COLUMN_NAME_IMAGE +  " TEXT )";
		
        db.execSQL(SQL_CREATE_FRIENDS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FriendsEntry.TABLE_NAME);
        onCreate(db);
	}
	
}
