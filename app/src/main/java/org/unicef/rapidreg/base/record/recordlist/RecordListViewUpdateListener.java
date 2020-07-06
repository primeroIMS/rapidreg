package org.unicef.rapidreg.base.record.recordlist;

import android.util.Log;
import android.widget.Button;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;

import java.lang.ref.WeakReference;

public class RecordListViewUpdateListener implements  RecordListAdapter.OnViewUpdateListener{
    private final WeakReference<RecordActivity> weakRecordActivity;
    private final Button listItemDeleteBtn;

    public RecordListViewUpdateListener(
            final WeakReference<RecordActivity> weakRecordActivity,
            final Button listItemDeleteBtn) {
        this.weakRecordActivity = weakRecordActivity;
        this.listItemDeleteBtn = listItemDeleteBtn;
    }

    @Override
    public void onRecordsDeletable(boolean isDeletable) {
        this.listItemDeleteBtn.setEnabled(isDeletable);
        this.listItemDeleteBtn.setBackgroundResource(isDeletable ? R.color.red_a200 : R.color.gray);
    }

    @Override
    public void onSelectedAllButtonCheckable(boolean isChecked) {
        RecordActivity recordActivity = this.weakRecordActivity.get();

        if(recordActivity != null) {
            this.weakRecordActivity.get().toggleSelectAllButtonState(isChecked);
            this.weakRecordActivity.get().setSelectAll(isChecked);
            Log.e("RecordListViewListener", "onSelectedAllButtonCheckable: isSelectAll - > " + isChecked);
        }
    }
}
