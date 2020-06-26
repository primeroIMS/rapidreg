package org.unicef.rapidreg.base.record.recordlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.ViewGroup;

import org.unicef.rapidreg.R;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class RecordListAdapter extends RecyclerView.Adapter<RecordListViewHolder> {
    public static final String TAG = RecordListAdapter.class.getSimpleName();
    private static final int TEXT_AREA_SHOWED_STATE = 0;
    private static final int TEXT_AREA_HIDDEN_STATE = 1;

    protected Context context;
    protected List<Long> recordList = new ArrayList<>();
    protected List<Long> recordWillBeDeletedList = new ArrayList<>();
    protected boolean isDetailShow = true;
    protected boolean isDeleteMode = false;
    protected boolean isSelectAll = false;
    protected OnViewUpdateListener onViewUpdateListener;
    protected int syncedRecordsCount;

    public RecordListAdapter(Context context) {
        this.context = context;
    }

    public void setOnViewUpdateListener(OnViewUpdateListener onViewUpdateListener) {
        this.onViewUpdateListener = onViewUpdateListener;
    }

    public void setRecordList(List<Long> recordList) {
        this.recordList = recordList;
    }

    public void removeRecords() {
        for (Long recordId : recordWillBeDeletedList) {
            int position = recordList.indexOf(recordId);
            recordList.remove(recordId);
            notifyItemRemoved(position);
        }
        recordWillBeDeletedList.clear();
    }

    public List<Long> getRecordWillBeDeletedList() {
        return recordWillBeDeletedList;
    }

    @Override
    public RecordListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup itemView = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_list, parent, false);

        return new RecordListViewHolder(itemView, this.context.getResources(), new WeakReference<>(this.onViewUpdateListener));
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public void toggleViews(boolean isDetailShow) {
        this.isDetailShow = isDetailShow;
        notifyDataSetChanged();
    }

    public void toggleDeleteViews(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode;
        if (isDeleteMode) {
            onViewUpdateListener.onRecordsDeletable(!recordWillBeDeletedList.isEmpty() && !recordList.isEmpty());
        }
        notifyDataSetChanged();
    }

    public void toggleSelectAllItems(boolean isSelectAll, List<Long> willBeSelectedList) {
        this.isSelectAll = isSelectAll;
        if (isSelectAll) {
            for (Long recordId : willBeSelectedList) {
                if (!recordWillBeDeletedList.contains(recordId)) {
                    recordWillBeDeletedList.add(recordId);
                }
            }
        } else {
            recordWillBeDeletedList.clear();
        }
        notifyDataSetChanged();
    }

    protected void toggleTextArea(RecordListViewHolder holder) {
        if (isDetailShow) {
            holder.viewSwitcher.setDisplayedChild(TEXT_AREA_SHOWED_STATE);
        } else {
            holder.viewSwitcher.setDisplayedChild(TEXT_AREA_HIDDEN_STATE);
        }
    }

    protected void toggleDeleteArea(RecordListViewHolder holder, boolean isDeletable) {
        if (isDeleteMode) {
            if (onViewUpdateListener != null) {
                onViewUpdateListener.onRecordsDeletable(!recordWillBeDeletedList.isEmpty());
            }
            holder.toggleDeleteView(isDeletable);
        } else {
            holder.toggleNormalView();
            recordWillBeDeletedList.clear();
        }
    }

    protected void toggleInvalidatedIcon(RecordListViewHolder holder, boolean isInvalidated) {
        holder.toggleInvalidatedIcon(isInvalidated);
    }

    protected void toggleDeleteCheckBox(RecordListViewHolder holder) {
        holder.deleteStateCheckBox.setChecked(recordWillBeDeletedList.contains(holder.deleteStateCheckBox.getTag()));
    }

    protected void toggleNoteAlert(RecordListViewHolder holder, boolean hasAlert) {
        holder.toggleNoteAlert(hasAlert);
    }

    public int calculateRetainedPosition() {
        int retainedPosition = 0;
        if (recordWillBeDeletedList.isEmpty()) {
            return retainedPosition;
        }
        retainedPosition = recordList.indexOf(recordWillBeDeletedList.get(0));
        for (Long recordId : recordWillBeDeletedList) {
            int position = recordList.indexOf(recordId);
            if (retainedPosition > position) {
                retainedPosition = position;
            }
        }
        return --retainedPosition;
    }

    public void setSyncedListCount(int syncedRecordsCount) {
        this.syncedRecordsCount = syncedRecordsCount;
    }

    public interface OnViewUpdateListener {
        void onRecordsDeletable(boolean isDeletable);

        void onSelectedAllButtonCheckable(boolean isChecked);
    }
}
