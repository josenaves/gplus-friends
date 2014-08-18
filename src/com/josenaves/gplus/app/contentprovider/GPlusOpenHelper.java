package com.josenaves.gplus.app.contentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.plus.model.people.Person;

public class GPlusOpenHelper extends SQLiteOpenHelper {
    
    public GPlusOpenHelper(Context context) {
        super(context, FriendsContract.DATABASE_NAME, null,FriendsContract.DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public long insert(Person person) {
		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(FriendsContract.Friends._ID, person.getId());
		values.put(FriendsContract.Friends.COLUMN_NAME_NAME, person.getDisplayName());
		values.put(FriendsContract.Friends.COLUMN_NAME_IMAGE, person.getImage().getUrl());

		// Insert the new row, returning the primary key value of the new row
		long newRowId = db.insert(FriendsContract.Friends.TABLE_NAME, null, values);
		
		return newRowId;
	}
	
	public Cursor list() {
		SQLiteDatabase db = getReadableDatabase();

		// Define a projection that specifies which columns from the databases
		// you will actually use after this query.
		String[] projection = {FriendsContract.Friends.COLUMN_NAME_NAME, FriendsContract.Friends.COLUMN_NAME_IMAGE};

		// How you want the results sorted in the resulting Cursor
		String sortOrder = FriendsContract.Friends.COLUMN_NAME_NAME + " DESC";

		Cursor cursor = db.query(FriendsContract.Friends.TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    null,                                // The columns for the WHERE clause
		    null,                            // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    sortOrder                                 // The sort order
		    );
		 	
		return cursor;
	}
	
	public void recreateDB() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(FriendsContract.Friends.DELETE_TABLE);
		createTable();
	}
	
	public void createTable() {
		SQLiteDatabase db = getWritableDatabase();
        db.execSQL(FriendsContract.Friends.CREATE_TABLE);
	}
	
}
