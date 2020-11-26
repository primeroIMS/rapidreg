package org.unicef.rapidreg.childcase.caseregister;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterWrapperFragment;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.CaseFeature;
import org.unicef.rapidreg.childcase.casephoto.CasePhotoAdapter;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RecordPhotoAdapter.class, FragmentActivity.class, Fragment.class, RecordActivity.class, Bundle.class, Utils.class, CaseRegisterWrapperFragment.class})
public class CaseRegisterWrapperFragmentTest {
    @Mock
    CaseRegisterPresenter caseRegisterPresenter;

    @Mock
    CasePhotoAdapter casePhotoAdapter;

    @Mock
    RecordPhotoAdapter recordPhotoAdapter;

    @Mock
    ItemValuesMap itemValues;

    @Mock
    SaveCaseEvent event;
    @Mock
    FloatingActionButton editButton;
    @Mock
    TextView topInfoMessage;
    @Mock
    RecordForm recordForm;
    @Mock
    RecordForm form;
    @Mock
    List<Section> sections;
    @Mock
    Section section;

    @Mock
    FragmentComponent fragmentComponent;

    @Mock
    CaseActivity caseActivity;
    @Mock
    Bundle bundle;

    Fragment fragment;
    RecordActivity recordActivity;
    FragmentActivity fragmentActivity;

    @InjectMocks
    CaseRegisterWrapperFragment caseRegisterWrapperFragment = new CaseRegisterWrapperFragment();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        fragment = PowerMockito.mock(Fragment.class);
        recordActivity = PowerMockito.mock(RecordActivity.class);
        fragmentActivity = PowerMockito.mock(FragmentActivity.class);

        when(recordActivity.getCurrentFeature()).thenReturn(CaseFeature.EDIT_MINI);
        when(caseRegisterPresenter.getCaseIsInvalidated(anyString())).thenReturn(true);
        when(recordPhotoAdapter.getAllItems()).thenReturn(new ArrayList<String>(){{add("test");}});
        PowerMockito.mockStatic(Utils.class);
        doNothing().when(Utils.class, "showMessageByToast", any(Context.class),anyInt(),anyInt());

        stub(PowerMockito.method(CaseRegisterWrapperFragment.class, "getPhotoPathsData")).toReturn(Collections.emptyList());
        stub(PowerMockito.method(CaseRegisterWrapperFragment.class, "getCurrentPhotoAdapter")).toReturn(recordPhotoAdapter);
        stub(PowerMockito.method(CaseRegisterWrapperFragment.class, "getFieldValueVerifyResult")).toReturn(new ItemValuesMap());
        stub(PowerMockito.method(CaseRegisterWrapperFragment.class, "getActivity")).toReturn(recordActivity);
        stub(PowerMockito.method(CaseRegisterWrapperFragment.class, "getArguments")).toReturn(bundle);
        stub(PowerMockito.method(CaseRegisterWrapperFragment.class, "getComponent")).toReturn(fragmentComponent);

    }

    @Test
    public void test_create_presenter() {
        assertNotNull(caseRegisterWrapperFragment);
        assertEquals(caseRegisterPresenter, caseRegisterWrapperFragment.createPresenter());
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
        caseRegisterWrapperFragment.onCreateView(inflater, viewGroup, bundle);
        verify(fragmentComponent, times(1)).inject(caseRegisterWrapperFragment);
    }

    @Test
    public void test_create_record_photo_adapter() {
        caseRegisterWrapperFragment.createRecordPhotoAdapter();
        verify(casePhotoAdapter, times(1)).setItems(any());
    }

    @Test
    public void test_save_case() {
        caseRegisterWrapperFragment.saveCase(event);
        Mockito.verify(caseRegisterPresenter, Mockito.times(1)).saveRecord(any(), any(), any());
    }

    @Test
    public void test_floating_action_button() {
        caseRegisterWrapperFragment.initFloatingActionButton();
        verify(editButton, times(1)).setVisibility(anyInt());
        //assertEquals(View.GONE, editButton.getVisibility());
    }

    @Test
    public void test_init_top_warning() {
        when(caseRegisterPresenter.getCaseIsInvalidated("1")).thenReturn(true);
        caseRegisterWrapperFragment.initTopWarning();
        assertEquals(topInfoMessage.getVisibility(), View.VISIBLE);
    }

    @Test
    public void test_on_edit_clicked() {
        stub(PowerMockito.method(CaseRegisterWrapperFragment.class, "getActivity")).toReturn(caseActivity);
        caseRegisterWrapperFragment.onEditClicked();
        Mockito.verify(caseActivity, Mockito.times(1)).turnToFeature(any(), any(), any());
    }

    @Test
    public void test_init_form_data() {
        when(caseRegisterPresenter.getTemplateForm()).thenReturn(recordForm);
        caseRegisterWrapperFragment.initFormData();
        Mockito.verify(recordForm, Mockito.times(1)).getSections();
    }

    @Test
    public void test_get_pages() {
        when(form.getSections()).thenReturn(sections);
        when(sections.size()).thenReturn(1);
        Iterator<Section> sectionsIterator = PowerMockito.mock(Iterator.class);
        when(sections.iterator()).thenReturn(sectionsIterator);
        when(sectionsIterator.next()).thenReturn(section);
        Map names = new HashMap<String, String>();
        names.put("test", "test");
        when(section.getName()).thenReturn(names);
        when(sectionsIterator.hasNext()).thenReturn(true, false);
        FragmentPagerItems pages = caseRegisterWrapperFragment.getPages();
        assertNotNull(pages);
        Assert.assertThat(pages, CoreMatchers.instanceOf(FragmentPagerItems.class));
        assertEquals(1, pages.size());
    }

    @Test
    public void test_on_save_successful() {
        doReturn(new ArrayList<String>()).when(caseRegisterPresenter).getPhotoPathsByRecordId(1l);
        when(caseRegisterPresenter.getCaseType()).thenReturn(CaseRegisterPresenter.MODULE_CASE_CP);
        stub(PowerMockito.method(CaseRegisterWrapperFragment.class, "getPhotoPathsData")).toReturn(new ArrayList<String>());
        caseRegisterWrapperFragment.onSaveSuccessful(1l);
        PowerMockito.verifyStatic(Utils.class, times(1));
        Utils.showMessageByToast(recordActivity, R.string.save_success, Toast.LENGTH_SHORT);
        Mockito.verify(caseRegisterPresenter, Mockito.times(2)).getCaseType();
        Mockito.verify(recordActivity, Mockito.times(1)).turnToFeature(any(), any(), any());
    }
}
