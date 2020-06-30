package org.unicef.rapidreg.service.impl;

import com.google.gson.JsonElement;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.repository.remote.SearchRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;


public class SearchRemoteService extends BaseRetrofitService<SearchRepository> implements SearchRepository {
    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }

    public SearchRemoteService() {}

    public Observable<Response<JsonElement>> searchRemoteCases(String cookie, Boolean showIds, String query, Boolean idSearch) {
        return getRepository(SearchRepository.class)
                .searchRemoteCases(cookie, showIds, query, idSearch)
                .retry(3)
                .timeout(90, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
