package com.layoutstry.android.trythisloyout;

import android.provider.BaseColumns;

/*
A contract class is a container for constants that define names for URIs, tables, and columns.
The contract class allows you to use the same constants across all the other classes in the same package.
This lets you change a column name in one place and have it propagate throughout your code.

A good way to organize a contract class is to put definitions that are global to your whole database in the root level of the class.
Then create an inner class for each table that enumerates its columns.
*/

/*
By implementing the BaseColumns interface,
your inner class can inherit a primary key field called _ID that some Android classes such as cursor adaptors will expect it to have.
It's not required, but this can help your database work harmoniously with the Android framework.
*/


public class ContactsManagerContract {
    public ContactsManagerContract() {

    }
    /* Inner class that defines the table contents */
    public static abstract class ContactsEntry implements BaseColumns {
        public static final String TABLE_NAME = "contacts_db";
        public static final String COLUMN_NAME_ENTRY_ID = "contacts_id";
        public static final String COLUMN_NAME_NAME = "contacts_name";
        public static final String COLUMN_NAME_NUMBER = "contacts_number";
        public static final String COLUMN_NAME_BIRTHDAY = "contacts_bday";
        public static final String COLUMN_NAME_ANNIE = "contacts_annie";
        public static final String COLUMN_NAME_OTHER = "contacts_other";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String NUM_TYPE = " INT";
    private static final String DATE_TYPE = " DATETIME";
    private static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ContactsEntry.TABLE_NAME + " (" +
                    ContactsEntry._ID + " INTEGER PRIMARY KEY," +
                    ContactsEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_NAME_NUMBER + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_NAME_BIRTHDAY + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_NAME_ANNIE + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_NAME_OTHER + TEXT_TYPE +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ContactsEntry.TABLE_NAME;
}
