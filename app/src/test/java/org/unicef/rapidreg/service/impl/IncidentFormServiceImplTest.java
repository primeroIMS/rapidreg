package org.unicef.rapidreg.service.impl;

import com.raizlabs.android.dbflow.data.Blob;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.repository.IncidentFormDao;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.IncidentForm;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IncidentFormServiceImplTest {

    @InjectMocks
    IncidentFormServiceImpl incidentFormService;

    @Mock
    IncidentFormDao incidentFormDao;

    private String formForm = "{\n" +
            "  \"Incidents\": [\n" +
            "    {\n" +
            "      \"order\": 10,\n" +
            "      \"fields\": [\n" +
            "        {\n" +
            "          \"name\": \"incidentworker_name\",\n" +
            "          \"type\": \"text_field\",\n" +
            "          \"editable\": true,\n" +
            "          \"multi_select\": false,\n" +
            "          \"mobile_visible\":true,\n" +
            "          \"show_on_minify_form\":true,\n" +
            "          \"required\":false,\n" +
            "          \"display_name\": {\n" +
            "            \"en\": \"Field/Incident/Social Worker\"\n" +
            "          },\n" +
            "          \"help_text\": {\n" +
            "            \"en\": \"\"\n" +
            "          },\n" +
            "          \"option_strings_text\": {\n" +
            "            \"en\": []\n" +
            "          }\n" +
            "        }\n" +
            "      ],\n" +
            "      \"base_language\": \"en\",\n" +
            "      \"name\": {\n" +
            "        \"en\": \"Record Owner\"\n" +
            "      },\n" +
            "      \"help_text\": {\n" +
            "        \"en\": \"\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";


    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_be_true_when_form_is_ready() {
        IncidentForm incidentForm = new IncidentForm();
        incidentForm.setForm(new Blob());
        when(incidentFormDao.getIncidentForm(PrimeroAppConfiguration.MODULE_ID_GBV, PrimeroAppConfiguration
                .getApiBaseUrl())).thenReturn
                (incidentForm);
        boolean result = incidentFormService.isReady();
        assertThat(result, is(true));
        verify(incidentFormDao, times(1)).getIncidentForm(PrimeroAppConfiguration.MODULE_ID_GBV, PrimeroAppConfiguration.getApiBaseUrl());
    }

    @Test
    public void should_be_false_when_form_is_not_exist_in_db() {
        when(incidentFormDao.getIncidentForm(PrimeroAppConfiguration.MODULE_ID_GBV, PrimeroAppConfiguration.getApiBaseUrl())).thenReturn(null);
        assertThat(incidentFormService.isReady(), is(false));
        verify(incidentFormDao, times(1)).getIncidentForm(PrimeroAppConfiguration.MODULE_ID_GBV, PrimeroAppConfiguration.getApiBaseUrl());
    }

    @Test
    public void should_be_false_when_incident_form_can_not_get_form() {
        IncidentForm incidentForm = new IncidentForm();
        when(incidentFormDao.getIncidentForm(PrimeroAppConfiguration.MODULE_ID_GBV, PrimeroAppConfiguration.getApiBaseUrl())).thenReturn
                (incidentForm);
            assertThat(incidentFormService.isReady(), is(false));
        verify(incidentFormDao, times(1)).getIncidentForm(PrimeroAppConfiguration.MODULE_ID_GBV, PrimeroAppConfiguration.getApiBaseUrl());
    }

    @Test
    public void should_get_incident_form() throws IOException {
        IncidentForm incidentForm = new IncidentForm();
        incidentForm.setForm(new Blob(formForm.getBytes()));
        when(incidentFormDao.getIncidentForm(anyString(), anyString())).thenReturn(incidentForm);
        IncidentTemplateForm form = incidentFormService.getGBVTemplate();

        assertThat(form.getSections().size(), is(1));

        Section section = form.getSections().get(0);
        assertThat(section.getName().get("en"), is("Record Owner"));
        assertThat(section.getOrder(), is(10));
        assertThat(section.getHelpText().get("en"), is(""));
        assertThat(section.getBaseLanguage(), is("en"));

        Field field = section.getFields().get(0);
        assertThat(field.getName(), is("incidentworker_name"));
        assertThat(field.getDisplayName().get("en"), is("Field/Incident/Social Worker"));
        assertThat(field.getHelpText().get("en"), is(""));
        assertThat(field.getType(), is("text_field"));
        assertThat(field.getOptionStringsText().get("en").size(), is(0));
        assertThat(field.isShowOnMiniForm(), is(true));
        assertThat(field.isMultiSelect(), is(false));
        assertThat(field.isEditable(), is(true));
        assertThat(field.isRequired(), is(false));
        assertThat(field.getSubForm(), is(nullValue()));
    }

    @Test
    public void should_save_when_existing_incident_form_is_null() {
        when(incidentFormDao.getIncidentForm(anyString(), anyString())).thenReturn(null);
        IncidentForm incidentForm = mock(IncidentForm.class);
        incidentFormService.saveOrUpdate(incidentForm);
        verify(incidentForm, times(1)).save();
    }

    @Test
    public void should_update_when_existing_incident_form_is_not_null() {
        IncidentForm existingIncidentForm = mock(IncidentForm.class);
        when(incidentFormDao.getIncidentForm(anyString(), anyString())).thenReturn(existingIncidentForm);
        IncidentForm incidentForm = mock(IncidentForm.class);
        incidentFormService.saveOrUpdate(incidentForm);
        verify(existingIncidentForm,times(1)).setForm(incidentForm.getForm());
        verify(existingIncidentForm,times(1)).update();
    }

    @Test
    public void should_load_incident_location_list_() {
        formForm = "{\n" +
                "  \"Incidents\": [\n" +
                "    {\n" +
                "      \"base_language\": \"en\",\n" +
                "      \"fields\": [{\n" +
                "        \"display_name\": {\n" +
                "          \"en\": \"Incident Location\"\n" +
                "        },\n" +
                "        \"editable\": true,\n" +
                "        \"help_text\": {\n" +
                "          \"en\": \"\"\n" +
                "        },\n" +
                "        \"index\": -1,\n" +
                "        \"show_on_minify_form\": false,\n" +
                "        \"multi_select\": false,\n" +
                "        \"name\": \"incident_location\",\n" +
                "        \"option_strings_text\": {\n" +
                "          \"en\": [\n" +
                "            \"Syria::Other\",\n" +
                "            \"Syria::Tartus\",\n" +
                "            \"Syria::Damascus\"\n" +
                "          ]\n" +
                "        },\n" +
                "        \"required\": false,\n" +
                "        \"type\": \"select_box\"\n" +
                "      }],\n" +
                "      \"help_text\": {\n" +
                "        \"en\": \"\"\n" +
                "      },\n" +
                "      \"name\": {\n" +
                "        \"en\": \"GBV Incident\"\n" +
                "      },\n" +
                "      \"order\": 10\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        IncidentForm incidentForm = new IncidentForm();
        incidentForm.setForm(new Blob(formForm.getBytes()));
        when(incidentFormDao.getIncidentForm(anyString(), anyString())).thenReturn(incidentForm);
        List<String> selectOptions = incidentFormService.getLocationList();

        assertThat(selectOptions.size(), is(3));

        List<String> expectedList = Arrays.asList(new String[]{"Syria::Other", "Syria::Tartus", "Syria::Damascus"});
        assertTrue(expectedList.equals(selectOptions));
    }

    @Test
    public void should_be_incident_location_list_empty() {
        formForm = "{\"Incidents\":[]}";
        IncidentForm incidentForm = new IncidentForm();
        incidentForm.setForm(new Blob(formForm.getBytes()));
        when(incidentFormDao.getIncidentForm(anyString(), anyString())).thenReturn(incidentForm);
        List<String> selectOptions = incidentFormService.getLocationList();
        assertThat(selectOptions.size(), is(0));
    }

    @Test
    public void should_load_violence_type_list_() {
        formForm = "{\n" +
                "  \"Incidents\": [\n" +
                "    {\n" +
                "      \"base_language\": \"en\",\n" +
                "      \"fields\": [{\n" +
                "        \"display_name\": {\n" +
                "          \"en\": \"Type of Incident Violence\"\n" +
                "        },\n" +
                "        \"editable\": true,\n" +
                "        \"help_text\": {\n" +
                "          \"en\": \"\"\n" +
                "        },\n" +
                "        \"index\": -1,\n" +
                "        \"show_on_minify_form\": false,\n" +
                "        \"multi_select\": false,\n" +
                "        \"name\": \"gbv_sexual_violence_type\",\n" +
                "        \"option_strings_text\": {\n" +
                "          \"en\": [\n" +
                "          \"Rape\",\n" +
                "          \"Sexual Assault\",\n" +
                "          \"Physical Assault\"\n" +
                "          ]\n" +
                "        },\n" +
                "        \"required\": false,\n" +
                "        \"type\": \"select_box\"\n" +
                "      }],\n" +
                "      \"help_text\": {\n" +
                "        \"en\": \"\"\n" +
                "      },\n" +
                "      \"name\": {\n" +
                "        \"en\": \"Type of Violence\"\n" +
                "      },\n" +
                "      \"order\": 40\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        IncidentForm incidentForm = new IncidentForm();
        incidentForm.setForm(new Blob(formForm.getBytes()));
        when(incidentFormDao.getIncidentForm(anyString(), anyString())).thenReturn(incidentForm);
        List<String> selectOptions = incidentFormService.getViolenceTypeList();

        assertThat(selectOptions.size(), is(3));

        List<String> expectedList = Arrays.asList(new String[]{"Rape", "Sexual Assault", "Physical Assault"});
        assertTrue(expectedList.equals(selectOptions));
    }

    @Test
    public void should_be_violence_type_list_empty() {
        formForm = "{\"Incidents\":[]}";
        IncidentForm incidentForm = new IncidentForm();
        incidentForm.setForm(new Blob(formForm.getBytes()));
        when(incidentFormDao.getIncidentForm(anyString(), anyString())).thenReturn(incidentForm);
        List<String> selectOptions = incidentFormService.getViolenceTypeList();
        assertThat(selectOptions.size(), is(0));
    }
}
