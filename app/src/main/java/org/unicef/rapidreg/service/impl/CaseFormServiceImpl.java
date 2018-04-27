package org.unicef.rapidreg.service.impl;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.repository.CaseFormDao;
import org.unicef.rapidreg.service.CaseFormService;

public class CaseFormServiceImpl implements CaseFormService {
    public static final String TAG = CaseFormService.class.getSimpleName();
    private CaseFormDao caseFormDao;

    public CaseFormServiceImpl(CaseFormDao caseFormDao) {
        this.caseFormDao = caseFormDao;
    }

    public boolean isReady() {
        User.Role roleType = PrimeroAppConfiguration.getCurrentUser().getRoleType();
        if (roleType == User.Role.CP) {
            return caseFormDao.getCaseForm(PrimeroAppConfiguration.MODULE_ID_CP, PrimeroAppConfiguration
                    .getApiBaseUrl(), PrimeroAppConfiguration.getDefaultLanguage()) != null && caseFormDao
                    .getCaseForm(PrimeroAppConfiguration.MODULE_ID_CP, PrimeroAppConfiguration.getApiBaseUrl(), PrimeroAppConfiguration.getDefaultLanguage())
                    .getForm() != null;
        }
        if (roleType == User.Role.GBV) {
            return caseFormDao.getCaseForm(PrimeroAppConfiguration.MODULE_ID_GBV, PrimeroAppConfiguration
                    .getApiBaseUrl(), PrimeroAppConfiguration.getDefaultLanguage()) != null &&
                    caseFormDao.getCaseForm(PrimeroAppConfiguration.MODULE_ID_GBV, PrimeroAppConfiguration
                            .getApiBaseUrl(), PrimeroAppConfiguration.getDefaultLanguage()).getForm() != null;
        }
        return caseFormDao.getCaseForm(PrimeroAppConfiguration.MODULE_ID_CP, PrimeroAppConfiguration.getApiBaseUrl(), PrimeroAppConfiguration.getDefaultLanguage()) != null && caseFormDao
                .getCaseForm(PrimeroAppConfiguration.MODULE_ID_CP, PrimeroAppConfiguration.getApiBaseUrl(), PrimeroAppConfiguration.getDefaultLanguage()).getForm() != null;
    }

    public CaseTemplateForm getCPTemplate() {
        Blob form = caseFormDao.getCaseForm(PrimeroAppConfiguration.MODULE_ID_CP, PrimeroAppConfiguration
                .getApiBaseUrl(), PrimeroAppConfiguration.getDefaultLanguage()).getForm();
        return getCaseTemplateForm(form);
    }

    @Override
    public CaseTemplateForm getGBVTemplate() {
        Blob form = caseFormDao.getCaseForm(PrimeroAppConfiguration.MODULE_ID_GBV, PrimeroAppConfiguration
                .getApiBaseUrl(), PrimeroAppConfiguration.getDefaultLanguage())
                .getForm();
        return getCaseTemplateForm(form);
    }

    private CaseTemplateForm getCaseTemplateForm(Blob form) {
        String formJson = new String(form.getBlob());
        if ("".equals(formJson)) {
            return null;
        }
        return new Gson().fromJson(formJson, CaseTemplateForm.class);
    }

    public void saveOrUpdate(CaseForm caseForm) {
        CaseForm existingCaseForm = caseFormDao.getCaseForm(caseForm.getModuleId(), PrimeroAppConfiguration
                .getApiBaseUrl(), PrimeroAppConfiguration.getDefaultLanguage());
        if (existingCaseForm == null) {
            caseForm.setServerUrl(PrimeroAppConfiguration.getApiBaseUrl());
            caseForm.setFormLocale(PrimeroAppConfiguration.getDefaultLanguage());
            caseForm.save();
        } else {
            existingCaseForm.setForm(caseForm.getForm());
            existingCaseForm.update();
        }
    }
}
