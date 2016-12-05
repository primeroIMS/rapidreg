package org.unicef.rapidreg.tracing.tracingsearch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchFragment;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.tracing.tracinglist.TracingListAdapter;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class TracingSearchFragment extends RecordSearchFragment {

    @Inject
    TracingSearchPresenter tracingSearchPresenter;

    @Inject
    TracingListAdapter tracingListAdapter;

    @Override
    @NonNull
    public RecordSearchPresenter createPresenter() {
        return tracingSearchPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onInitViewContent() {
        super.onInitViewContent();
        caregiver.setVisibility(View.GONE);
        caregiverSeparator.setVisibility(View.GONE);

        registrationDate.setHint(R.string.inquiry_date);
    }

    @Override
    protected RecordListAdapter createRecordListAdapter() {
        return tracingListAdapter;
    }

    @Override
    protected List<Long> getSearchResult(Map<String, String> filters) {
        String id = filters.get(ID);
        String name = filters.get(NAME);
        String from = filters.get(AGE_FROM);
        int ageFrom = TextUtils.isEmpty(from) ? RecordModel.MIN_AGE : Integer.valueOf(from);
        String to = filters.get(AGE_TO);
        int ageTo = TextUtils.isEmpty(to) ? RecordModel.MAX_AGE : Integer.valueOf(to);
        String registrationDate = filters.get(REGISTRATION_DATE);

        return tracingSearchPresenter.getSearchResult(id, name, ageFrom, ageTo, registrationDate);
    }
}
