package org.unicef.rapidreg.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.exception.ObservableNullResponseException;
import org.unicef.rapidreg.model.Lookup;
import org.unicef.rapidreg.repository.LookupDao;
import org.unicef.rapidreg.repository.remote.LookupRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;
import org.unicef.rapidreg.service.LookupService;
import org.unicef.rapidreg.service.cache.GlobalLookupCache;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class LookupServiceImpl extends BaseRetrofitService<LookupRepository> implements LookupService {
    protected String getBaseUrl() { return PrimeroAppConfiguration.getApiBaseUrl(); }

    private static final String TAG = LookupService.class.getSimpleName();

    protected final Gson gson = new Gson();

    private LookupDao lookupDao;

    public LookupServiceImpl(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    @Override
    public Observable<Lookup> getLookups(String cookie, String locale, Boolean getAll) {
        return getRepository(LookupRepository.class).getLookups(cookie, locale, getAll)
                .map(response -> {
                    Lookup lookups = new Lookup();
                    lookups.setServerUrl(PrimeroAppConfiguration.getApiBaseUrl());
                    lookups.setLocale(PrimeroAppConfiguration.getServerLocale());

                    if (response == null) {
                        throw new ObservableNullResponseException();
                    } else {
                        if (response.isSuccessful()) {
                            JsonObject jsonObject = response.body().getAsJsonObject();
                            JsonArray sources = jsonObject.getAsJsonArray("sources");

                            if (sources.size() > 0) {
                                Blob lookupsBlob = new Blob(gson.toJson(sources).getBytes());
                                lookups.setLookupsJson(lookupsBlob);
                            }

                        }

                        return lookups;
                    }
                })
                .retry(3)
                .timeout(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void setLookups() {
        Lookup lookups = lookupDao.getByServerUrlAndLocale(PrimeroAppConfiguration.getApiBaseUrl(), PrimeroAppConfiguration.getServerLocale());

        if (lookups != null) {
          GlobalLookupCache.initLookupOptions(lookups);
        }
    }

    @Override
    public void saveOrUpdate(Lookup lookups, Boolean forceReload) {
        Lookup currentLookups = lookupDao.getByServerUrlAndLocale(lookups.getServerUrl(), PrimeroAppConfiguration.getServerLocale());
        if (currentLookups == null) {
            lookupDao.save(lookups);
        } else {
            lookupDao.update(currentLookups);
        }

        if (forceReload) {
            GlobalLookupCache.clearLookups();
        }

        GlobalLookupCache.initLookupOptions(lookups);
    }
}
