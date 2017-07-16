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
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.event.Event;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.event.RedirectIncidentEvent;
import static org.unicef.rapidreg.service.cache.ItemValuesMap.RecordProfile.INCIDENT_LINKS;
import org.unicef.rapidreg.model.Incident;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindColor;
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
        setEditableBackgroundStyle(isEditable(field));

        fieldName = field.getName();
        customFormTitle.setText(field.getDisplayName().get(PrimeroAppConfiguration.getDefaultLanguage()));

        restoreItemList(true);
    }

    private void restoreItemList(boolean clickable) {
        // obtain remote records
        // TODO in the case of incident links they are not being downloaded from server
        // as soon as Incidents created in Cases start to be downloaded from server they should show up in incident_links field
        List<String> childrenArray = itemValues.getAsList(fieldName);
        if (childrenArray == null) {
            return;
        }

        LinkedHashMap<String, Event> events = new LinkedHashMap<String, Event>();

        // look for local stored incident links
        if (fieldName.equals(INCIDENT_LINKS)) {
            String caseId = itemValues.getAsString(CaseService.CASE_ID);
            // TODO Inject CaseService
            if (caseId != null) {
                List<String> incidentList = CaseService.getInstance().getIncidentsByCaseId(caseId);
                if (incidentList != null) {
                    for (String uniqueId : incidentList) {
                        if (!events.containsKey(uniqueId)) {
                            events.put(uniqueId, new RedirectIncidentEvent(uniqueId));
                        }
                    }
                }
            }
        }

        for (String internalId : childrenArray) {
            if (clickable) {
                if (fieldName.equals(INCIDENT_LINKS)) {
                    // everything that comes from server carries internalIds, but we print uniqueIds
                    // ensure that the Incident was downloaded
                    Incident incident = new IncidentService().getByInternalId(internalId);
                    if (incident != null) {
                        events.put(incident.getUniqueId(), new RedirectIncidentEvent(incident.getUniqueId()));
                    }
                }
            } else {
                events.put(internalId, null);
            }
        }

        RecyclerView.LayoutManager layout = new LinearLayoutManager(context);
        layout.setAutoMeasureEnabled(true);
        itemListView.setLayoutManager(layout);

        CustomAdapter adapter = new CustomAdapter(events);
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

    @Override
    public void setFieldClickable(boolean clickable) {
        restoreItemList(clickable);
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomItemViewHolder> {

        private List<String> keys = new ArrayList<>();
        private List<Event> values = new ArrayList<>();

        public CustomAdapter(LinkedHashMap<String, Event> keyValues) {
            for (Map.Entry<String, Event> entry : keyValues.entrySet()) {
                keys.add(entry.getKey());
                values.add(entry.getValue());
            }
        }

        @Override
        public CustomItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.form_custom_item, parent, false);
            return new CustomItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CustomItemViewHolder holder, int position) {
            holder.setValue(keys.get(position), values.get(position));
        }

        @Override
        public int getItemCount() {
            return keys.size();
        }

        class CustomItemViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.custom_item_layout)
            LinearLayout customItemLayout;

            @BindView(R.id.custom_item_content)
            TextView customItemView;

            @BindColor(R.color.primero_blue)
            int clickableColor;

            @BindColor(R.color.primero_font_dark)
            int unclickableColor;

            public CustomItemViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void setValue(String key, Event event) {
                customItemView.setText(key);
                if (event == null) {
                    setClickable(false);
                } else {
                    setClickable(true);
                    customItemLayout.setOnClickListener(v -> {
                        EventBus.getDefault().post(event);
                    });
                }
            }

            public void setClickable(boolean clickable) {
                customItemLayout.setClickable(clickable);
                customItemView.setTextColor(clickable ? clickableColor : unclickableColor);
            }
        }
    }
}
