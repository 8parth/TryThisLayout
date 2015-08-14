package com.layoutstry.android.trythisloyout;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by User on 28-04-2015.
 */

public class MailFragment extends Fragment {
    private View v;
    public static MailFragment newInstance(){
        MailFragment fragment = new MailFragment();
        return fragment;
    }

    public MailFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mail, container, false);
        v = rootView;
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
        View root = v.findViewById(R.id.mail_fragment);
        //Context context = getActivity().getApplicationContext();
        //getActivity().setContentView(new View(context));
        unbindDrawables(root);
        System.gc();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawables(v.findViewById(R.id.mail_fragment));
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup ) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
        }
    }
}
