package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.TextUtils;

import java.util.LinkedHashMap;

public abstract class BaseTextViewHolder extends BaseViewHolder<Field> {
    public BaseTextViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
    }

    protected void saveValues(final Field field) {
        String result = getResult();
        if (!field.isNumericField() || !TextUtils.isEmpty(result)) {
            itemValues.addItem(field.getName(), result);
            return;
        }
        if (TextUtils.isEmpty(result)){
            itemValues.removeItem(field.getName());
        }
    }

    protected void initValueViewStatus() {
        if (!((RecordActivity) context).getCurrentFeature().isEditMode()) {
            getValueView().setEnabled(false);
            getValueView().setTextColor(context.getResources().getColor(R.color.gray));
        } else {
            getValueView().setEnabled(true);
            getValueView().setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    protected void initValueViewData(Field field) {
        getValueView().setText(getTranslatedValue(field));

        if (fieldValueVerifyResult != null) {
            LinkedHashMap<String, String> fieldsValueVerifyResultMap = fieldValueVerifyResult
                    .getChildrenAsLinkedHashMap(field.getSectionName().get(PrimeroAppConfiguration.getServerLocale
                            ()));
            if (fieldsValueVerifyResultMap != null) {
                String fieldVerifyResult = fieldsValueVerifyResultMap.get(field.getDisplayName().get
                        (PrimeroAppConfiguration.getServerLocale()));
                if (!TextUtils.isEmpty(fieldVerifyResult)) {
                    getValueView().setError(fieldVerifyResult);
                }
            }
        }
    }

    protected abstract String getResult();

    @Override
    public void setFieldClickable(boolean clickable) {

    }

    protected abstract TextView getValueView();
}
