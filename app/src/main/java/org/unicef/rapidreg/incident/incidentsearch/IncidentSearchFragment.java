package org.unicef.rapidreg.incident.incidentsearch;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchFragment;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.incident.incidentlist.IncidentListAdapter;
import org.unicef.rapidreg.lookups.Option;
import org.unicef.rapidreg.service.cache.GlobalLookupCache;
import org.unicef.rapidreg.widgets.ClearableEditText;
import org.unicef.rapidreg.widgets.dialog.SearchAbleDialog;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.CONSTANT.AGE_FROM;
import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.CONSTANT.AGE_TO;
import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.CONSTANT.ID;
import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.CONSTANT.LOCATION;
import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.CONSTANT.SURVIVOR_CODE;
import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.CONSTANT.TYPE_OF_VIOLENCE;
import static org.unicef.rapidreg.model.RecordModel.EMPTY_AGE;

public class IncidentSearchFragment extends RecordSearchFragment {
    private SearchAbleDialog dialog;

    private List<Option> typeOfViolenceValues;
    private List<Option> locationValues;

    @Inject
    IncidentSearchPresenter incidentSearchPresenter;

    @Inject
    IncidentListAdapter incidentListAdapter;

    @Override
    public RecordSearchPresenter createPresenter() {
        return incidentSearchPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        getComponent().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected Map<String, String> getFilterValues() {
        Map<String, String> searchValues = new LinkedHashMap<>();
        searchValues.put(ID, id.getText());
        searchValues.put(SURVIVOR_CODE, survivorCode.getText());
        searchValues.put(AGE_FROM, ageFrom.getText().isEmpty() ? String.valueOf(EMPTY_AGE) : ageFrom.getText());
        searchValues.put(AGE_TO, ageTo.getText().isEmpty() ? String.valueOf(EMPTY_AGE) : ageTo.getText());
        searchValues.put(TYPE_OF_VIOLENCE, GlobalLookupCache.getSingleSelectedOptions(typeOfViolenceValues, typeOfViolence.getText()).getId());

        searchValues.put(LOCATION, GlobalLookupCache.getSingleSelectedOptions(locationValues, location.getText()).getId());
        return searchValues;
    }

    @Override
    protected void onInitSearchFields() {
        idField.setVisibility(View.VISIBLE);
        survivorCodeField.setVisibility(View.VISIBLE);
        ageField.setVisibility(View.VISIBLE);
        typeOfViolenceField.setVisibility(View.VISIBLE);
        locationField.setVisibility(View.VISIBLE);

        initTypeOfViolenceField();
        initIncidentLocationField();
    }

    @Override
    protected RecordListAdapter createRecordListAdapter() {
        return incidentListAdapter;
    }

    private void initIncidentLocationField() {
        locationValues = incidentSearchPresenter.getIncidentLocationList();
        setMultipleSelectionOnClickListener(location, locationValues, getResources()
                .getString(R.string
                        .location));
    }

    private void initTypeOfViolenceField() {
        typeOfViolenceValues = incidentSearchPresenter.getViolenceTypeList();
        setMultipleSelectionOnClickListener(typeOfViolence, typeOfViolenceValues, getResources().getString(R.string
                .type_of_violence));
    }

    private void setMultipleSelectionOnClickListener(final ClearableEditText target, final List<Option> items, final
    String title) {
        target.setOnClickListener(view -> {
            final String originalValue = target.getText();
            int originalIndex =  GlobalLookupCache.getSelectOptionIndex(items, originalValue);

            dialog = new SearchAbleDialog(IncidentSearchFragment.this.getContext(), title, items, originalIndex);
            dialog.setOnClick(result -> target.setText(resultDisplayName(items, result)));
            dialog.setCancelButton(v -> {
                target.setText(resultDisplayName(items, originalValue));
                dialog.dismiss();
            });
            dialog.setOkButton(v -> dialog.dismiss());
            dialog.show();
        });
    }

    private String resultDisplayName(List<Option> options, String result) {
        return GlobalLookupCache.getSingleSelectedOptions(options, result).getDisplayText();
    }
}
