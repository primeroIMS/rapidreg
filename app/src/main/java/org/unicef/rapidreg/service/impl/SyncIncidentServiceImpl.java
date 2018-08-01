package org.unicef.rapidreg.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.exception.ObservableNullResponseException;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.repository.remote.SyncIncidentRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.SyncIncidentService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.TextUtils;

import retrofit2.Response;
import io.reactivex.Observable;


public class SyncIncidentServiceImpl extends BaseRetrofitService<SyncIncidentRepository> implements
        SyncIncidentService {
    @Override
    public Response<JsonElement> uploadIncidentJsonProfile(Incident item) throws ObservableNullResponseException {
        ItemValuesMap itemValuesMap = ItemValuesMap.fromJson(new String(item.getContent().getBlob()));
        String shortUUID = TextUtils.getLastSevenNumbers(item.getUniqueId());

        itemValuesMap.addStringItem("short_id", shortUUID);
        itemValuesMap.removeItem("_attachments");
        itemValuesMap.addStringItem("_id", item.getInternalId());
        itemValuesMap.addStringItem("unique_identifier", item.getUniqueIdentifier());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("incident_case_id", item.getIncidentCaseId());
        jsonObject.add("incident", new Gson().fromJson(new Gson().toJson(
                itemValuesMap.getValues()), JsonObject.class));

        Response<JsonElement> response;
        if (!TextUtils.isEmpty(item.getInternalId())) {
            response = getRepository(SyncIncidentRepository.class).putIncident(PrimeroAppConfiguration.getCookie(),
                    item.getInternalId(), jsonObject).blockingFirst();
        } else {
            response = getRepository(SyncIncidentRepository.class).postIncident(PrimeroAppConfiguration.getCookie(),
                    jsonObject)
                    .blockingFirst();
        }
        if (!response.isSuccessful()) {
            throw new ObservableNullResponseException(response.errorBody().toString());
        }

        JsonObject responseJsonObject = response.body().getAsJsonObject();

        item.setInternalId(responseJsonObject.get("_id").getAsString());
        item.setInternalRev(responseJsonObject.get("_rev").getAsString());
        item.setLocation(responseJsonObject.has(RecordService.INCIDENT_LOCATION) ? responseJsonObject.get(RecordService
                .INCIDENT_LOCATION).getAsString() : null);
        responseJsonObject.remove("histories");
        item.setContent(new Blob(responseJsonObject.toString().getBytes()));
        item.update();

        return response;
    }

    @Override
    public Observable<Response<JsonElement>> getIncidentIds(String lastUpdate, boolean isMobile) {
        return getRepository(SyncIncidentRepository.class).getIncidentIds(PrimeroAppConfiguration.getCookie(),
                lastUpdate,
                isMobile);
    }

    @Override
    public Observable<Response<JsonElement>> getIncident(String id, String locale, boolean isMobile) {
        return getRepository(SyncIncidentRepository.class).getIncident(PrimeroAppConfiguration.getCookie(), id,
                locale, isMobile);
    }

    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }
}
