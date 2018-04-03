package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.lookups.Option;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.widgets.viewholder.GenericViewHolder;

import java.util.List;
import java.util.Locale;

public class SingleSelectDialog extends BaseDialog {

    private String result;
    private List<Option> optionItems;

    SearchAbleDialog dialog;

    public SingleSelectDialog(Context context, Field field,
                              ItemValuesMap itemValues, TextView resultView, ViewSwitcher
                                      viewSwitcher) {
        super(context, field, itemValues, resultView, viewSwitcher);
        result = "";
    }

    @Override
    public void initView() {
        optionItems = field.getSelectOptions();

        int selectIndex = field.getSelectOptionIndex(result);
        result = itemValues.getAsString(field.getName());

        dialog = new SearchAbleDialog(context, field.getDisplayName().get(Locale.getDefault()
                .getLanguage()), optionItems, selectIndex);

        dialog.setOnClick(result -> SingleSelectDialog.this.result = result);

        dialog.setCancelButton(v -> dialog.dismiss());

        dialog.setOkButton(view -> {
            if (getResult() != null && !TextUtils.isEmpty(getResult())) {
                viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_HAS_ANSWER_STATE);
            } else {
                viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_NO_ANSWER_STATE);
            }
            resultView.setText(getResult() == null ? null : getDisplayText());

            itemValues.addItem(field.getName(), getResult());

            dialog.dismiss();
        });

    }

    @Override
    public void show() {
        initView();

        dialog.show();
        changeDialogDividerColor(context, dialog);
    }

    @Override
    protected String getDisplayText() {
        return result == null ? null : field.getSingleSelectedOptions(result);
    }


    @Override
    public String getResult() {
        return result;
    }
}
