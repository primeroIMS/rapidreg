package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.model.Lookup;
import org.unicef.rapidreg.model.Lookup_Table;
import org.unicef.rapidreg.repository.LookupDao;

public class LookupDaoImpl implements LookupDao {
    @Override
    public Lookup getByServerUrlAndLocale(String apiBaseUrl, String locale) {
        return SQLite.select()
                .from(Lookup.class)
                .where(Lookup_Table.server_url.eq(apiBaseUrl))
                .and(Lookup_Table.locale.eq(locale))
                .querySingle();
    }

    @Override
    public void save(Lookup lookups) {
        lookups.save();
    }

    @Override
    public void update(Lookup lookups) {
        lookups.update();
    }
}
