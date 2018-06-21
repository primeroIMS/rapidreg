package org.unicef.rapidreg.service;

import com.google.gson.JsonElement;

import org.unicef.rapidreg.exception.ObservableNullResponseException;
import org.unicef.rapidreg.model.Incident;

import retrofit2.Response;
import io.reactivex.Observable;

public interface SyncIncidentService {
    Response<JsonElement> uploadIncidentJsonProfile(Incident item) throws ObservableNullResponseException;

    Observable<Response<JsonElement>> getIncidentIds(String lastUpdate, boolean isMobile);

    Observable<Response<JsonElement>> getIncident(String id, String locale, boolean isMobile);
}
