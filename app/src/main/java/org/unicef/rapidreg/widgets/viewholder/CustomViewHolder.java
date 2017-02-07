package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.event.RedirectIncidentEvent;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class CustomViewHolder extends BaseViewHolder<Field> {
    private static final String TAG = CustomViewHolder.class.getSimpleName();

    @BindView(R.id.custom_form_title_layout)
    LinearLayout customFormTitleLayout;

    @BindView(R.id.custom_form_title)
    TextView customFormTitle;

    @BindView(R.id.custom_form_title_arrow)
    ImageView customFormTitleArrow;

    @BindView(R.id.item_list)
    RecyclerView itemListView;

    private String fieldName;

    public CustomViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setValue(Field field) {
        fieldName = field.getName();
        customFormTitle.setText(field.getDisplayName().get(Locale.getDefault().getLanguage()));

        restoreItemList();
    }

    private void restoreItemList() {
        List<String> childrenArray = itemValues.getAsList(fieldName);
        if (childrenArray == null) {
            return;
        }

        RecyclerView.LayoutManager layout = new LinearLayoutManager(context);
        layout.setAutoMeasureEnabled(true);
        itemListView.setLayoutManager(layout);

        CustomAdapter adapter = new CustomAdapter(childrenArray);
        itemListView.setAdapter(adapter);

        enableContainer();

        customFormTitleLayout.setOnClickListener(v -> {
            if (GONE == itemListView.getVisibility()) {
                enableContainer();
            } else {
                disableContainer();
            }
        });
    }

    private void enableContainer() {
        itemListView.setVisibility(View.VISIBLE);
        customFormTitleArrow.setImageResource(R.drawable.arrow_down_blue);
    }

    private void disableContainer() {
        itemListView.setVisibility(GONE);
        customFormTitleArrow.setImageResource(R.drawable.arrow_up_blue);
    }

    @Override
    public void setOnClickListener(Field field) {}

    @Override
    public void setFieldEditable(boolean editable) {

    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomItemViewHolder> {

        private List<String> values;

        public CustomAdapter(List<String> values) {
            this.values = values;
        }

        @Override
        public CustomItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.form_custom_item, parent, false);
            return new CustomItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CustomItemViewHolder holder, int position) {
            holder.setValue(values.get(position));
        }

        @Override
        public int getItemCount() {
            return values.size();
        }

        class CustomItemViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.custom_item_layout)
            LinearLayout customItemLayout;

            @BindView(R.id.custom_item_content)
            TextView customItemView;

            public CustomItemViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void setValue(String value) {
                customItemView.setText(value);
                customItemLayout.setOnClickListener(v -> {
                    RedirectIncidentEvent redirectIncidentEvent = new RedirectIncidentEvent(value);
                    EventBus.getDefault().postSticky(redirectIncidentEvent);
                });
            }
        }
    }
}
