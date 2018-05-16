package org.unicef.rapidreg.repository;

import org.unicef.rapidreg.model.Lookup;

public interface LookupDao {
    Lookup getByServerUrlAndLocale(String apiBaseUrl, String locale);

    void save(Lookup systemSettings);

    void update(Lookup systemSettings);
}

