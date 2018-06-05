package org.unicef.rapidreg.childcase;

import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.LoginService;

import javax.inject.Inject;

public class CasePresenter extends RecordPresenter {
    private IncidentFormService incidentFormService;
    private LoginService loginService;

    @Inject
    public CasePresenter(IncidentFormService incidentFormService, LoginService loginService) {
        this.incidentFormService = incidentFormService;
        this.loginService = loginService;
    }

    public boolean isOnline() {
        return loginService.isOnline();
    }

    public boolean isIncidentFormReady() {
        return incidentFormService.isReady();
    }
}
