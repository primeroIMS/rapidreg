package org.unicef.rapidreg.base.record.recordlist;

import android.view.View;
import android.widget.AdapterView;

import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerState;

import java.util.List;

public class RecordItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private final RecordListPresenter presenter;
    private final RecordListAdapter adapter;
    private final SpinnerState[] spinnerStates;

    public RecordItemSelectedListener(
            final RecordListPresenter presenter,
            final RecordListAdapter adapter,
            final SpinnerState[] spinnerStates){
        this.presenter = presenter;
        this.adapter = adapter;
        this.spinnerStates = spinnerStates;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        List<Long> filterRecords = this.presenter.getRecordsByFilter(this.spinnerStates[position]);
        if (filterRecords == null || filterRecords.isEmpty()) {
            return;
        }
        this.adapter.setRecordList(filterRecords);
        this.adapter.setSyncedListCount(this.presenter.getSyncedRecordsCount());
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
