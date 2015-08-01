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
    Context context;
    ContentResolver cr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        cr = context.getContentResolver();
        setContentView(R.layout.activity_contact_profile);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        String id = i.getStringExtra("contact_id");
        Long x = Long.parseLong(id);
        Toast.makeText(getApplicationContext(), "contact id " + id, Toast.LENGTH_SHORT).show();
        getSpecificContact(x);
//        getSpecificContactPhoto(x);



        InputStream photoStream;
        BufferedInputStream buf;
        photoStream = openDisplayPhoto(x);
        if(photoStream != null) {
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
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void getSpecificContactPhoto(Long id){

        InputStream thumbnailStream;
        BufferedInputStream buf;

        Uri contactPhotoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        thumbnailStream = ContactsContract.Contacts.openContactPhotoInputStream(cr,
                contactPhotoUri, true);
        if(thumbnailStream != null) {
            buf = new BufferedInputStream(thumbnailStream);
            Bitmap my_btmp = BitmapFactory.decodeStream(buf);
            ImageView thumbImage = (ImageView) findViewById(R.id.profile_image);
            thumbImage.setImageBitmap(my_btmp);
            try{
                buf.close();
                thumbnailStream.close();
            }catch (Exception e){

            }
        }
    }
    public void getSpecificContact(Long id) {
        Uri contactDataUri = ContactsContract.Data.CONTENT_URI;
        final String[] projection = {
                ContactsContract.Contacts._ID,
                Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.HONEYCOMB ?
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                        ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.LOOKUP_KEY
        };

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
                /*if (contactCursor.getString(contactCursor.getColumnIndex("mimetype"))
                        .equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                    switch (contactCursor.getInt(contactCursor.getColumnIndex("data2"))) {
                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                            homePhone = contactCursor.getString(contactCursor.getColumnIndex("data1"));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                            mobilePhone = contactCursor.getString(contactCursor.getColumnIndex("data1"));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                            workPhone = contactCursor.getString(contactCursor.getColumnIndex("data1"));
                            break;
                    }

                }*/

        }
        contactCursor.close();
        TextView x = (TextView) findViewById(R.id.profile_name);
        x.setText(contactName);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public InputStream openDisplayPhoto(long contactId) {
        //Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        //Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.PHOTO_URI);


        //using PHOTO_FILE_ID with CONTENT_URI to create uri of the photo
        Uri person =  ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        //for thumbnail, api level 5
        Uri displayPhotoUri = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        //for full size image, api level 14
        //Uri displayPhotoUri = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);

        //
        try {
            AssetFileDescriptor fd =
                    getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            return fd.createInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unbindDrawables(rootView.findViewById(R.id.swiperefresh_home));
        System.gc();
    }
}
