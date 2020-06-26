package org.unicef.rapidreg.base.record.recordlist;

import android.widget.CompoundButton;

import java.lang.ref.WeakReference;
import java.util.List;

public class RecordListCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
    private final List<Long> recordList;
    private final List<Long> recordWillBeDeletedList;
    private final int position;
    private final int syncedRecordsCount;
    private final WeakReference<RecordListAdapter.OnViewUpdateListener> onViewUpdateListener;

    public RecordListCheckedChangeListener(
            final List<Long> recordList,
            final List<Long> recordWillBeDeletedList,
            final int position,
            final int syncedRecordsCount,
            final WeakReference<RecordListAdapter.OnViewUpdateListener> onViewUpdateListener) {
        this.recordList = recordList;
        this.recordWillBeDeletedList = recordWillBeDeletedList;
        this.position = position;
        this.onViewUpdateListener = onViewUpdateListener;
        this.syncedRecordsCount = syncedRecordsCount;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Long recordId = recordList.get(position);
        if (isChecked) {
            if (!recordWillBeDeletedList.contains(recordId)) {
                recordWillBeDeletedList.add(recordId);
            }
        } else {
            if (recordWillBeDeletedList.contains(recordId)) {
                recordWillBeDeletedList.remove(recordId);
            }
        }
        RecordListAdapter.OnViewUpdateListener currentOnViewUpdateListener = this.onViewUpdateListener.get();
        if (currentOnViewUpdateListener != null) {
            currentOnViewUpdateListener.onRecordsDeletable(!recordWillBeDeletedList.isEmpty());
            currentOnViewUpdateListener.onSelectedAllButtonCheckable(recordWillBeDeletedList.size() ==
                    syncedRecordsCount);
        }
    }
}
