package org.unicef.rapidreg.service;

import org.unicef.rapidreg.forms.LookupOptions;
import org.unicef.rapidreg.model.Lookup;

import rx.Observable;

import java.util.List;

public interface LookupService {
    Observable<Lookup> getLookups(String cookie, String locale, Boolean getAll);

    void saveOrUpdate(Lookup lookup);

    List<LookupOptions> getLookupOptions(String id);
}
