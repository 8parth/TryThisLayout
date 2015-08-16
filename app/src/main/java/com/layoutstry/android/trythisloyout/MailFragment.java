package com.layoutstry.android.trythisloyout;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by parth modi on 28-04-2015.
 * about page
 */

public class MailFragment extends Fragment {
    private View v;

    public static MailFragment newInstance() {
        MailFragment fragment = new MailFragment();
        return fragment;
    }

    public MailFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mail, container, false);
        v = rootView;
        TextView aboutApp = (TextView) v.findViewById(R.id.about_app_id);
        aboutApp.setText(Html.fromHtml("<p>This is an open - source android project.<p>" +
                "<p>You can report any issues or request features on " +
                "<a href=\"https://toknowtoshare.wordpress.com/2015/08/16/wisher-an-app-which-will-never-let-you-forget-birthdays-ever\">Wisher</a></p>"));
        Linkify.addLinks(aboutApp, Linkify.ALL);
        aboutApp.setMovementMethod(LinkMovementMethod.getInstance());
/*
        TextView licenceIcon = (TextView) v.findViewById(R.id.licence_icons8_id);
        licenceIcon.setText(Html.fromHtml("<p>Icons credit: <a href=\"https://icons8.com/android-L\">Free icons by Icons8</a></p>"));
        licenceIcon.setVisibility(View.VISIBLE);
        Linkify.addLinks(licenceIcon, Linkify.ALL);
        licenceIcon.setMovementMethod(LinkMovementMethod.getInstance());*/
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(3);
    }

    @Override
    public void onStop() {
        super.onStop();
        /*View root = v.findViewById(R.id.mail_fragment);
        //Context context = getActivity().getApplicationContext();
        //getActivity().setContentView(new View(context));
        unbindDrawables(root);
        System.gc();
*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawables(v.findViewById(R.id.mail_fragment_scrollview));
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
        }
    }
}
