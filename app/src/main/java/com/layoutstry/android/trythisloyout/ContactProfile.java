package com.layoutstry.android.trythisloyout;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;


public class ContactProfile extends ActionBarActivity {
    String contactName;
    String homePhone;
    String mobilePhone;
    String workPhone;
    String number;

    Context context;
    ContentResolver cr;
    ImageView contact_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        context = getApplicationContext();
        cr = context.getContentResolver();

        setContentView(R.layout.activity_contact_profile);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Intent i = getIntent();

        //String id = i.getStringExtra("contact_id");
        String id;
            Bundle b = getIntent().getExtras();
            id = b.getString("contact_id");

        Long x = Long.parseLong(id);

        getSpecificContact(x);
        //getSpecificContactPhoto(x);


        //
        /*Bitmap my_btmp = loadContactPhoto(getContentResolver(),Long.parseLong(id));
        if(my_btmp!=null) {
            System.out.println("my_btmp is not null");
            ImageView profileImage = (ImageView) findViewById(R.id.profile_image);
            profileImage.setImageBitmap(my_btmp);
        }*/
        //
        //
        InputStream photoStream;
        BufferedInputStream buf;
        photoStream = openDisplayPhoto(x);
        if (photoStream != null) {
            buf = new BufferedInputStream(photoStream);
            Bitmap my_btmp = BitmapFactory.decodeStream(buf);
            ImageView profileImage = (ImageView) findViewById(R.id.profile_image);
            profileImage.setImageBitmap(my_btmp);

            try {
                photoStream.close();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //handle calling functionality
        contact_call = (ImageView) findViewById(R.id.profile_call);
        contact_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageCall();
            }
        });
    }

    public void manageCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        if (number != null && !number.equals("")) {
            callIntent.setData(Uri.parse("tel:" + number));
        }
        try {
            startActivity(callIntent);
        } catch (Exception e) {
            System.out.println("call activity not found");
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void getSpecificContactPhoto(Long id) {

        InputStream thumbnailStream;
        BufferedInputStream buf;

        Uri contactPhotoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        thumbnailStream = ContactsContract.Contacts.openContactPhotoInputStream(cr,
                contactPhotoUri, true);
        if (thumbnailStream != null) {
            buf = new BufferedInputStream(thumbnailStream);
            Bitmap my_btmp = BitmapFactory.decodeStream(buf);
            ImageView thumbImage = (ImageView) findViewById(R.id.profile_image);
            thumbImage.setImageBitmap(my_btmp);
            try {
                buf.close();
                thumbnailStream.close();
            } catch (Exception e) {

            }
        }
    }


    public void getSpecificContact(Long id) {
        homePhone = "";
        mobilePhone = "";
        workPhone = "";
        number = "";

        Uri contactDataUri = ContactsContract.Data.CONTENT_URI;
        /*final String[] projection = {
                ContactsContract.Contacts._ID,
                Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.HONEYCOMB ?
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                        ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.LOOKUP_KEY
        };*/

        final String selection = ContactsContract.Data.CONTACT_ID + " = " + id;

        Cursor contactCursor = cr.query(
                contactDataUri,
                null,
                selection,
                null,
                null
        );
        //contactCursor.moveToFirst();
        if (contactCursor.moveToFirst()) {


            contactName = contactCursor.getString(contactCursor
                    .getColumnIndex(ContactsContract.Data.DISPLAY_NAME));

            //Getting phone numbers
            do {
                if (contactCursor.getString(contactCursor.getColumnIndex("mimetype"))
                        .equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {

                    switch (contactCursor.getInt(contactCursor.getColumnIndex("data2"))) {
                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                            homePhone = contactCursor.getString(contactCursor.getColumnIndex("data1"));
                            Toast.makeText(context, "contact id " + id + "has homePhone", Toast.LENGTH_SHORT).show();
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                            mobilePhone = contactCursor.getString(contactCursor.getColumnIndex("data1"));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                            workPhone = contactCursor.getString(contactCursor.getColumnIndex("data1"));
                            break;
                    }
                }
            }while (contactCursor.moveToNext());
        }
        contactCursor.close();
        TextView cname = (TextView) findViewById(R.id.profile_name);
        cname.setText(contactName);

        TextView cnum = (TextView) findViewById(R.id.profile_number);
        if (!homePhone.equals("") && homePhone != null) {
            cnum.setText(homePhone);
            number = homePhone;
        } else if (mobilePhone != null && !mobilePhone.equals("")) {
            cnum.setText(mobilePhone);
            number = mobilePhone;
        } else if (workPhone != null && !workPhone.equals("")) {
            cnum.setText(workPhone);
            number = workPhone;
        } else {
            cnum.setText("No number found!");
            contact_call = (ImageView) findViewById(R.id.profile_call);
            contact_call.setVisibility(View.INVISIBLE);
            contact_call.setClickable(false);
        }

    }


/*

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(getParentActivityIntent());
        return super.onSupportNavigateUp();
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    //lastly used till 15_08_2015
    public InputStream openDisplayPhoto(long contactId) {
        //Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        //Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.PHOTO_URI);


        //using PHOTO_FILE_ID with CONTENT_URI to create uri of the photo
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri;
        if (Build.VERSION.SDK_INT >= 14) {
            //for full size image, api level 14
            displayPhotoUri = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        } else {
            //for thumbnail, api level 5
            //displayPhotoUri = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            displayPhotoUri = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        }
        //
        try {
            AssetFileDescriptor fd =
                    getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            return fd.createInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    ///new and suppported by api 5
    /*public static Bitmap loadContactPhoto(ContentResolver cr, long id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input == null) {
            return null;
            //return getBitmapFromURL("http://thinkandroid.wordpress.com");
        }
        return BitmapFactory.decodeStream(input);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawables(findViewById(R.id.contact_profile_layout));
        System.gc();
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
