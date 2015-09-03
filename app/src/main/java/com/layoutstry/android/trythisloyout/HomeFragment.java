package com.layoutstry.android.trythisloyout;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.view.View.INVISIBLE;
import static com.layoutstry.android.trythisloyout.ContactsManagerContract.ContactsEntry.COLUMN_NAME_OTHER;
import static com.layoutstry.android.trythisloyout.ContactsManagerContract.ContactsEntry.TABLE_NAME;


public class HomeFragment extends Fragment {
    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public View v;
    public Boolean isBirthday = false;
    SimpleCursorAdapter myAdapter, myAdapterRefresh;
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
    boolean bool_adapter;
    MatrixCursor myMatrixCursorRefresh;
    ListView lstContacts;
    static SwipeRefreshLayout mySwipeRefreshLayout;

    boolean isContactsWithBirthdaysFound;

    public static final String PACKAGENAME_ACTION = "com.layoutstry.android.trythisloyout.ACTION";

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
        setHasOptionsMenu(true);

        isContactsWithBirthdaysFound = false;


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        v = rootView;
        bool_adapter = true;

        /*//Enable swipeRefresh
        v.findViewById(R.id.swiperefresh_home).setVisibility(View.VISIBLE);
        v.findViewById(R.id.tv_no_contacts).setVisibility(View.INVISIBLE);*/
        // SWIPE TO REFRESH FEATURE
        mySwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh_home);
        //if(mySwipeRefreshLayout.getVisibility() != VISIBLE){
            enableListView(mySwipeRefreshLayout);
        //}

        // The contacts from the contacts content provider is stored in this cursor
        myMatrixCursor = new MatrixCursor(new String[]{
                "_id",
                "contact_id",
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

        lstContacts = (ListView) v.findViewById(R.id.home_contacts_list);

        // Setting adapter to the listview
        lstContacts.setAdapter(myAdapter);

        //To access your database, instantiate your subclass of SQLiteOpenHelper
        //contactsManagerHelper = new ContactsManagerHelper(context);
        if (!isDBExist(ContactsManagerHelper.DATABASE_NAME)) {


            // Creating an AsyncTask object to retrieve and load listview with contacts
            ListViewContactsLoader listViewContactsLoader = new ListViewContactsLoader();

            // Starting the AsyncTask process to retrieve and load listview with contacts
            listViewContactsLoader.execute();

        } else {

            DBContactsLoader contactsLoader = new DBContactsLoader();
            //home_spinner = (ProgressBar) rootView.findViewById(R.id.home_progressbar);
            contactsLoader.execute();
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView contactsListView = (ListView) v.findViewById(R.id.home_contacts_list);

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        Cursor c = (Cursor) parent.getItemAtPosition(position);
                                                        Bundle b = new Bundle();
                                                        Long cid = c.getLong(1);
                                                        b.putString("contact_id", cid.toString());
                                                        //try {

                                                        //} catch (Exception e){
                                                        //    System.out.println("Column not found");
                                                        //}

                                                        //String name = c.getString(1);
                                                        Intent i = new Intent(activity, ContactProfile.class);
                                                        i.putExtras(b);
                                                        //i.putExtra("contact_id", cid.toString());


                                                        startActivity(i);

                                                    }
                                                }
        );


        //mySwipeRefreshLayout = new SwipeRefreshLayout(context);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        //if (updateDB()) {
                        updateDB();
                        //Toast.makeText(context, "DB updated in swipe", Toast.LENGTH_SHORT).show();

                        //}
                    }
                }
        );

        //service
        //setting alarm inside a method
        if(mySwipeRefreshLayout.getVisibility() == View.VISIBLE){
            //setAlarmWisher();
            enableReceiver(context);
            if(!WisherAlarmSetterReceiver.isScheduled){
                WisherAlarmSetterReceiver.scheduleWisherAlarm(context);
            }
        }

    }

    private void enableListView(SwipeRefreshLayout sr){
        //disable textView layout
        v.findViewById(R.id.tv_no_contacts).setVisibility(View.INVISIBLE);
        //enable swipeRefresh layout
        sr.setVisibility(View.VISIBLE);

    }

    private void disableListView(SwipeRefreshLayout sr){
        //disable SwipeRefresh Layout
        sr.setVisibility(INVISIBLE);
        //enable TextView Layout
        v.findViewById(R.id.tv_no_contacts).setVisibility(View.VISIBLE);
    }
    private void setAlarmWisher(){
        PendingIntent pendingIntent;
        Intent wishIntent = new Intent(activity, WisherManagerReceiver.class);
        wishIntent.setAction(PACKAGENAME_ACTION);
        pendingIntent = PendingIntent.getBroadcast(context, 0, wishIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 1);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);                         //this cancels previous alarms
        //used setInexactRepeating() method
        /*
        When you use this method, Android synchronizes
        multiple inexact repeating alarms and fires them at the same time.
        This reduces the drain on the battery.
        */

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,   //type of alarm
                calendar.getTimeInMillis(),                         //setting time
                AlarmManager.INTERVAL_DAY,                          //interval ex. when alarm has to be repeated
                pendingIntent);                                     //intent to be fired

        //Enabling the receiver programatically
        //WisherManagerReceiver.class
        //enableReceiver(context, "WisherManagerReceiver.class");
        enableReceiver(context);

    }





    public void enableReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, WisherAlarmSetterReceiver.class);
        ComponentName receiver2 = new ComponentName(context, WisherManagerReceiver.class);
        PackageManager pm = context.getPackageManager();


        pm.setComponentEnabledSetting(receiver2,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        //TO DISABLE AN ALARM
        //IN SITUATIONS LIKE WHEN USER DISABLES THE ALARM USE FOLLOWING METHOD
        /*
        ComponentName receiver = new ComponentName(context, SampleBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        */


    }

    public boolean isDBExist(String name) {

        return context.getDatabasePath(name).exists();
    }

    private boolean updateDB() {
        db = contactsManagerHelper.getWritableDatabase();
        //closing all connections to the database
        contactsManagerHelper.close();
        db.close();

        //deleting database
        context.deleteDatabase("ContactsManager.db");

        ////

        myMatrixCursorRefresh = new MatrixCursor(new String[]{
                "_id",
                "contact_id",
                "name",
                //"photo",
                "details"});
        myAdapter = null;
        myAdapterRefresh = new SimpleCursorAdapter(
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

        lstContacts.setAdapter(myAdapterRefresh);
        bool_adapter = false;
        isContactsWithBirthdaysFound = false;
        // Creating an AsyncTask object to retrieve and load listview with contacts
        ListViewContactsLoader listViewContactsLoader = new ListViewContactsLoader();
        //home_spinner = (ProgressBar) rootView.findViewById(R.id.home_progressbar);
        // Starting the AsyncTask process to retrieve and load listview with contacts
        listViewContactsLoader.execute();
        mySwipeRefreshLayout.setRefreshing(false);

        return true;
    }


    private class ListViewContactsLoader extends AsyncTask<Void, Void, Cursor> {
        final String[] inputFormats = {
                "yyyy-MM-dd",
                "-MM-dd",
                "yyyyMMdd",
                "ddMM"
        };

        protected Cursor doInBackground(Void... Params) {
            Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;
            contactsManagerHelper = new ContactsManagerHelper(context);
            db = contactsManagerHelper.getWritableDatabase();
            ContentValues values = new ContentValues();


            // Querying the table ContactsContract.Contacts to retrieve all the contacts
            Cursor contactsCursor = activity.getContentResolver().query(
                    contactsUri,
                    null,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
                    null,
                    "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") COLLATE LOCALIZED ASC");

            if (contactsCursor.moveToFirst()) {
                do {
                    long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID"));

                    Uri dataUri = ContactsContract.Data.CONTENT_URI;

                    /*String[] projection1 = {
                            ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DATA2,
                            ContactsContract.Contacts.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.DATA1,
                            ContactsContract.CommonDataKinds.Phone.DATA15
                            //ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
                    };
*/
                    // Querying the table ContactsContract.Data to retrieve individual items like
                    // home phone, mobile phone, work email etc corresponding to each contact
                    //ContactsContract.Data.CONTENT_TYPE
                    String selection =
                            ContactsContract.Data.CONTACT_ID + "=" + contactId
                                    + " AND "
                                    + ContactsContract.Data.MIMETYPE + "= '"
                                    + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' AND "
                                    + ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
                    //+" AND " +ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE +"=" +ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;

                    Cursor dataCursor = activity.getContentResolver().query(
                            dataUri,
                            null,   //projection
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

                    if (dataCursor.getCount() > 0) {

                        dataCursor.moveToFirst();
                        // Getting Display name
                        displayName = dataCursor.getString(dataCursor
                                .getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                        isBirthday = false;


                        do {
                            //Getting BDAY
                            if (dataCursor.getString(dataCursor.getColumnIndex("mimetype"))
                                    .equals(ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)) {

                                isBirthday = true;
                                isContactsWithBirthdaysFound = true;

                                switch (dataCursor.getInt(dataCursor.getColumnIndex("data2"))) {
                                    case ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY:
                                        birthdate = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        birthdate = formatThisDate(birthdate);
                                        break;

                                    case ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY:
                                        anniversary = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        anniversary = formatThisDate(anniversary);
                                        break;

                                    case ContactsContract.CommonDataKinds.Event.TYPE_OTHER:
                                        other = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        other = formatThisDate(other);
                                        break;
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
                        }
                        String dateDetails = null;
                        if (birthdate != null && !birthdate.equals("")) {
                            details += "Birthdate : " + birthdate + "\n";
                            dateDetails = "Birthdate : " + birthdate + "\n";
                        }
                        if (anniversary != null && !anniversary.equals("")) {
                            details += "anniversary : " + anniversary + "\n";
                            dateDetails += "anniversary : " + anniversary;
                        } else {
                            dateDetails += "\n";
                        }

                        if (other != null && !other.equals("")) {
                            details += "other date: " + other + "\n";
                        }

                        // Adding id, display name, path to photo and other details to cursor
                        //if (isBirthday) {

                        values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_ENTRY_ID, contactId);
                        values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_NAME, displayName);

                        values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_NUMBER, homePhone);
                        values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_BIRTHDAY, birthdate);
                        if (!isPhone) {
                            values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_NUMBER, mobilePhone);
                            isPhone = true;
                        }
                        values.put(ContactsManagerContract.ContactsEntry.COLUMN_NAME_ANNIE, anniversary);
                        values.put(COLUMN_NAME_OTHER, other);

                        db.insert(
                                TABLE_NAME,
                                COLUMN_NAME_OTHER,
                                values
                        );


                        if (!bool_adapter) {
                            myMatrixCursorRefresh.addRow(new Object[]{
                                    Long.toString(contactId),
                                    contactId,
                                    displayName,
                                    dateDetails
                            });

                            //bool_adapter = true;
                        } else {

                            myMatrixCursor.addRow(new Object[]{
                                    Long.toString(contactId),
                                    contactId,
                                    displayName,
                                    //photoPath,
                                    dateDetails});
                        }
                    } else { // else for if(dataCursor.getCount()>0)

                        /*if (!bool_adapter) {
                            myMatrixCursorRefresh.addRow(new Object[]{
                                    Long.toString(-1),
                                    -1,
                                    "No contacts with birthdays found !",
                                    null
                            });

                            //bool_adapter = true;
                        } else {

                            myMatrixCursor.addRow(new Object[]{
                                    Long.toString(-1),
                                    -1,
                                    "No contacts with birthdays found !",
                                    //photoPath,
                                    null});

                        }*/

                            /*
                            TextView tv = (TextView) v.findViewById(R.id.no_contacts_found);
                            tv.setVisibility(VISIBLE);
                            */


                    }        // big else closed


                    dataCursor.close();

//                    }
                } while (contactsCursor.moveToNext());
                contactsCursor.close();
                db.close();
                if (!isContactsWithBirthdaysFound) {
                    /*if (!bool_adapter) {
                        myMatrixCursorRefresh.addRow(new Object[]{
                                Long.toString(-1),
                                -1,
                                "No contacts with birthdays Found!",
                                null
                        });

                        //bool_adapter = true;
                    } else {

                        myMatrixCursor.addRow(new Object[]{
                                Long.toString(-1),
                                -1,
                                "No contacts with birthdays Found!",
                                null
                        });
                    }*/


                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            if(mySwipeRefreshLayout.getVisibility() != View.INVISIBLE) {
                                disableListView(mySwipeRefreshLayout);
                            }

                        }
                    });


                }

            }

            if (!bool_adapter) {
                //myAdapterRefresh.notifyDataSetChanged(); //caused error

                return myMatrixCursorRefresh;
            } else {

                //myAdapter.notifyDataSetChanged(); //caused error
                return myMatrixCursor;
            }
        }

        private String formatThisDate(String birthdate) {

            Locale locale = Locale.getDefault();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", locale);


            for (int i = 0; i < inputFormats.length; i++) {
                try {
                    return dateFormat
                            .format(new SimpleDateFormat(inputFormats[i], locale).parse(birthdate));
                } catch (ParseException e) {
                    e.getMessage();

                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    if (mySwipeRefreshLayout.getVisibility() != View.VISIBLE) {
                        enableListView(mySwipeRefreshLayout);
                    }
                }
            });

            home_spinner = (ProgressBar) rootView.findViewById(R.id.home_progressbar);
            home_spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            //myAdapter.notifyDataSetChanged();
            // Setting the cursor containing contacts to listview
            home_spinner.setVisibility(View.INVISIBLE);

            if (!bool_adapter) {

                myMatrixCursorRefresh.close();
                myAdapterRefresh.swapCursor(result);

            } else {

                myMatrixCursor.close();
                myAdapter.swapCursor(result);
            }

            bool_adapter = true;
        }
    }

    private class DBContactsLoader extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            contactsManagerHelper = new ContactsManagerHelper(context);
            db = contactsManagerHelper.getReadableDatabase();
            String sortOrder = "UPPER(" + ContactsManagerContract.ContactsEntry.COLUMN_NAME_NAME + ") ASC";
            Cursor cursor = db.query(
                    TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );
            if (cursor.getCount() > 0) {
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
                    String details = null;
                    if (annie != null && !annie.equals("")) {
                        details = "Birthdate : " + bday + "\n" + "Annieversary: " + annie;
                    } else {
                        details = "Birthdate : " + bday + "\n" + "\n";
                    }
                    Long contact_id = cursor.getLong(cursor
                            .getColumnIndexOrThrow(ContactsManagerContract.ContactsEntry.COLUMN_NAME_ENTRY_ID));
                    myMatrixCursor.addRow(new Object[]{
                            Long.toString(Id),
                            contact_id,
                            name,
                            //photoPath,
                            details});
                } while (cursor.moveToNext());
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        if(mySwipeRefreshLayout.getVisibility() != View.INVISIBLE){
                            disableListView(mySwipeRefreshLayout);
                        }
                    }
                });
            }
            cursor.close();
            db.close();

            return myMatrixCursor;
        }

        @Override
        protected void onPreExecute() {
            //home_spinner.setVisibility(VISIBLE);
            //mySwipeRefreshLayout.setRefreshing(true);
            if(mySwipeRefreshLayout.getVisibility() != View.VISIBLE){
                enableListView(mySwipeRefreshLayout);
            }

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Cursor result) {

            // Setting the cursor containing contacts to listview
            //home_spinner.setVisibility(GONE);
            //mySwipeRefreshLayout.setRefreshing(false);
            //myAdapter.notifyDataSetChanged();
            myAdapter.swapCursor(result);

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(1);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.homefragment_refresh, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh:

                return updateDB();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*@Override
    public void onDestroyView() {
        super.onDestroyView();
        unbindDrawables(rootView);
        System.gc();
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        //activity.setContentView(null);
        unbindDrawables(rootView);
        System.gc();
    }

    //this method is from stackoverflow
    //http://stackoverflow.com/questions/1147172/what-android-tools-and-methods-work-best-to-find-memory-resource-leaks
    //thanks to hp.android
    /*
    if (view instanceof ViewGroup && !(view instanceof AdapterView)) this will get rid of exception which you are getting for Adapter
    */
    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            if (!(view instanceof AdapterView)) {
                ((ViewGroup) view).removeAllViews();
            }


        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            imageView.setImageBitmap(null);
        }
    }
}