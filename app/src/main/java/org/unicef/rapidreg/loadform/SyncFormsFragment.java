package org.unicef.rapidreg.loadform;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;

public class SyncFormsFragment extends MvpFragment<SyncFormsView, SyncFormsPresenter> {


    public SyncFormsFragment() {
        // Required empty public constructor
    }

    @Override
    public SyncFormsPresenter createPresenter() {
        return null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.load_forms, container, false);
    }

}
