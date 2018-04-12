package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.cache.GlobalLookupCache;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.TextUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class MiniFormProfileViewHolder extends BaseViewHolder<Field> {

    public static final String TAG = MiniFormProfileViewHolder.class.getSimpleName();
    public static final String GENDER_LOOKUP = "lookup-gender";

    @BindView(R.id.id_normal_state)
    TextView idView;

    @BindView(R.id.gender_name)
    TextView genderName;

    @BindView(R.id.age)
    TextView age;

    @BindView(R.id.registration_date)
    TextView registrationDate;

    @BindView(R.id.container_record_list_item)
    RelativeLayout containerRecordListItem;

    @BindView(R.id.container_incident_list_item)
    RelativeLayout containerIncidentListItem;

    public MiniFormProfileViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setValue(Field field) {
        idView.setText(itemValues.getAsString(ItemValuesMap.RecordProfile.ID_NORMAL_STATE));

        // TODO: Should show display text when I18n
        String gender = itemValues.getAsString(RecordService.SEX);
        genderName.setText(TextUtils.isEmpty(gender) ? "---" : GlobalLookupCache.translationValueByLookup(GENDER_LOOKUP, gender));

        registrationDate.setText(itemValues.getAsString(ItemValuesMap.RecordProfile
                .REGISTRATION_DATE));

        String age = extractAge();
        this.age.setText(TextUtils.isEmpty(age) ? "---" : age);
    }


    protected String extractAge() {
        String age;
        if (itemValues.has(RecordService.RELATION_AGE)) {
            age = itemValues.getAsString(RecordService.RELATION_AGE);
        } else {
            age = itemValues.getAsString(RecordService.AGE);
        }
        return age;
    }

    @Override
    public void setOnClickListener(final Field field) {

    }

    @Override
    public void setFieldEditable(boolean editable) {

    }

    @Override
    public void setFieldClickable(boolean clickable) {

    }

    public void disableRecordGenderView() {
        genderName.setVisibility(GONE);
    }
}
