package com.layoutstry.android.trythisloyout;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.layoutstry.android.trythisloyout.ContactsManagerContract.ContactsEntry.TABLE_NAME;

/**
 * onHandleIntent() callback method must be overridden
 * Notice that the other callbacks of a regular Service component,
 * such as onStartCommand() are automatically invoked by IntentService.
 * In an IntentService, you should avoid overriding these callbacks.
 * <p/>
 * <p/>
 * An IntentService also needs an entry in your application manifest.
 * Provide this entry as a <service> element that's a child of the <application> element
 */
public class WisherManagerService extends IntentService {
    //private static boolean isAlreadyNotified;
    private static int notifyId;
    public WisherManagerService() {
        super("service");
    }

    public WisherManagerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent)throws NullPointerException{
        // Gets data from the incoming intent
        //String dataString = workIntent.getDataString();
        SQLiteDatabase home_db;
        //HomeFragment homeFragment = new HomeFragment();
        /*MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "_id",
                "name",
                //"photo",
                "details"});
        */
        Calendar currentDate = Calendar.getInstance(); // Get the current date
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd MMMM yyyy", Locale.getDefault()); // format it as per your requirement
        String toDay = formatter.format(currentDate.getTime());

        if (getApplicationContext().getDatabasePath(ContactsManagerHelper.DATABASE_NAME).exists()) {
            notifyId = 1;
            ContactsManagerHelper managerHelper = new ContactsManagerHelper(getApplicationContext());
            home_db = managerHelper.getReadableDatabase();
            String sort_order = "UPPER(" + ContactsManagerContract.ContactsEntry.COLUMN_NAME_NAME + ") ASC";


            Cursor c = home_db.query(
                    TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    sort_order
            );

            if (c.moveToFirst()) {
                do {
                    long Id = c.getLong(
                            c.getColumnIndexOrThrow(ContactsManagerContract.ContactsEntry.COLUMN_NAME_ENTRY_ID)
                    );
                    String name = c.getString(c
                            .getColumnIndexOrThrow(ContactsManagerContract.ContactsEntry.COLUMN_NAME_NAME));
                    String bday = c.getString(c
                            .getColumnIndexOrThrow(ContactsManagerContract.ContactsEntry.COLUMN_NAME_BIRTHDAY));

                    bday = formatThisDateStripOffYears(bday);
                    toDay = formatThisDateStripOffYears(toDay);

                    if (bday.equalsIgnoreCase(toDay)) {

                        notifyUserAboutBirthday(name, Id);
                        notifyId++;
                    }
                } while (c.moveToNext());
            }
            c.close();
            home_db.close();
        }

        WisherManagerReceiver.completeWakefulIntent(intent);
    }

    private String formatThisDateStripOffYears(String birthdate) {

        Locale locale = Locale.getDefault();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", locale);
        String temp;
        try {
            temp = dateFormat
                    .format(new SimpleDateFormat("dd MMMM yyyy", locale).parse(birthdate));
        } catch (ParseException e) {

            e.getMessage();
            return birthdate;
        }
        //return null;
        return temp;
    }

    private void notifyUserAboutBirthday(String name, Long id) {


        Bundle b = new Bundle();
        //Long id = getContactId2(name, number);


        b.putString("contact_id", id.toString());
        Intent resultIntent = new Intent(this, ContactProfile.class);
        resultIntent.putExtras(b);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //Adds the back stack
        stackBuilder.addParentStack(ContactProfile.class);
        //Adds the intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        //Gets a PendingIntent containing entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifyBday = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.bday_cake)
                .setSound(soundUri)
                .setContentTitle("Wish " + name + " for birthday ;)")
                .setContentText("P.S. have an amazing day. ")
                .setAutoCancel(true);
        //.setExtras(b);

        notifyBday.setContentIntent(resultPendingIntent);

                    /*matrixCursor.addRow(new Object[]{
                            Long.toString(Id),
                            name,
                            //photoPath,
                            "Birthdate : " + bday + "\n" + "Annieversary: " + annie});
                    */


        NotificationManager nm =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(notifyId, notifyBday.build());
        //isAlreadyNotified = true;
    }


}
