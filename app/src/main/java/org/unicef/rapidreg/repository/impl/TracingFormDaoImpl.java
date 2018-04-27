package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.model.TracingForm;
import org.unicef.rapidreg.model.TracingForm_Table;
import org.unicef.rapidreg.model.Tracing_Table;
import org.unicef.rapidreg.repository.TracingFormDao;

public class TracingFormDaoImpl implements TracingFormDao {
    @Override
    public TracingForm getTracingForm(String apiBaseUrl, String formLocale) {
        return SQLite.select()
                .from(TracingForm.class)
                .where(TracingForm_Table.server_url.eq(apiBaseUrl))
                .and(TracingForm_Table.form_locale.eq(formLocale))
                .querySingle();
    }
}
