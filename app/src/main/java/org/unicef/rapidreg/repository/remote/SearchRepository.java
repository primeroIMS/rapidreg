package org.unicef.rapidreg.repository.remote;

import com.google.gson.JsonElement;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;

public interface SearchRepository {
    @GET("/cases.json")
    Observable<Response<JsonElement>> searchRemoteCases(
            @Header("Cookie") String cookie,
            @Query("ids") Boolean showIds,
            @Query("query") String parentForm,
            // NOTE: id_search is for searching cases that do not belong to you
            @Query("id_search") Boolean idSearch);
}
