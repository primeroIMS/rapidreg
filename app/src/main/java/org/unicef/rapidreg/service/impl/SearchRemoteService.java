package org.unicef.rapidreg.service.impl;

import com.google.gson.JsonElement;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.repository.remote.SearchRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchRemoteService extends BaseRetrofitService<SearchRepository> implements SearchRepository {
    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }

    public SearchRemoteService() {}

    public Observable<Response<JsonElement>> searchRemoteCases(String cookie, Boolean showIds, String query) {
        return getRepository(SearchRepository.class)
                .searchRemoteCases(cookie, showIds, query)
                .retry(3)
                .timeout(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
