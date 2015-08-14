package com.layoutstry.android.trythisloyout;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class SettingsFragment extends Fragment {
    public View v;
    public Boolean isBirthday = false;
    //SimpleCursorAdapter mAdapter;
    MatrixCursor mMatrixCursor;

    String displayName;
    String homePhone;
    String mobilePhone;
    String workPhone;
    String photoPath;
    byte[] photoByte;
    String birthdate;
    String anniversary;
    String other;
    Activity activity;
    Context context;
    //File tmpFile;
    private ProgressBar spinner;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    MatrixCursor myDataset;

    public SettingsFragment() {

    }

    public static SettingsFragment newInstance() {
        //SettingsFragment fragment = new SettingsFragment();
        return new SettingsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        v = rootView;

        mRecyclerView = (RecyclerView) activity.findViewById(R.id.lst_contacts);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecyclerContactsAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);


        // The contacts from the contacts content provider is stored in this cursor
        mMatrixCursor = new MatrixCursor(new String[]{"_id", "name", "photo", "details"});
/*
        // Adapter to set the data in listview
        mAdapter = new SimpleCursorAdapter(
                activity,
                R.layout.lv_layout,
                null,
                new String[]{"name", "photo", "details"},
                new int[]{R.id.tv_name, R.id.iv_photo, R.id.tv_details},
                0);

*/
        // Getting reference to the listview
        //ListView lstContacts = (ListView) rootView.findViewById(R.id.lst_contacts);
/*
        // Setting adapter to the listview
        lstContacts.setAdapter(mAdapter);
*/
        // Creating an AsyncTask object to retrieve and load listview with contacts
        ListViewContactsLoader listViewContactsLoader = new ListViewContactsLoader();


        spinner = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        // Starting the AsyncTask process to retrieve and load listview with contacts
        listViewContactsLoader.execute();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView contactsListView = (ListView) v.findViewById(R.id.lst_contacts);


/*
        SwipeDetector swipeDetector = new SwipeDetector();
        contactsListView.setOnTouchListener(swipeDetector);
        SwipeDetector.Action swipeDetectorAction= swipeDetector.getAction();
        if(swipeDetectorAction == SwipeDetector.Action.LR){
            Toast.makeText(context, "Swiped Right", Toast.LENGTH_SHORT).show();
        }
        if(swipeDetectorAction == SwipeDetector.Action.RL){
            Toast.makeText(context, "Swiped Left", Toast.LENGTH_SHORT).show();
        }

*/

        /*contactsListView.setOnTouchListener(new View.OnTouchListener() {
                                                @Override
                                                public boolean onTouch(View v, MotionEvent event) {

                                                    return true;
                                                }
                                            }
        );*/
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
        ((MainActivity) activity).onSectionAttached(2);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbindDrawables(v.findViewById(R.id.settings_fragment));
        System.gc();
    }

    private class ListViewContactsLoader extends AsyncTask<Void, Void, Cursor> {
        protected Cursor doInBackground(Void... Params) {
            Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;


            // Querying the table ContactsContract.Contacts to retrieve all the contacts
            Cursor contactsCursor = activity.getContentResolver().query(
                    contactsUri,
                    null,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1"
                    ,
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
                    photoPath = "" + R.drawable.ic_launcher;
                    photoByte = null;
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
                                            birthdate = dateFormat.format(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(birthdate));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY:
                                        anniversary = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        try {
                                            anniversary = dateFormat.format(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(anniversary));
                                        } catch (Exception e) {
                                            e.printStackTrace();
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

                            //Getting photo
/*
                            try {
                                InputStream thumbnailStream;
                                BufferedInputStream buf;

                                thumbnailStream = ContactsContract.Contacts.openContactPhotoInputStream(activity.getContentResolver(),
                                        dataUri);
                                buf = new BufferedInputStream(thumbnailStream);
                                Bitmap my_btmp = BitmapFactory.decodeStream(buf);
                                ImageView thumbImage = (ImageView) activity.findViewById(R.id.iv_photo);
                                thumbImage.setImageBitmap(my_btmp);
                                thumbnailStream.close();
                                buf.close();
                            } catch (IOException e) {

                            } finally {


                            }
*/
                            //Getting Photo
                            /*
                            if (dataCursor.getString(dataCursor.getColumnIndex("mimetype"))
                                    .equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {

                                photoByte = dataCursor.getBlob(dataCursor.getColumnIndex("data15"));

                                if (photoByte != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);

                                    //Getting the caching directory
                                    File cacheDirectoty = activity.getBaseContext().getCacheDir();

                                    //Temporary File to store the cache image
                                    tmpFile = new File(cacheDirectoty.getPath() + "/wpta_" + contactId + ".png");

                                    //FileoutputStream to the temporary file
                                    try {
                                        FileOutputStream fOutStream = new FileOutputStream(tmpFile);

                                        // Writing the bitmap to the temporary file as png file
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOutStream);

                                        // Flush the FileOutputStream
                                        fOutStream.flush();

                                        //Close the FileOutputStream
                                        fOutStream.close();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    photoPath = tmpFile.getPath();

                                }
                            }*/
                        } while (dataCursor.moveToNext());
                        dataCursor.close();


                        String details = "";

                        // Concatenating various information to single string
                        if (homePhone != null && !homePhone.equals(""))
                            details = "HomePhone : " + homePhone + "\n";
                        if (mobilePhone != null && !mobilePhone.equals(""))
                            details += "MobilePhone : " + mobilePhone + "\n";
                        if (workPhone != null && !workPhone.equals(""))
                            details += "WorkPhone : " + workPhone + "\n";
/*
                        if (nickName != null && !nickName.equals(""))
                            details += "NickName : " + nickName + "\n";
                        if (homeEmail != null && !homeEmail.equals(""))
                            details += "HomeEmail : " + homeEmail + "\n";
                        if (workEmail != null && !workEmail.equals(""))
                            details += "WorkEmail : " + workEmail + "\n";
                        if (companyName != null && !companyName.equals(""))
                            details += "CompanyName : " + companyName + "\n";
                        if (title != null && !title.equals(""))
                            details += "Title : " + title + "\n";
*/
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

                            //mMatrixCursor.addRow(new Object[]{Long.toString(contactId), displayName, photoPath, details});
                            myDataset.addRow(new Object[]{Long.toString(contactId), displayName, photoPath, details});

                        }

                    }
                } while (contactsCursor.moveToNext());
                mMatrixCursor.close();
                contactsCursor.close();
            }


            return mMatrixCursor;
        }

        @Override
        protected void onPreExecute() {
            spinner.setVisibility(VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Cursor result) {
            // Setting the cursor containing contacts to listview
            spinner.setVisibility(GONE);
            //mAdapter.swapCursor(result);

        }
    }


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