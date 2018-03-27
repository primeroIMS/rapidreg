package org.unicef.rapidreg.repository;

import org.unicef.rapidreg.model.Lookup;

public interface LookupDao {
    Lookup getByServerUrl(String apiBaseUrl);

    void save(Lookup systemSettings);

    void update(Lookup systemSettings);
}

