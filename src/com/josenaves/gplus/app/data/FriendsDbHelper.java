package com.josenaves.gplus.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.plus.model.people.Person;
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
				+ FriendsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ FriendsEntry.COLUMN_NAME_NAME + " TEXT, "
				+ FriendsEntry.COLUMN_NAME_IMAGE +  " TEXT )";
		
        db.execSQL(SQL_CREATE_FRIENDS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FriendsEntry.TABLE_NAME);
        onCreate(db);
	}

	public long insert(Person person) {
		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(FriendsEntry._ID, person.getId());
		values.put(FriendsEntry.COLUMN_NAME_NAME, person.getDisplayName());
		values.put(FriendsEntry.COLUMN_NAME_IMAGE, person.getImage().getUrl());

		// Insert the new row, returning the primary key value of the new row
		long newRowId = db.insert(FriendsEntry.TABLE_NAME, null, values);
		
		return newRowId;
	}
	
	public Cursor list() {
		SQLiteDatabase db = getReadableDatabase();

		// Define a projection that specifies which columns from the databases
		// you will actually use after this query.
		String[] projection = {FriendsEntry.COLUMN_NAME_NAME, FriendsEntry.COLUMN_NAME_IMAGE};

		// How you want the results sorted in the resulting Cursor
		String sortOrder = FriendsEntry.COLUMN_NAME_NAME + " DESC";

		Cursor cursor = db.query(FriendsEntry.TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    null,                                // The columns for the WHERE clause
		    null,                            // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    sortOrder                                 // The sort order
		    );
		 	
		return cursor;
	}
	
}
