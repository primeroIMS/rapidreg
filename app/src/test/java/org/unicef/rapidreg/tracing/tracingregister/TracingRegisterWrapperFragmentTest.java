package org.unicef.rapidreg.tracing.tracingregister;

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
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterWrapperFragment;
import org.unicef.rapidreg.event.SaveTracingEvent;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.tracing.TracingActivity;
import org.unicef.rapidreg.tracing.TracingFeature;
import org.unicef.rapidreg.tracing.tracingphoto.TracingPhotoAdapter;
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

@RunWith(PowerMockRunner.class)
@PrepareForTest({TracingRegisterWrapperFragment.class, Utils.class})
public class TracingRegisterWrapperFragmentTest {

    @Mock
    TracingRegisterPresenter tracingRegisterPresenter;

    @Mock
    TracingPhotoAdapter tracingPhotoAdapter;

    @Mock
    FragmentComponent fragmentComponent;

    @Mock
    TracingActivity tracingActivity;

    @Mock
    RecordRegisterAdapter recordRegisterAdapter;

    @Mock
    RecordPhotoAdapter recordPhotoAdapter;

    @Mock
    RecordForm form;

    @Mock
    List<Section> sections;

    @Mock
    Section section;

    @Mock
    Bundle arguments;

    @InjectMocks
    TracingRegisterWrapperFragment tracingRegisterWrapperFragment = PowerMockito.spy(new TracingRegisterWrapperFragment());

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(Utils.class);
        doNothing().when(Utils.class, "showMessageByToast", any(Context.class),anyInt(),anyInt());
        stub(PowerMockito.method(TracingRegisterWrapperFragment.class, "getCurrentPhotoAdapter")).toReturn(recordPhotoAdapter);
        stub(PowerMockito.method(TracingRegisterWrapperFragment.class, "getComponent")).toReturn(fragmentComponent);
        stub(PowerMockito.method(TracingRegisterWrapperFragment.class, "getActivity")).toReturn(tracingActivity);
        stub(PowerMockito.method(TracingRegisterWrapperFragment.class, "getArguments")).toReturn(arguments);
        stub(PowerMockito.method(TracingRegisterWrapperFragment.class, "getFieldValueVerifyResult")).toReturn(new ItemValuesMap());
    }

    @Test
    public void test_create_presenter() {
        assertEquals(tracingRegisterPresenter, tracingRegisterWrapperFragment.createPresenter());
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
        tracingRegisterWrapperFragment.onCreateView(inflater, viewGroup, bundle);
        verify(fragmentComponent, times(1)).inject(tracingRegisterWrapperFragment);
    }

    @Test
    public void test_create_record_photo_adapter() {
        tracingRegisterWrapperFragment.createRecordPhotoAdapter();
        verify(tracingPhotoAdapter, times(1)).setItems(any());
    }

    @Test
    public void test_save_tracing() {
        ItemValuesMap recordRegisterData = PowerMockito.mock(ItemValuesMap.class);
        stub(PowerMockito.method(TracingMiniFormFragment.class, "getRecordRegisterData")).toReturn(recordRegisterData);
        when(recordRegisterAdapter.getItemValues()).thenReturn(new ItemValuesMap());
        tracingRegisterWrapperFragment.saveTracing(new SaveTracingEvent());
        verify(tracingRegisterPresenter, times(1)).saveRecord(any(), any(), any());
    }

    @Test
    public void test_on_edit_clicked() {
        List<String> allItems = new ArrayList<String>();
        when(recordPhotoAdapter.getAllItems()).thenReturn(allItems);
        tracingRegisterWrapperFragment.onEditClicked();
        verify(tracingActivity, times(1)).turnToFeature(any(TracingFeature.class), any(Bundle.class), any());
    }

    @Test
    public void test_on_init_item_values() throws IllegalAccessException {
        Whitebox.setInternalState(tracingRegisterWrapperFragment, "recordPhotoPageChangeListener", new RecordPhotoPageChangeListener());
        tracingRegisterWrapperFragment.initItemValues();
        verify(tracingRegisterWrapperFragment, times(1)).setRecordRegisterData(any());
        verify(tracingRegisterWrapperFragment, times(1)).setFieldValueVerifyResult(any());
    }

    @Test
    public void test_on_init_form_data() {
        when(tracingRegisterPresenter.getTemplateForm()).thenReturn(form);
        tracingRegisterWrapperFragment.initFormData();
        verify(tracingRegisterPresenter, times(1)).getTemplateForm();
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
        List<String> allItems = new ArrayList<String>();
        when(recordPhotoAdapter.getAllItems()).thenReturn(allItems);

        FragmentPagerItems pages = tracingRegisterWrapperFragment.getPages();

        assertNotNull(pages);
        Assert.assertThat(pages, CoreMatchers.instanceOf(FragmentPagerItems.class));
        assertEquals(1, pages.size());
    }

    @Test
    public void test_on_save_successful() {
        stub(PowerMockito.method(TracingRegisterWrapperFragment.class, "getPhotoPathsData")).toReturn(new ArrayList<String>());
        tracingRegisterWrapperFragment.onSaveSuccessful(1l);

        PowerMockito.verifyStatic(times(1));
        Utils.showMessageByToast(tracingActivity, R.string.save_success, Toast.LENGTH_SHORT);
        Mockito.verify(tracingActivity, Mockito.times(1)).turnToFeature(any(), any(), any());
    }
}
