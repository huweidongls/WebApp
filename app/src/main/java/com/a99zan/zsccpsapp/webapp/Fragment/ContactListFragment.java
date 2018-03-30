package com.a99zan.zsccpsapp.webapp.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a99zan.zsccpsapp.webapp.R;

/**
 * Created by 99zan on 2018/3/5.
 */

public class ContactListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_list_fragment, null);
        return view;
    }
}
