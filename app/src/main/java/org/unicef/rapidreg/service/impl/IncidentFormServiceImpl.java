package org.unicef.rapidreg.service.impl;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.lookups.Option;
import org.unicef.rapidreg.repository.IncidentFormDao;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.service.IncidentFormService;

import java.util.ArrayList;
import java.util.List;

public class IncidentFormServiceImpl implements IncidentFormService {
    public static final String TAG = IncidentFormService.class.getSimpleName();
    private static final String ENG_VALUE = "en";
    public static final String SECTION_TYPE_OF_VIOLENCE = "Type of Violence";
    public static final String SECTION_GBV_INCIDENT = "GBV Incident";
    private static final String FIELD_INCIDENT_TYPE_OF_VIOLENCE = "gbv_sexual_violence_type";
    private static final String FIELD_LOCATION_INCIDENT = "incident_location";
    private IncidentFormDao incidentFormDao;

    public IncidentFormServiceImpl(IncidentFormDao incidentFormDao) {
        this.incidentFormDao = incidentFormDao;
    }

    public boolean isReady() {
        IncidentForm incidentForm = incidentFormDao.getIncidentForm(PrimeroAppConfiguration
                .MODULE_ID_GBV, PrimeroAppConfiguration.getApiBaseUrl());
        return incidentForm != null && incidentForm.getForm() != null;
    }

    @Override
    public IncidentTemplateForm getGBVTemplate() {
        Blob form = incidentFormDao.getIncidentForm(PrimeroAppConfiguration.MODULE_ID_GBV, PrimeroAppConfiguration
                .getApiBaseUrl()).getForm();
        return getIncidentTemplateForm(form);
    }

    private IncidentTemplateForm getIncidentTemplateForm(Blob form) {
        String formJson = new String(form.getBlob());
        if ("".equals(formJson)) {
            return null;
        }
        return new Gson().fromJson(formJson, IncidentTemplateForm.class);
    }

    public void saveOrUpdate(IncidentForm incidentForm) {
        IncidentForm existingIncidentForm = incidentFormDao.getIncidentForm(incidentForm.
                getModuleId(),PrimeroAppConfiguration.getApiBaseUrl());
        if (existingIncidentForm == null) {
            incidentForm.setServerUrl(PrimeroAppConfiguration.getApiBaseUrl());
            incidentForm.save();
        } else {
            existingIncidentForm.setForm(incidentForm.getForm());
            existingIncidentForm.update();
        }
    }

    @Override
    public List<Option> getViolenceTypeList() {
        IncidentTemplateForm incidentTemplateForm = getGBVTemplate();
        List<Section> sections = incidentTemplateForm.getSections();
        List<Option> selectOptions = new ArrayList<>();
        Section violenceSection = null;
        if (sections != null) {
            for (Section section : sections) {
                if (SECTION_TYPE_OF_VIOLENCE.equals(section.getName().get(PrimeroAppConfiguration.getDefaultLanguage()))) {
                    violenceSection = section;
                    break;
                }
            }
            if (violenceSection != null) {
                List<Field> fields = violenceSection.getFields();
                for (Field field : fields) {
                    if (FIELD_INCIDENT_TYPE_OF_VIOLENCE.equals(field.getName())) {
                        selectOptions = field.getSelectOptions();
                        break;
                    }
                }
            }
        }
        return selectOptions;
    }

    @Override
    public List<Option> getLocationList() {
        IncidentTemplateForm incidentTemplateForm = getGBVTemplate();
        List<Section> sections = incidentTemplateForm.getSections();
        List<Option> selectOptions = new ArrayList<>();
        Section locationSection = null;
        if (sections != null) {
            for (Section section : sections) {
                if (SECTION_GBV_INCIDENT.equals(section.getName().get(PrimeroAppConfiguration.getDefaultLanguage()))) {
                    locationSection = section;
                    break;
                }
            }
            if (locationSection != null) {
                List<Field> fields = locationSection.getFields();
                for (Field field : fields) {
                    if (FIELD_LOCATION_INCIDENT.equals(field.getName())) {
                        selectOptions = field.getSelectOptions();
                        break;
                    }
                }
            }
        }
        return selectOptions;
    }
}
