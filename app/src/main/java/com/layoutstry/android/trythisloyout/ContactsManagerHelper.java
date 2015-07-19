package com.layoutstry.android.trythisloyout;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
A useful set of APIs is available in the SQLiteOpenHelper class.
When you use this class to obtain references to your database,
the system performs the potentially long-running operations of creating and updating the database only when needed and not during app startup.
All you need to do is call getWritableDatabase() or getReadableDatabase()
*/

/*
To use SQLiteOpenHelper, create a subclass that overrides the onCreate(), onUpgrade() and onOpen() callback methods.
You may also want to implement onDowngrade(), but it's not required.
*/

public class ContactsManagerHelper extends SQLiteOpenHelper {



    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ContactsManager.db";

    public ContactsManagerHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ContactsManagerContract.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(ContactsManagerContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void deleteDB(SQLiteDatabase db){
        db.execSQL(ContactsManagerContract.SQL_DELETE_ENTRIES);
        db.execSQL(ContactsManagerContract.SQL_CREATE_ENTRIES);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}