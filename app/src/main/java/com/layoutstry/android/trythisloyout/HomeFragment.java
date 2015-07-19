package com.layoutstry.android.trythisloyout;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class HomeFragment extends Fragment {
    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public View v;
    public Boolean isBirthday = false;
    SimpleCursorAdapter myAdapter;
    MatrixCursor myMatrixCursor;
    String displayName;
    String homePhone;
    String mobilePhone;
    String workPhone;
    /*
    String photoPath;
    byte[] photoByte;
    */
    String birthdate;
    String anniversary;
    String other;
    Activity activity;
    Context context;
    ContactsManagerHelper contactsManagerHelper;
    SQLiteDatabase db;
    private ProgressBar home_spinner;
    View rootView;
    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = activity.getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        v = rootView;
        // The contacts from the contacts content provider is stored in this cursor
        myMatrixCursor = new MatrixCursor(new String[]{
                "_id",
                "name",
                //"photo",
                "details"});

        // Adapter to set the data in listview
        myAdapter = new SimpleCursorAdapter(
                activity,
                R.layout.home_lv_layout,
                null,
                new String[]{
                        "name",
                        //"photo",
                        "details"
                },
                new int[]{
                        R.id.home_tv_name,
                        //R.id.iv_photo,
                        R.id.home_tv_details},
                0);


        // Getting reference to the listview
        ListView lstContacts = (ListView) rootView.findViewById(R.id.home_contacts_list);

        // Setting adapter to the listview
        lstContacts.setAdapter(myAdapter);

        // Creating an AsyncTask object to retrieve and load listview with contacts
        ListViewContactsLoader listViewContactsLoader = new ListViewContactsLoader();

        //To access your database, instantiate your subclass of SQLiteOpenHelper
        contactsManagerHelper = new ContactsManagerHelper(context);
        if (isDBExist(ContactsManagerContract.ContactsEntry.TABLE_NAME)) {
            Toast.makeText(context, ContactsManagerContract.ContactsEntry.TABLE_NAME + " already exists!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, ContactsManagerContract.ContactsEntry.TABLE_NAME + " does not exist!", Toast.LENGTH_SHORT).show();
        }

        home_spinner = (ProgressBar) rootView.findViewById(R.id.home_progressbar);
        // Starting the AsyncTask process to retrieve and load listview with contacts
        listViewContactsLoader.execute();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView contactsListView = (ListView) v.findViewById(R.id.home_contacts_list);

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        Toast.makeText(context, "Not yet done", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
        );
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(1);
    }

    private boolean isDBExist(String name) {
        return context.getDatabasePath(name).exists();
    }

    private class ListViewContactsLoader extends AsyncTask<Void, Void, Cursor> {
        protected Cursor doInBackground(Void... Params) {
            Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;
            db = contactsManagerHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            // Querying the table ContactsContract.Contacts to retrieve all the contacts
            Cursor contactsCursor = activity.getContentResolver().query(
                    contactsUri,
                    null,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
                    null,
                    ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY + " COLLATE LOCALIZED ASC");

            if (contactsCursor.moveToFirst()) {
                do {
                    long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID"));

                    Uri dataUri = ContactsContract.Data.CONTENT_URI;

                    String[] projection1 = {
                            ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DATA2,
                            ContactsContract.Contacts.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.DATA1,
                            ContactsContract.CommonDataKinds.Phone.DATA15
                            //ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
                    };

                    // Querying the table ContactsContract.Data to retrieve individual items like
                    // home phone, mobile phone, work email etc corresponding to each contact

                    String selection =
                            ContactsContract.Data.CONTACT_ID + "=" + contactId;

                    Cursor dataCursor = activity.getContentResolver().query(
                            dataUri,
                            projection1,   //projection
                            selection,   //selection
                            null,
                            null);


                    displayName = "";
                    homePhone = "";
                    mobilePhone = "";
                    workPhone = "";
                    /*
                    photoPath = "" + R.drawable.ic_launcher;
                    photoByte = null;
                    */
                    birthdate = "";
                    anniversary = "";
                    other = "";


                    if (dataCursor.moveToFirst()) {
                        // Getting Display name
                        displayName = dataCursor.getString(dataCursor
                                .getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                        isBirthday = false;

                        do {
                            //Getting BDAY
                            if (dataCursor.getString(dataCursor.getColumnIndex("mimetype"))
                                    .equals(ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)) {
                                isBirthday = true;
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

                                switch (dataCursor.getInt(dataCursor.getColumnIndex("data2"))) {
                                    case ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY:
                                        birthdate = dataCursor.getString(dataCursor.getColumnIndex("data1"));

                                        try {
                                            birthdate = dateFormat
                                                    .format(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(birthdate));
                                        } catch (Exception e) {
                                            try {
                                                birthdate = dateFormat
                                                        .format(new SimpleDateFormat("-MM-dd", Locale.getDefault()).parse(birthdate));
                                            } catch (Exception e2) {
                                                e.printStackTrace();
                                            }
                                        }
                                        break;
                                    case ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY:
                                        anniversary = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        try {
                                            anniversary = dateFormat
                                                    .format(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(anniversary));
                                        } catch (Exception e) {
                                            try {
                                                anniversary = dateFormat
                                                        .format(new SimpleDateFormat("-MM-dd", Locale.getDefault()).parse(anniversary));
                                            } catch (Exception e2) {
                                                e.printStackTrace();
                                            }
                                        }
                                        break;
                                    case ContactsContract.CommonDataKinds.Event.TYPE_OTHER:
                                        other = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                }

                            }


                            //Getting phone numbers
                            if (dataCursor.getString(dataCursor.getColumnIndex("mimetype"))
                                    .equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) && isBirthday) {
                                switch (dataCursor.getInt(dataCursor.getColumnIndex("data2"))) {
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                        homePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                        mobilePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                        workPhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                }
                            }


                        } while (dataCursor.moveToNext());
                        dataCursor.close();


                        String details = "";

                        Boolean isPhone = false;
                        // Concatenating various information to single string
                        if (homePhone != null && !homePhone.equals("")) {
                            details = "HomePhone : " + homePhone + "\n";
                            isPhone = true;
                           /* if (isPhone) {
                            }*/
                        }
                        if (mobilePhone != null && !mobilePhone.equals("")) {
                            details += "MobilePhone : " + mobilePhone + "\n";
                            /*if (!isPhone) {
                                values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_NUMBER, mobilePhone);
                                isPhone = true;
                            }*/
                        }
                        if (workPhone != null && !workPhone.equals("")) {
                            details += "WorkPhone : " + workPhone + "\n";

                            //in db only two number of support is added. workPhone has to be implemented

                            /*if (!isPhone) {
                                if (isPhone) {
                                    values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_NUMBER, workPhone);
                                }
                            }*/
                        }
                        if (birthdate != null && !birthdate.equals("")) {
                            details += "Birthdate : " + birthdate + "\n";

                        }
                        if (anniversary != null && !anniversary.equals("")) {
                            details += "anniversary : " + anniversary + "\n";

                        }
                        if (other != null && !other.equals("")) {
                            details += "other date: " + other + "\n";
                        }

                        // Adding id, display name, path to photo and other details to cursor
                        if (isBirthday) {

                            values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_ENTRY_ID, contactId);
                            values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_NAME, displayName);

                            values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_NUMBER, homePhone);
                            values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_BIRTHDAY, birthdate);
                            if (!isPhone) {
                                values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_NUMBER, mobilePhone);
                                isPhone = true;
                            }
                            values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_ANNIE, anniversary);
                            values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_OTHER, other);

                            db.insert(
                                    ContactsManagerContract.ContactsEntry.TABLE_NAME,
                                    null,
                                    values
                            );

                            myMatrixCursor.addRow(new Object[]{
                                    Long.toString(contactId),
                                    displayName,
                                    //photoPath,
                                    details});
                        }

                    }
                } while (contactsCursor.moveToNext());
                myMatrixCursor.close();
                contactsCursor.close();
            }


            return myMatrixCursor;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            home_spinner.setVisibility(VISIBLE);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            // Setting the cursor containing contacts to listview
            home_spinner.setVisibility(GONE);
            myAdapter.swapCursor(result);

        }
    }

    private class DBContactsLoader extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            db = contactsManagerHelper.getReadableDatabase();
            String sortOrder = ContactsManagerContract.ContactsEntry.COLUMN_NAME_NAME + " ASC";
            Cursor cursor = db.query(
                    ContactsManagerContract.ContactsEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            cursor.moveToFirst();
            do {
                long Id = cursor.getLong(
                        cursor.getColumnIndexOrThrow(ContactsManagerContract.ContactsEntry._ID)
                );
                String name = cursor.getString(cursor
                        .getColumnIndexOrThrow(ContactsManagerContract.ContactsEntry.COLUMN_NAME_NAME));
                String bday = cursor.getString(cursor
                        .getColumnIndexOrThrow(ContactsManagerContract.ContactsEntry.COLUMN_NAME_BIRTHDAY));
                String annie = cursor.getString(cursor
                        .getColumnIndexOrThrow(ContactsManagerContract.ContactsEntry.COLUMN_NAME_ANNIE));
                //String det = "Birthdate : " + bday + "\n" +"Annieversary: " +annie;
                myMatrixCursor.addRow(new Object[]{
                        Long.toString(Id),
                        name,
                        //photoPath,
                        "Birthdate : " + bday + "\n" + "Annieversary: " + annie});
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
            return myMatrixCursor;
        }

        @Override
        protected void onPreExecute() {
            home_spinner.setVisibility(VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Cursor result) {
            // Setting the cursor containing contacts to listview
            home_spinner.setVisibility(GONE);
            myAdapter.swapCursor(result);

        }
    }
}