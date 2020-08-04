package org.unicef.rapidreg.base.record.recordregister;

import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.List;

public class RecordTabProvider implements SmartTabLayout.TabProvider {
    private final LayoutInflater layoutInflater;
    private final List<Section> sections;
    private final ItemValuesMap itemValues;

    public RecordTabProvider(
            final LayoutInflater layoutInflater,
            final List<Section> sections,
            final ItemValuesMap itemValues) {
        this.layoutInflater = layoutInflater;
        this.sections = sections;
        this.itemValues = itemValues;
    }

    @Override
    public View createTabView(
            final ViewGroup container,
            final int position,
            final PagerAdapter adapter) {
        View itemView = this.layoutInflater.inflate(R.layout.custom_tab_icon_and_text, container, false);
        TextView text = (TextView) itemView.findViewById(R.id.tab_text);
        text.setText(adapter.getPageTitle(position));
        ImageView icon = (ImageView) itemView.findViewById(R.id.tab_icon);
        boolean showAlert = this.sections.get(position).getUniqueId().equals(RecordModel.ALERT_NOTE_TYPE) && this.itemValues.hasNoteAlerts();

        icon.setVisibility(showAlert ? View.VISIBLE : View.GONE);

        return itemView;
    }
}
