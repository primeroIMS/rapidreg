package org.unicef.rapidreg.sync;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.FormRemoteService;
import org.unicef.rapidreg.service.LookupService;
import org.unicef.rapidreg.R;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseSyncPresenter extends MvpBasePresenter<SyncView> {
    protected Context context;

    protected CaseService caseService;
    private CaseFormService caseFormService;
    private LookupService lookupService;

    protected FormRemoteService formRemoteService;

    protected int numberOfSuccessfulUploadedRecords;
    protected int totalNumberOfUploadRecords;

    protected boolean isSyncing;

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Disposable downloadCaseFormDisposable;
    private Disposable downloadLookupsDisposable;


    private List<Case> cases;

    protected static final int NO_VALUE = 0;

    public BaseSyncPresenter(Context context, CaseService caseService, CaseFormService caseFormService,
                             FormRemoteService formRemoteService, LookupService lookupService) {
        this.context = context;
        this.caseService = caseService;
        this.caseFormService = caseFormService;
        this.formRemoteService = formRemoteService;
        this.lookupService = lookupService;

        cases = caseService.getAll();
    }

    @Override
    public void attachView(SyncView view) {
        super.attachView(view);
        if (isViewAttached()) {
            getView().setNotSyncedRecordNumber(totalNumberOfUploadRecords);
        }
    }

    public void tryToSync() {
        if (isViewAttached()) {
            getView().showAttemptSyncDialog();
        }
    }

    public void execSync() {
        if (!isViewAttached()) {
            return;
        }
        try {
            getView().disableSyncButton();
            initSyncRecordNumber();
            upLoadCases(cases);
        } catch (Throwable t) {
            syncFail(t);
        }
    }

    protected void initSyncRecordNumber() {
        numberOfSuccessfulUploadedRecords = 0;
        totalNumberOfUploadRecords = 0;
        totalNumberOfUploadRecords += countNumber(cases);
        totalNumberOfUploadRecords += countNumber(getTracings());
        totalNumberOfUploadRecords += countNumber(getIncidents());
    }

    private int countNumber(List<? extends RecordModel> recordModels) {
        int initNumber = 0;
        for (RecordModel recordModel : recordModels) {
            if (!recordModel.isSynced()) {
                initNumber++;
            }
        }
        return initNumber;
    }

    public void attemptCancelSync() {
        if (isViewAttached()) {
            getView().showSyncCancelConfirmDialog();
        }
    }

    public void cancelSync() {
        isSyncing = false;
    }

    protected void increaseSyncNumber() {
        numberOfSuccessfulUploadedRecords += 1;
    }

    protected List<Tracing> getTracings() {
        return Collections.EMPTY_LIST;
    }

    protected List<Incident> getIncidents() {
        return Collections.EMPTY_LIST;
    }

    protected abstract void upLoadCases(List<Case> cases);

    public void downloadCaseForm(ProgressDialog loadingDialog, String moduleId) {
        downloadCaseFormDisposable = formRemoteService.getCaseForm(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getDefaultLanguage
                (), true, PrimeroAppConfiguration.PARENT_CASE, moduleId)
                .subscribe(caseFormJson -> {
                            CaseForm caseForm = new CaseForm(new Blob(new Gson().toJson(caseFormJson).getBytes()));
                            caseForm.setModuleId(moduleId);
                            caseFormService.saveOrUpdate(caseForm);
                        },
                        throwable -> {
                            loadingDialog.dismiss();
                            syncFail(throwable);
                        },
                        () -> {
                            downloadLookups();
                            downloadSecondFormByModule();
                            loadingDialog.dismiss();
                        });

        compositeDisposable.add(downloadCaseFormDisposable);
    }

    protected abstract void downloadSecondFormByModule();

    protected void downloadLookups() {
        downloadLookupsDisposable = lookupService.getLookups(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getDefaultLanguage(), true)
                .subscribe(lookup -> lookupService.saveOrUpdate(lookup, true),
                        throwable -> syncFail(throwable),
                        () -> syncPullLookupsSuccessfully());

        compositeDisposable.add(downloadLookupsDisposable);
    }

    protected void syncPullLookupsSuccessfully() {
        if (getView() != null && isViewAttached()) {
            getView().showSyncPullLookupsSuccessMessage();
            getView().hideSyncProgressDialog();
        }
    }


    protected void syncPullFormSuccessfully() {
        if (getView() != null && isViewAttached()) {
            getView().showSyncPullFormSuccessMessage();
            getView().hideSyncProgressDialog();
            getView().enableSyncButton();
        }
    }

    protected void updateRecordSynced(RecordModel record, boolean synced) {
        record.setSynced(synced);
        record.update();
    }

    protected void updateRecordInvalidated(Case record, boolean invalidated) {
        record.setInvalidated(invalidated);
        record.update();
    }

    protected void syncFail(Throwable throwable) {
        if (getView() == null || !isViewAttached()) {
            return;
        }
        Throwable cause = throwable.getCause();
        if (throwable instanceof SocketTimeoutException || cause instanceof
                SocketTimeoutException) {
            getView().showRequestTimeoutSyncErrorMessage();
        } else if (throwable instanceof ConnectException || cause instanceof ConnectException
                || throwable instanceof IOException || cause instanceof IOException) {
            getView().showServerNotAvailableSyncErrorMessage();
        } else {
            getView().showSyncErrorMessage();
        }
        getView().hideSyncProgressDialog();
        updateDataViews();
        getView().enableSyncButton();
    }

    protected void setProgressIncrease() {
        if (getView() != null && isViewAttached()) {
            getView().setProgressIncrease();
        }
    }

    protected void setAgeIfExists(RecordModel item, JsonObject source) {
        try {
            item.setAge(source.get("age").getAsInt());
        } catch (Exception e) {
            item.setAge(-1);
        }
    }

    private void updateDataViews() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        String currentDateTime = sdf.format(new Date());
        int numberOfFailedUploadedCases = totalNumberOfUploadRecords -
                numberOfSuccessfulUploadedRecords;

        getView().setDataViews(currentDateTime, String.valueOf(numberOfSuccessfulUploadedRecords)
                , String.valueOf
                        (numberOfFailedUploadedCases));

        PrimeroApplication.getAppRuntime().storeSyncData(new SyncStatisticData(currentDateTime, numberOfSuccessfulUploadedRecords, numberOfFailedUploadedCases));
    }

    protected void syncUploadSuccessfully() {
        if (getView() != null && isViewAttached()) {
            updateDataViews();
            getView().showSyncUploadSuccessMessage();
            getView().hideSyncProgressDialog();
        }
    }

    protected void syncDownloadSuccessfully() {
        if (getView() != null && isViewAttached()) {
            updateDataViews();
            getView().showSyncDownloadSuccessMessage();
            getView().hideSyncProgressDialog();
        }
    }

    protected void disposeOfDisposables() {
        compositeDisposable.dispose();
    }

    protected void reportReassignedCasesIfAny(List<String> casesShortIds) {
        String message = "";
        StringBuilder caseIds = new StringBuilder();

        if (casesShortIds != null && casesShortIds.size() > 0) {
            if (casesShortIds.size() == 1) {
                message = String.format(context.getResources().getString(R.string.sync_one_case_reassigned), casesShortIds.get(0));
            } else {
                String caseId = "";
                for (int i = 0; i < casesShortIds.size(); i++) {
                    caseId = casesShortIds.get(i) + (i < casesShortIds.size() - 1 ? ", " : "");
                    caseIds.append(caseId);
                }
                message = String.format(context.getResources().getString(R.string.sync_many_case_reassigned), caseIds.toString());
            }
            getView().showReassignedCasesWarningMessage(message);
        }
    }
}
