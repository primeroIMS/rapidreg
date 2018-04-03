package org.unicef.rapidreg.service;

import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.lookups.Option;
import org.unicef.rapidreg.model.IncidentForm;

import java.util.List;

public interface IncidentFormService {

    boolean isReady();

    IncidentTemplateForm getGBVTemplate();

    void saveOrUpdate(IncidentForm incidentForm);

    List<Option> getViolenceTypeList();

    List<Option> getLocationList();
}
