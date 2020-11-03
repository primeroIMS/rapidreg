package org.unicef.rapidreg.service.impl;

import android.widget.Toast;

import androidx.core.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.recordphoto.PhotoConfig;
import org.unicef.rapidreg.exception.ObservableNullResponseException;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.repository.CasePhotoDao;
import org.unicef.rapidreg.repository.remote.SyncCaseRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;
import org.unicef.rapidreg.service.SyncCaseService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.TextUtils;
import org.unicef.rapidreg.utils.Utils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

import static org.unicef.rapidreg.service.RecordService.CAREGIVER_NAME;


public class SyncCaseServiceImpl extends BaseRetrofitService<SyncCaseRepository> implements SyncCaseService {
    private CasePhotoDao casePhotoDao;

    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }

    public SyncCaseServiceImpl(CasePhotoDao casePhotoDao) {
        this.casePhotoDao = casePhotoDao;
    }

    public Observable<Response<JsonElement>> getCase(String id, String locale, Boolean isMobile) {
        return getRepository(SyncCaseRepository.class).getCase(PrimeroAppConfiguration.getCookie(), id, locale,
                isMobile);
    }

    public Observable<Response<ResponseBody>> getCasePhoto(String id, String photoKey, int
            photoSize) {
        return getRepository(SyncCaseRepository.class).getCasePhoto(PrimeroAppConfiguration.getCookie(), id, photoKey,
                photoSize);
    }

    public Observable<Response<ResponseBody>> getCaseAudio(String id) {
        return getRepository(SyncCaseRepository.class).getCaseAudio(PrimeroAppConfiguration.getCookie(), id);
    }

    @Override
    public Observable<Response<JsonElement>> getCasesIds(String moduleId, String lastUpdate, Boolean isMobile) {
        return getRepository(SyncCaseRepository.class).getCasesIds(PrimeroAppConfiguration.getCookie(), moduleId,
                lastUpdate, isMobile);
    }

    public Response<JsonElement> uploadCaseJsonProfile(RecordModel item) throws ObservableNullResponseException {
        ItemValuesMap itemValuesMap = ItemValuesMap.fromJson(new String(item.getContent().getBlob
                ()));

        String shortUUID = TextUtils.getLastSevenNumbers(item.getUniqueId());
        itemValuesMap.addStringItem("short_id", shortUUID);
        itemValuesMap.removeItem("_attachments");
        itemValuesMap.addStringItem("_id", item.getInternalId());
        itemValuesMap.addStringItem("unique_identifier", item.getUniqueIdentifier());

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("child", new Gson().fromJson(new Gson().toJson(
                itemValuesMap.getValues()), JsonObject.class));

        Observable<Response<JsonElement>> responseObservable;
        if (!TextUtils.isEmpty(item.getInternalId()) && item.getLastSyncedDate() != null) {
            responseObservable = getRepository(SyncCaseRepository.class).putCase(PrimeroAppConfiguration.getCookie(),
                    item.getInternalId(), jsonObject, true);
        } else {
            responseObservable = getRepository(SyncCaseRepository.class).postCaseExcludeMediaData
                    (PrimeroAppConfiguration
                            .getCookie(), jsonObject, true);
        }
        Response<JsonElement> response = responseObservable.blockingFirst();
        if (!response.isSuccessful()) {
            if (response.code() == 403) {
                return response;
            } else if (response.code() == 401){
                throw new HttpException(response);
            } else if (response.code() == 402){
                return response;
            }
            else {
                throw new ObservableNullResponseException(response.errorBody().toString());
            }
        }

        JsonObject responseJsonObject = response.body().getAsJsonObject();

        item.setInternalId(responseJsonObject.get("_id").getAsString());
        item.setInternalRev(responseJsonObject.get("_rev").getAsString());
        item.setCaregiver(responseJsonObject.has(CAREGIVER_NAME) ? responseJsonObject.get(CAREGIVER_NAME).getAsString() :
                null);
        responseJsonObject.remove("histories");
        item.setContent(new Blob(responseJsonObject.toString().getBytes()));
        item.update();

        return response;
    }

    public void uploadAudio(RecordModel item) throws ObservableNullResponseException {
        if (item.getAudio() != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse(
                    PhotoConfig.CONTENT_TYPE_AUDIO), item.getAudio().getBlob());
            MultipartBody.Part body = MultipartBody.Part.createFormData(FORM_DATA_KEY_AUDIO,
                    "audioFile.amr", requestFile);
            Observable<Response<JsonElement>> observable = getRepository(SyncCaseRepository.class).postCaseMediaData(
                    PrimeroAppConfiguration.getCookie(), item.getInternalId(), body);

            Response<JsonElement> response = observable.blockingFirst();
            verifyResponse(response);
            updateRecordRev(item, response.body().getAsJsonObject().get("_rev").getAsString());
        }
    }

    public Call<Response<JsonElement>> deleteCasePhotos(String id, JsonArray photoKeys) {
        JsonObject requestBody = new JsonObject();
        JsonObject requestPhotoKeys = new JsonObject();
        for (JsonElement photoKey : photoKeys) {
            requestPhotoKeys.addProperty(photoKey.getAsString(), 1);
        }
        requestBody.add("delete_child_photo", requestPhotoKeys);
        return getRepository(SyncCaseRepository.class).deleteCasePhoto(PrimeroAppConfiguration.getCookie(), id,
                requestBody);
    }

    public void uploadCasePhotos(final RecordModel record) {
        List<Long> casePhotosIds = casePhotoDao.getIdsByCaseId(record.getId());
        Observable.fromIterable(casePhotosIds)
                .filter(casePhotoId -> true)
                .flatMap(new Function<Long, Observable<Pair<CasePhoto, Response<JsonElement>>>>() {
                    @Override
                    public Observable<Pair<CasePhoto, Response<JsonElement>>> apply(final Long
                                                                                            casePhotoId) {
                        return Observable.create(new ObservableOnSubscribe<Pair<CasePhoto, Response<JsonElement>>>() {
                            @Override
                            public void subscribe(ObservableEmitter<Pair<CasePhoto, Response<JsonElement>>> emitter) throws ObservableNullResponseException {
                                CasePhoto casePhoto = casePhotoDao.getById(casePhotoId);

                                RequestBody requestFile = RequestBody.create(MediaType.parse
                                                (PhotoConfig.CONTENT_TYPE_IMAGE),
                                        casePhoto.getPhoto().getBlob());
                                MultipartBody.Part body = MultipartBody.Part.createFormData
                                        (FORM_DATA_KEY_PHOTO, casePhoto.getKey() + ".jpg",
                                                requestFile);
                                Observable<Response<JsonElement>> observable = getRepository(SyncCaseRepository.class)
                                        .postCaseMediaData(PrimeroAppConfiguration.getCookie(),
                                                record.getInternalId(), body);
                                Response<JsonElement> response = observable.blockingFirst();
                                emitter.onNext(new Pair<>(casePhoto, response));
                                emitter.onComplete();
                            }
                        });
                    }
                })
                .map(casePhotoResponsePair -> {
                    Response<JsonElement> response = casePhotoResponsePair.second;
                    CasePhoto casePhoto = casePhotoResponsePair.first;
                    updateRecordRev(record, response.body().getAsJsonObject().get("_rev")
                            .getAsString());
                    updateCasePhotoSyncStatus(casePhoto, true);
                    return casePhotoResponsePair;
                }).toList().blockingGet();
    }

    private void updateRecordRev(RecordModel record, String revId) {
        record.setInternalRev(revId);
        record.update();
    }

    private void updateCasePhotoSyncStatus(CasePhoto casePhoto, boolean status) {
        casePhoto.setSynced(status);
        casePhoto.update();
    }

    private static final String FORM_DATA_KEY_AUDIO = "child[audio]";

    private static final String FORM_DATA_KEY_PHOTO = "child[photo][0]";

    private void verifyResponse(Response<JsonElement> response) throws ObservableNullResponseException {
        if (!response.isSuccessful()) {
            Utils.showMessageByToast(PrimeroApplication.getAppContext(), R.string.sync_photos_error, Toast.LENGTH_SHORT);
        }
    }
}


