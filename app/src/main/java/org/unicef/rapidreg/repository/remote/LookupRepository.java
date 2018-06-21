package org.unicef.rapidreg.repository.remote;


import com.google.gson.JsonElement;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import io.reactivex.Observable;

public interface LookupRepository {
    @GET("/api/options")
    Observable<Response<JsonElement>> getLookups(
            @Header("Cookie") String cookie,
            @Query("locale") String locale,
            @Query("all") Boolean getAll);
}
