package org.unicef.rapidreg.childcase.casesearchweb;

import android.util.Log;

import com.google.common.base.Function;
import com.google.gson.JsonElement;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.BaseView;
import org.unicef.rapidreg.base.record.recordlist.RecordListView;
import org.unicef.rapidreg.childcase.casesearch.CaseSearchFragment;
import org.unicef.rapidreg.service.impl.SearchRemoteService;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class CaseSearchWebPresenter extends MvpBasePresenter<BaseView> {
    private SearchRemoteService searchRemoteService;

    @Inject
    public CaseSearchWebPresenter(SearchRemoteService searchRemoteService) {
        this.searchRemoteService = searchRemoteService;
    }

    public void hasResults(String query, CaseSearchWebFragment.WebSearchCallback callback) {
        searchRemoteService.searchRemoteCases(PrimeroAppConfiguration.getCookie(), true, query, true)
            .subscribe(new Action1<Response<JsonElement>>() {
                @Override
                public void call(Response<JsonElement> response) {
                    Boolean hasIds = response.body().getAsJsonArray().size() > 0;
                    callback.onSuccess(hasIds);
                }
            }, throwable -> Log.e("Case Search Web", throwable.getMessage()));
    }

    @Override
    public void attachView(BaseView view) { }

    @Override
    public void detachView(boolean retainInstance) { }
}
