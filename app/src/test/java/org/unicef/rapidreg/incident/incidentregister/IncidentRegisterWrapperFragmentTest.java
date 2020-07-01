package org.unicef.rapidreg.incident.incidentregister;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordPhotoPageChangeListener;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterWrapperFragment;
import org.unicef.rapidreg.event.SaveIncidentEvent;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.incident.IncidentActivity;
import org.unicef.rapidreg.incident.IncidentFeature;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.support.membermodification.MemberModifier.stub;
import static org.unicef.rapidreg.service.CaseService.CASE_ID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IncidentRegisterWrapperFragment.class, Utils.class})
public class IncidentRegisterWrapperFragmentTest {

    @Mock
    IncidentRegisterPresenter incidentRegisterPresenter;

    @Mock
    FragmentComponent fragmentComponent;

    @Mock
    RecordPhotoAdapter recordPhotoAdapter;

    @Mock
    Bundle arguments;

    @Mock
    IncidentActivity incidentActivity;

    @Mock
    RecordForm form;

    @Mock
    List<Section> sections;

    @Mock
    Section section;


    @InjectMocks
    IncidentRegisterWrapperFragment incidentRegisterWrapperFragment = PowerMockito.spy(new IncidentRegisterWrapperFragment());

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(Utils.class);
        doNothing().when(Utils.class, "showMessageByToast", any(Context.class),anyInt(),anyInt());
        stub(PowerMockito.method(IncidentRegisterWrapperFragment.class, "getComponent")).toReturn(fragmentComponent);
        stub(PowerMockito.method(IncidentRegisterWrapperFragment.class, "getActivity")).toReturn(incidentActivity);
        stub(PowerMockito.method(IncidentRegisterWrapperFragment.class, "getArguments")).toReturn(arguments);
        stub(PowerMockito.method(IncidentRegisterWrapperFragment.class, "getCurrentPhotoAdapter")).toReturn(recordPhotoAdapter);
        stub(PowerMockito.method(IncidentRegisterWrapperFragment.class, "getFieldValueVerifyResult")).toReturn(new ItemValuesMap());
    }

    @Test
    public void test_create_presenter() {
        assertEquals(incidentRegisterPresenter, incidentRegisterWrapperFragment.createPresenter());
    }

    @Test
    public void test_on_create_view() {
        LayoutInflater inflater = PowerMockito.mock(LayoutInflater.class);
        ViewGroup viewGroup = PowerMockito.mock(ViewGroup.class);
        Bundle bundle = PowerMockito.mock(Bundle.class);
        try {
            PowerMockito.suppress(RecordRegisterWrapperFragment.class.getMethod("onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        incidentRegisterWrapperFragment.onCreateView(inflater, viewGroup, bundle);
        verify(fragmentComponent, times(1)).inject(incidentRegisterWrapperFragment);

    }

    @Test
    public void test_create_record_photo_adapter() {
        assertEquals(null, incidentRegisterWrapperFragment.createRecordPhotoAdapter());
    }

    @Test
    public void test_save_incident() throws IllegalAccessException {
        String caseId = "123";
        ItemValuesMap recordRegisterData = PowerMockito.mock(ItemValuesMap.class);
        stub(PowerMockito.method(IncidentRegisterWrapperFragment.class, "getRecordRegisterData")).toReturn(recordRegisterData);
        when(arguments.getString(CASE_ID)).thenReturn(caseId);
        incidentRegisterWrapperFragment.saveIncident(new SaveIncidentEvent());
        verify(recordRegisterData, times(1)).addStringItem(CASE_ID, caseId);
        verify(incidentRegisterPresenter, times(1)).saveRecord(any(), any(), any());
    }

    @Test
    public void test_on_edit_clicked() {
        when(incidentRegisterWrapperFragment.getFieldValueVerifyResult()).thenReturn(new ItemValuesMap());
        incidentRegisterWrapperFragment.onEditClicked();
        verify(incidentActivity, times(1)).turnToFeature(any(IncidentFeature.class), any(Bundle.class), any());
    }

    @Test
    public void test_on_init_item_values() throws IllegalAccessException {
        Whitebox.setInternalState(incidentRegisterWrapperFragment, "recordPhotoPageChangeListener", new RecordPhotoPageChangeListener());
        incidentRegisterWrapperFragment.initItemValues();
        verify(incidentRegisterWrapperFragment, times(1)).setRecordRegisterData(any());
        verify(incidentRegisterWrapperFragment, times(1)).setFieldValueVerifyResult(any());
    }

    @Test
    public void test_on_init_form_data() {
        when(incidentRegisterPresenter.getTemplateForm()).thenReturn(form);
        incidentRegisterWrapperFragment.initFormData();
        verify(incidentRegisterPresenter, times(1)).getTemplateForm();
        verify(form, times(1)).getSections();
    }

    @Test
    public void test_get_pages() {
        when(form.getSections()).thenReturn(sections);
        Iterator<Section> sectionsIterator = PowerMockito.mock(Iterator.class);
        when(sections.iterator()).thenReturn(sectionsIterator);
        when(sectionsIterator.next()).thenReturn(section);
        Map names = new HashMap<String, String>();
        names.put("test", "test");
        when(section.getName()).thenReturn(names);
        when(sectionsIterator.hasNext()).thenReturn(true, false);

        FragmentPagerItems pages = incidentRegisterWrapperFragment.getPages();

        assertNotNull(pages);
        Assert.assertThat(pages, CoreMatchers.instanceOf(FragmentPagerItems.class));
        assertEquals(1, pages.size());
    }

    @Test
    public void test_on_save_successful() {
        stub(PowerMockito.method(IncidentRegisterWrapperFragment.class, "getPhotoPathsData")).toReturn(new ArrayList<String>());
        incidentRegisterWrapperFragment.onSaveSuccessful(1l);

        PowerMockito.verifyStatic(times(1));
        Utils.showMessageByToast(incidentActivity, R.string.save_success, Toast.LENGTH_SHORT);
        Mockito.verify(incidentActivity, Mockito.times(1)).turnToFeature(any(), any(), any());

    }
}
