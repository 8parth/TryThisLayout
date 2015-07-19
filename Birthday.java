package com.layoutstry.android.trythisloyout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Birthday {
    /**
     * the persons name
     */
    public String personName;
    /**
     * the event
     */
    public Date birthDate;
    public Date annieDate;
    /**
     * id of contact that belongs to this birthday
     */
    public String contactId;
    /**
     * contact photo
     */
    public Bitmap photo;
    /* indicate if this Birthday has a year */
    public boolean hasYear;

    Context _c;

    /**
     * constructor
     */
    public Birthday(Date birthday, Context c) {
        this._c = c;
        this.birthDate = birthday;
        this.personName = "";
        this.contactId = "";
        this.photo = null;
        this.hasYear = false;
    }

    public Birthday(Date birthday, Date annieday) {
        this.annieDate = annieday;
        this.birthDate = birthday;
        this.personName = "";
        this.contactId = "";
        this.photo = null;
        this.hasYear = false;
    }

    public Birthday(String personName, Date birthday, String contactId, Bitmap photo, Context c) {
        this._c = c;
        this.personName = personName;
        this.birthDate = birthday;
        this.contactId = contactId;
        this.photo = photo;
        this.hasYear = false;
    }
    public Birthday(String personName, Date birthday, Date anniday, String contactId, Bitmap photo) {
        this.personName = personName;
        this.birthDate = birthday;
        this.annieDate = anniday;
        this.contactId = contactId;
        this.photo = photo;
        this.hasYear = false;
    }

    /**
     * return current age of person
     */
    public int getPersonAge() {
        Calendar now = new GregorianCalendar();
        Calendar bday = new GregorianCalendar();
        bday.setTime(birthDate);

                        
                        /* get amount of years between birthday and today */
        int years = now.get(Calendar.YEAR) - bday.get(Calendar.YEAR);
                        
                        /* is this years birthday yet to come? */
        if (
                now.get(Calendar.MONTH) < bday.get(Calendar.MONTH) ||
                        (
                                (now.get(Calendar.MONTH) == bday.get(Calendar.MONTH)) &&
                                        (now.get(Calendar.DAY_OF_MONTH) < bday.get(Calendar.DAY_OF_MONTH))
                        )
                ) {
            years--;
        }

                        /* birthday today? */
        if (now.get(Calendar.MONTH) == bday.get(Calendar.MONTH) &&
                now.get(Calendar.DAY_OF_MONTH) == bday.get(Calendar.DAY_OF_MONTH)) {
            years--;
        }

        if (years < 0) {
            //Log.e(TAG, "Illegal age: " + years + " Using: age = 0");
            return 0;
        }

        return years + 1;
    }

    /**
     * return amount of days until birthday
     *
     * @result amount of days until birthday (negative if past)
     */
    public int getDaysUntil() {
                        /* calendar for current time */
        Calendar now = new GregorianCalendar();
                        
                        /* calendar for birthday */
        Calendar b = new GregorianCalendar();
        b.setTime(birthDate);

                        /* set calendar to current year */
        b.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));

        return b.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR);

    }


    /**
     * return amount of days left until birthday
     *
     * @result amount of days until next birthday (always in the future)
     */
    public int getDaysUntilFuture() {
        int days_left = getDaysUntil();
                        
                        /* if birthday is already past, calculate amount of days
                           until birthday next year */
        if (days_left < 0) {
            days_left +=
                    Calendar.getInstance()
                            .getActualMaximum(Calendar.DAY_OF_YEAR);
        }

        return days_left;
    }

    /**
     * return "upcoming" string
     */
    public String getMessage() {
                        /* amount of days until birthday */
        int in_days = getDaysUntilFuture();

                        /* get age */
        int age = getPersonAge();


        Resources res = _c.getResources();

                        
                        
                        /* special treatment for newborns */
        if (in_days == 0 && age == 0) {
            //.findResourceById();
            String msg = res.getString(R.string.born_today);
            return msg;
        }
                        
                        /* build "in ... days" string */
        String days;
        switch (in_days) {
                                /* today */
            case 0: {
                days = res.getString(R.string.days_zero);
                break;
            }

                                /* tomorrow */
            case 1: {
                days = res.getString(R.string.days_one);
                break;
            }

            default: {
                days = String.format(res.getString(R.string.days_more), in_days);
            }

        }

                        /* generate different messages whether the birthday
                         * has the year of birth set or not */
        String msg;
        if (hasYear) {
								/* build message with age */
            msg = String.format(
                    res.getString(R.string.upcoming),
                    age, days);
        } else {
								/* build message without age */
            msg = String.format(
                    res.getString(R.string.upcoming_no_age),
                    days);
        }

        return msg;
    }


    /* get special leap-year message if necessary */
    public String getLeapYearMessage() {
                        /* create temporary calendar for some calculations */
        GregorianCalendar tmp = new GregorianCalendar();

                        /* is current year a leap year? */
        boolean is_leap_year = tmp.isLeapYear(tmp.get(Calendar.YEAR));

                        /* set calendar to birthday */
        tmp.setTime(birthDate);
                        
                        /* is birthday on 29 of February and 
                           current year no leap-year? */
        if (tmp.get(Calendar.MONTH) == Calendar.FEBRUARY &&
                tmp.get(Calendar.DAY_OF_MONTH) == 29 &&
                is_leap_year == false) {
                                /* add no-leap-year message? */
            return "\n" + _c.getResources().getString(R.string.no_leap_year);
        }

        return "";
    }


    /**
     * open contact belonging to this birthday
     */
    /*public void openContact() {
        Intent i;
        Context context ;
        i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(ContactsContract.Contacts.CONTENT_LOOKUP_URI + "/" + contactId));
        context.startActivity(i);
    }*/
}
        
