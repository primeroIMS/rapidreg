package org.unicef.rapidreg.base.record.recordlist;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.content.res.Resources;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.cache.GlobalLookupCache;
import org.unicef.rapidreg.utils.TextUtils;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.id_normal_state)
    public TextView idNormalState;

    @BindView(R.id.incident_id_normal_state)
    public TextView incidentIdNormalState;

    @BindView(R.id.id_on_hidden_state)
    public TextView idHiddenState;

    @BindView(R.id.gender_name)
    public TextView genderName;

    @BindView(R.id.age)
    public TextView age;

    @BindView(R.id.incident_age)
    public TextView incidentAge;

    @BindView(R.id.registration_date)
    public TextView registrationDate;

    @BindView(R.id.incident_registration_date)
    public TextView incidentRegistrationDate;

    @BindView(R.id.incident_location)
    public TextView incidentLocation;

    @BindView(R.id.record_image)
    public ImageView image;

    @BindView(R.id.view_switcher)
    public ViewSwitcher viewSwitcher;

    @BindView(R.id.item_delete_checkbox_content)
    public LinearLayout itemDeleteCheckboxContent;

    @BindView(R.id.delete_state)
    public CheckBox deleteStateCheckBox;

    @BindView(R.id.container_record_list_item)
    public RelativeLayout containerRecordListItem;

    @BindView(R.id.container_incident_list_item)
    public RelativeLayout containerIncidentListItem;

    @BindView(R.id.invalidated)
    public ImageButton invalidated;

    @BindView(R.id.note_alert)
    public ImageView noteAlert;

    private DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

    private RecordModel record;

    private final Resources resources;
    private final WeakReference<RecordListAdapter.OnViewUpdateListener> onViewUpdateListener;

    public RecordListViewHolder(
            final View itemView,
            final Resources resources,
            final WeakReference<RecordListAdapter.OnViewUpdateListener> onViewUpdateListener) {
        super(itemView);
        this.resources = resources;
        this.onViewUpdateListener = onViewUpdateListener;
        ButterKnife.bind(this, itemView);
    }

    public void setValues(final String gender,
                          final String shortUUID,
                          final String ageContent,
                          final RecordModel record,
                          final List<Long> recordList,
                          final List<Long> recordWillBeDeletedList,
                          final int syncedRecordsCount) {
        this.record = record;
        int position = getAdapterPosition();
        deleteStateCheckBox.setTag(recordList.get(position));

        final String GENDER_LOOKUP = "lookup-gender";
        final String LOCATION_LOOKUP = "Location";


        Glide
                .with(image.getContext())
                .load(record)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(this.resources.getDrawable(R.drawable.avatar_placeholder))
                .into(image);

        idNormalState.setText(shortUUID);
        idHiddenState.setText(shortUUID);

        genderName.setText(TextUtils.isEmpty(gender) ? "---" :
                GlobalLookupCache.translationValueByLookup(GENDER_LOOKUP, gender));

        age.setText(isValidAge(ageContent) ? ageContent : "---");

        Date registrationDate = record.getRegistrationDate();
        String registrationDateText = isValidDate(registrationDate) ? dateFormat.format(registrationDate) :
                "---";
        this.registrationDate.setText(registrationDateText);

        deleteStateCheckBox.setOnCheckedChangeListener(null);
        deleteStateCheckBox.setChecked(recordWillBeDeletedList.contains(deleteStateCheckBox.getTag()));
        deleteStateCheckBox.setOnCheckedChangeListener(
                new RecordListCheckedChangeListener(
                        recordList,
                        recordWillBeDeletedList,
                        position,
                        syncedRecordsCount,
                        onViewUpdateListener
                )
        );

        if (record instanceof Incident) {
            containerRecordListItem.setVisibility(View.GONE);
            containerIncidentListItem.setVisibility(View.VISIBLE);
            String locationText = TextUtils.truncateByDoubleColons(((Incident) record).getLocation(),
                    PrimeroAppConfiguration.getCurrentSystemSettings().getDistrictLevel());
            incidentLocation.setText(TextUtils.isEmpty(locationText) ? "---" :
                    GlobalLookupCache.translationValueByLookup(LOCATION_LOOKUP, locationText));
            incidentIdNormalState.setText(shortUUID);
            incidentAge.setText(isValidAge(ageContent) ? ageContent : "---");
            incidentRegistrationDate.setText(registrationDateText);
        }
    }

    public RecordModel getRecord() {
        return record;
    }

    public void setViewOnClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }

    public void setViewOnLongClickListener(View.OnLongClickListener listener) {
        itemView.setOnLongClickListener(listener);
    }

    public void disableRecordImageView() {
        image.setVisibility(View.GONE);
    }

    public void disableRecordAgeView() {
        age.setVisibility(View.GONE);
    }

    public void disableRecordGenderView() {
        genderName.setVisibility(View.GONE);
    }

    public void toggleDeleteView(boolean isDeletable) {
        itemDeleteCheckboxContent.setVisibility(View.VISIBLE);
        deleteStateCheckBox.setEnabled(isDeletable);
        itemView.setEnabled(isDeletable);

        itemView.setOnClickListener(view -> deleteStateCheckBox.toggle());
        itemView.setBackgroundColor(isDeletable ? Color.WHITE : Color.LTGRAY);
    }

    public void toggleNormalView() {
        deleteStateCheckBox.setChecked(false);
        itemDeleteCheckboxContent.setVisibility(View.GONE);

        itemView.setEnabled(true);
        itemView.setBackgroundColor(Color.WHITE);
    }

    public void toggleInvalidatedIcon(boolean isInvalidated) {
        if (isInvalidated) {
            this.invalidated.setVisibility(View.VISIBLE);
        } else {
            this.invalidated.setVisibility(View.GONE);
        }

    }

    public void toggleNoteAlert(boolean showAlert) {
        if (showAlert) {
            this.noteAlert.setVisibility(View.VISIBLE);
        } else {
            this.noteAlert.setVisibility(View.INVISIBLE);
        }
    }

    protected boolean isValidAge(String value) {
        if (value == null || "".equals(value.trim())) {
            return false;
        }
        return Double.valueOf(value).intValue() >= 0;
    }

    protected boolean isValidDate(Date date) {
        if (date == null) {
            return false;
        }
        return true;
    }
}
