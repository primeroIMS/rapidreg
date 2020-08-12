package org.unicef.rapidreg.service.impl;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.model.TracingForm;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.service.AppDataService;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.FormRemoteService;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.LookupService;
import org.unicef.rapidreg.service.SystemSettingsService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.utils.Utils;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

import static org.unicef.rapidreg.PrimeroAppConfiguration.MODULE_ID_CP;
import static org.unicef.rapidreg.PrimeroAppConfiguration.MODULE_ID_GBV;

public class AppDataServiceImpl implements AppDataService {
    private static final String TAG = "AppData";

    private SystemSettingsService systemSettingsService;
    private LookupService lookupService;
    private FormRemoteService formRemoteService;
    private CaseFormService caseFormService;
    private TracingFormService tracingFormService;
    private IncidentFormService incidentFormService;

    private AppDataService.LoadCallback callback;
    private User.Role roleType;

    private Disposable systemSettingsDisposable;
    private Disposable lookupDisposable;
    private Disposable caseFormDisposable;
    private Disposable tracingFormDisposable;
    private Disposable incidentFormDisposable;

    private static CompositeDisposable compositeDisposable;

    protected final Gson gson = new Gson();

    private Intent broadcastIntent;

    public AppDataServiceImpl(LookupService lookupService, SystemSettingsService systemSettingsService,
                              FormRemoteService formRemoteService, CaseFormService caseFormService,
                              TracingFormService tracingFormService, IncidentFormService incidentFormService) {
        this.lookupService = lookupService;
        this.systemSettingsService = systemSettingsService;
        this.formRemoteService = formRemoteService;
        this.caseFormService = caseFormService;
        this.tracingFormService = tracingFormService;
        this.incidentFormService = incidentFormService;
    }

    public void loadAppData(AppDataService.LoadCallback loginCallback, User.Role userRoleType, boolean forceUpdate) {
        callback = loginCallback;
        roleType = userRoleType;
        broadcastIntent = new Intent("sync_form_progress");

        if (forceUpdate || !formSynced()) {
            loadSystemSettings();
        } else {
            systemSettingsService.setGlobalSystemSettings();
            lookupService.setLookups();
            callback.onSuccess();
        }
    }

    public void loadCaseForm() {
        String moduleId = roleType == User.Role.CP ? PrimeroAppConfiguration.MODULE_ID_CP :
                PrimeroAppConfiguration.MODULE_ID_GBV;

        sendProgress("resource", "sync_case_forms");

        caseFormDisposable = formRemoteService.getCaseForm(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getServerLocale
                (), true, PrimeroAppConfiguration.PARENT_CASE, moduleId)
                .subscribe(caseForm -> {
                    saveCaseForm(caseForm, moduleId);
                }, throwable -> {
                    loadFail(throwable);
                    Log.e(TAG, "Case Form Error -> " + throwable.getMessage());
                    callback.onFailure();
                }, () -> {
                    if (moduleId == PrimeroAppConfiguration.MODULE_ID_GBV) {
                        loadIncidentForm();
                    } else {
                        loadTracingForm();
                    }

                    sendProgress("progress", 50);
                });

        getCompositeDisposable().add(caseFormDisposable);
    }


    public void saveCaseForm(RecordForm recordForm, String moduleId) {
        Blob caseFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        CaseForm caseForm = new CaseForm(caseFormBlob);
        caseForm.setModuleId(moduleId);
        caseFormService.saveOrUpdate(caseForm);
    }

    public void loadTracingForm() {
        sendProgress("resource", "sync_tracing_request_forms");
        tracingFormDisposable = formRemoteService.getTracingForm(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getServerLocale
                (), true, PrimeroAppConfiguration.PARENT_TRACING_REQUEST, MODULE_ID_CP)
                .subscribe(tracingForm -> {
                    saveTracingForm(tracingForm);
                }, throwable -> {
                    Log.e(TAG, "Tracing Form Error -> " + throwable.getMessage());
                    loadFail(throwable);
                    callback.onFailure();
                }, () -> {
                    loadLookups();
                    sendProgress("progress", 75);
                });

        getCompositeDisposable().add(tracingFormDisposable);
    }

    public void saveTracingForm(RecordForm recordForm) {
        Blob tracingFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        TracingForm tracingForm = new TracingForm(tracingFormBlob);
        tracingForm.setModuleId(MODULE_ID_CP);
        tracingFormService.saveOrUpdate(tracingForm);
    }

    public void loadIncidentForm() {
        sendProgress("resource", "sync_incident_forms");
        incidentFormDisposable = formRemoteService.getIncidentForm(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getServerLocale
                (), true, PrimeroAppConfiguration.PARENT_INCIDENT, MODULE_ID_GBV)
                    .subscribe(incidentForm -> {
                        saveIncidentForm(incidentForm);
                    }, throwable -> {
                        Log.e(TAG, "Incident Form Error -> " + throwable.getMessage());
                        loadFail(throwable);
                        callback.onFailure();
                    }, () -> {
                        loadLookups();
                        sendProgress("progress", 75);
                    });

        getCompositeDisposable().add(incidentFormDisposable);
    }

    public void saveIncidentForm(RecordForm recordForm) {
        Blob incidentFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        IncidentForm incidentForm = new IncidentForm(incidentFormBlob);
        incidentForm.setModuleId(MODULE_ID_GBV);
        incidentFormService.saveOrUpdate(incidentForm);
    }


    public void loadLookups() {
        sendProgress("resource", "sync_lookups");
        lookupDisposable = lookupService.getLookups(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getServerLocale(), true)
                .subscribe(lookup -> {
                    lookupService.saveOrUpdate(lookup, true);
                }, throwable -> {
                    Log.e(TAG, "Lookups Error -> " + throwable.getMessage());
                    loadFail(throwable);
                    callback.onFailure();
                }, () -> {
                    callback.onSuccess();
                    getCompositeDisposable().dispose();
                    sendProgress("progress", 100);
                });

        getCompositeDisposable().add(lookupDisposable);
    }

    public void loadSystemSettings() {
        sendProgress("resource", "settings");
        systemSettingsDisposable = systemSettingsService.getSystemSettings()
                .subscribe(systemSettings -> {
                    systemSettingsService.saveOrUpdateSystemSettings(systemSettings);
                }, throwable -> {
                    Log.e(TAG, "Init system settings error -> " + throwable.getMessage());
                    loadFail( throwable);
                    callback.onFailure();
                }, () -> {
                    systemSettingsService.setGlobalSystemSettings();
                    loadCaseForm();
                    sendProgress("progress", 25);
                });
        getCompositeDisposable().add(systemSettingsDisposable);
    }

    public void loadFail(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            if (httpException.code() == 401)
            {
                PrimeroAppConfiguration.setAuthorized(false);
                Utils.showMessageByToast(PrimeroApplication.getAppContext(), R.string.sync_pull_unauthorized_error_message, Toast.LENGTH_SHORT);
                sendProgress("progress", 110);
            }
        }
    }

    private static CompositeDisposable getCompositeDisposable() {
        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        return compositeDisposable;
    }

    private void sendProgress(String key, int value) {
        broadcastIntent.putExtra(key, value);
        broadcastProgress();
    }

    private void sendProgress(String key, String value) {
        broadcastIntent.putExtra(key, value);
        broadcastProgress();
    }

    private void broadcastProgress() {
        LocalBroadcastManager.getInstance(PrimeroApplication.getAppContext()).sendBroadcast(broadcastIntent);
    }

    private boolean formSynced() {
        boolean formsSynced = false;

        if (roleType == User.Role.CP) {
            formsSynced = caseFormService.isReady() && tracingFormService.isReady();
        } else if (roleType == User.Role.GBV) {
            formsSynced = caseFormService.isReady() && incidentFormService.isReady();
        }

        return formsSynced && lookupService.isReady();
    }
}
