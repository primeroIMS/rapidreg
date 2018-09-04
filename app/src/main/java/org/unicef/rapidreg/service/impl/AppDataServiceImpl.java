package org.unicef.rapidreg.service.impl;

import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
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

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

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

    public void loadAppData(AppDataService.LoadCallback loginCallback, User.Role userRoleType) {
        callback = loginCallback;
        roleType = userRoleType;

        loadSystemSettings();
    }

    public void loadCaseForm() {
        String moduleId = roleType == User.Role.CP ? PrimeroAppConfiguration.MODULE_ID_CP :
                PrimeroAppConfiguration.MODULE_ID_GBV;

        caseFormDisposable = formRemoteService.getCaseForm(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getServerLocale
                (), true, PrimeroAppConfiguration.PARENT_CASE, moduleId)
                .subscribe(caseForm -> {
                    saveCaseForm(caseForm, moduleId);
                }, throwable -> {
                    Log.e(TAG, "Case Form Error -> " + throwable.getMessage());
                    callback.onFailure();
                }, () -> {
                    if (moduleId == PrimeroAppConfiguration.MODULE_ID_GBV) {
                        loadIncidentForm();
                    } else {
                        loadTracingForm();
                    }
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
        tracingFormDisposable = formRemoteService.getTracingForm(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getServerLocale
                (), true, PrimeroAppConfiguration.PARENT_TRACING_REQUEST, MODULE_ID_CP)
                .subscribe(tracingForm -> {
                    saveTracingForm(tracingForm);
                }, throwable -> {
                    Log.e(TAG, "Tracing Form Error -> " + throwable.getMessage());
                    callback.onFailure();
                }, () -> loadLookups());

        getCompositeDisposable().add(tracingFormDisposable);
    }

    public void saveTracingForm(RecordForm recordForm) {
        Blob tracingFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        TracingForm tracingForm = new TracingForm(tracingFormBlob);
        tracingForm.setModuleId(MODULE_ID_CP);
        tracingFormService.saveOrUpdate(tracingForm);
    }

    public void loadIncidentForm() {
        incidentFormDisposable = formRemoteService.getIncidentForm(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getServerLocale
                (), true, PrimeroAppConfiguration.PARENT_INCIDENT, MODULE_ID_GBV)
                    .subscribe(incidentForm -> {
                        saveIncidentForm(incidentForm);
                    }, throwable -> {
                        Log.e(TAG, "Incident Form Error -> " + throwable.getMessage());
                        callback.onFailure();
                    }, () -> loadLookups());

        getCompositeDisposable().add(incidentFormDisposable);
    }

    public void saveIncidentForm(RecordForm recordForm) {
        Blob incidentFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        IncidentForm incidentForm = new IncidentForm(incidentFormBlob);
        incidentForm.setModuleId(MODULE_ID_GBV);
        incidentFormService.saveOrUpdate(incidentForm);
    }


    public void loadLookups() {
        lookupDisposable = lookupService.getLookups(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getServerLocale(), true)
                .subscribe(lookup -> {
                    lookupService.saveOrUpdate(lookup, false);
                }, throwable -> {
                    Log.e(TAG, "Lookups Error -> " + throwable.getMessage());
                    callback.onFailure();
                }, () -> {
                    callback.onSuccess();
                    getCompositeDisposable().dispose();
                });

        getCompositeDisposable().add(lookupDisposable);
    }

    public void loadSystemSettings() {
        systemSettingsDisposable = systemSettingsService.getSystemSettings()
                .subscribe(systemSettings -> {
                    systemSettingsService.saveOrUpdateSystemSettings(systemSettings);
                }, throwable -> {
                    Log.e(TAG, "Init system settings error -> " + throwable.getMessage());
                    callback.onFailure();
                }, () -> {
                    systemSettingsService.setGlobalSystemSettings();
                    loadCaseForm();
                });
        getCompositeDisposable().add(systemSettingsDisposable);
    }

    private static CompositeDisposable getCompositeDisposable() {
        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        return compositeDisposable;
    }

}
