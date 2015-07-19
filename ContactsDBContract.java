package com.layoutstry.android.trythisloyout;

import android.provider.BaseColumns;

/**
 * Created by User on 06-06-2015.
 */
public final class ContactsDBContract {
    public ContactsDBContract(){ }

    public static abstract class ContactsEntry implements BaseColumns{
        public static final String TABLE_NAME = "BdayContacts";
        public static final String COLUMN_NAME_ID = "entryid";
        public static final String COLUMN_NAME_NAME = "entryname";
        public static final String COLUMN_NAME_BDAY = "entrybday";
        public static final String COLUMN_NAME_ANNIE = "entryannie";
        public static final String COLUMN_NAME_OTHER = "entryother";
        public static final String COLUMN_NAME_NUM = "entrynum";
    }
    //Process of creating database using sqlite helper
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ContactsEntry.TABLE_NAME + " (" +
                    ContactsEntry._ID + " INTEGER PRIMARY KEY," +
                    ContactsEntry.COLUMN_NAME_ID + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_NAME_BDAY + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_NAME_ANNIE + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_NAME_OTHER + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_NAME_NUM + TEXT_TYPE + COMMA_SEP +
            " )";
    //Process of deleting database using sqlite helper
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ContactsEntry.TABLE_NAME;


}
