package org.unicef.rapidreg.tracing.tracingregister;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.greenrobot.eventbus.EventBus;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.event.SaveTracingEvent;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.tracing.TracingActivity;
import org.unicef.rapidreg.tracing.TracingFeature;
import org.unicef.rapidreg.tracing.tracingphoto.TracingPhotoAdapter;
import org.unicef.rapidreg.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TracingMiniFormFragment.class, RecordRegisterFragment.class, Utils.class, EventBus.class})
public class TracingMiniFormFragmentTest {

    @Mock
    FragmentComponent fragmentComponent;

    @Mock
    TracingActivity tracingActivity;

    @Mock
    Bundle arguments;

    @Mock
    TracingPhotoAdapter tracingPhotoAdapter;

    @Mock
    TracingRegisterPresenter tracingRegisterPresenter;

    @Mock
    FloatingActionButton editButton;

    @Mock
    RecordRegisterAdapter recordRegisterAdapter;

    @Mock
    Feature featureMock;

    @Mock
    TextView topInfoMessage;

    @InjectMocks
    TracingMiniFormFragment tracingMiniFormFragment = PowerMockito.spy(new TracingMiniFormFragment());

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        stub(PowerMockito.method(TracingMiniFormFragment.class, "getComponent")).toReturn(fragmentComponent);
        stub(PowerMockito.method(TracingMiniFormFragment.class, "getActivity")).toReturn(tracingActivity);
        stub(PowerMockito.method(TracingMiniFormFragment.class, "getArguments")).toReturn(arguments);
        mockStatic(Utils.class);
        PowerMockito.doNothing().when(Utils.class, "showMessageByToast", any(Context.class),anyInt(),anyInt());
    }

    @Test
    public void test_on_create_view() {
        LayoutInflater inflater = PowerMockito.mock(LayoutInflater.class);
        ViewGroup container = PowerMockito.mock(ViewGroup.class);
        Bundle savedInstanceState = PowerMockito.mock(Bundle.class);
        tracingMiniFormFragment.onCreateView(inflater, container, savedInstanceState);
        verify(fragmentComponent, times(1)).inject(tracingMiniFormFragment);
        verify(inflater, times(1)).inflate(R.layout.fragment_register, container, false);

    }

    @Test
    public void test_create_record_register_adapter() {
        int position = 0;
        List<Field> fields = new ArrayList<Field>();

        // Mock protected inherited method
        try {
            PowerMockito.doNothing().when(tracingMiniFormFragment, "addProfileFieldForDetailsPage", anyInt(), anyString(), any());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> photosPaths = new ArrayList<>();
        PowerMockito.stub(PowerMockito.method(TracingRegisterPresenter.class, "getDefaultPhotoPaths")).toReturn(photosPaths);
        ItemValuesMap itemValues = PowerMockito.mock(ItemValuesMap.class);
        PowerMockito.stub(PowerMockito.method(TracingMiniFormFragment.class, "getFieldValueVerifyResult")).toReturn(itemValues);
        RecordRegisterAdapter recordRegisterAdapter = tracingMiniFormFragment.createRecordRegisterAdapter();

        Assert.assertThat(recordRegisterAdapter, CoreMatchers.instanceOf(RecordRegisterAdapter.class));
        verify(tracingPhotoAdapter, times(1)).setItems(photosPaths);
        assertEquals(tracingPhotoAdapter, recordRegisterAdapter.getPhotoAdapter());
        assertEquals(itemValues, recordRegisterAdapter.getFieldValueVerifyResult());
    }

    @Test
    @Ignore(" TODO fix mocking of EventBus.getDefault()")
    public void test_on_start() {
        doNothing().when((MvpFragment)tracingMiniFormFragment).onStart();
        PowerMockito.mockStatic(EventBus.class);
        EventBus eventBus = PowerMockito.mock(EventBus.class);
        stub(PowerMockito.method(EventBus.class, "getDefault")).toReturn(eventBus);
        tracingMiniFormFragment.onStart();
        verify(eventBus, times(1)).register(tracingMiniFormFragment);
    }

    @Test
    @Ignore(" TODO fix mocking of EventBus.getDefault()")
    public void test_on_stop() {
        doNothing().when((MvpFragment)tracingMiniFormFragment).onStop();
        PowerMockito.mockStatic(EventBus.class);
        EventBus eventBus = PowerMockito.mock(EventBus.class);
        stub(PowerMockito.method(EventBus.class, "getDefault")).toReturn(eventBus);
        tracingMiniFormFragment.onStop();
        verify(eventBus, times(1)).register(tracingMiniFormFragment);
    }

    @Test
    public void test_on_init_view_content() {
//        doNothing().when((RecordRegisterFragment)tracingMiniFormFragment).onInitViewContent();
//        stub(PowerMockito.method(TracingFeature.class, "isDetailMode")).toReturn(true);
        Whitebox.setInternalState(tracingMiniFormFragment, "fieldList", PowerMockito.mock(RecyclerView.class));
        when(featureMock.isDetailMode()).thenReturn(true);
        when(tracingActivity.getCurrentFeature()).thenReturn(featureMock);
        doNothing().when((RecordRegisterFragment)tracingMiniFormFragment).addProfileFieldForDetailsPage(anyInt(), anyList());
        tracingMiniFormFragment.onInitViewContent();
        verify((RecordRegisterFragment)tracingMiniFormFragment, times(1)).onInitViewContent();
        verify(editButton, times(1)).setVisibility(View.VISIBLE);
    }

    @Test
    public void test_save_tracing() {
        ItemValuesMap recordRegisterData = PowerMockito.mock(ItemValuesMap.class);
        stub(PowerMockito.method(TracingMiniFormFragment.class, "getRecordRegisterData")).toReturn(recordRegisterData);
        when(recordRegisterAdapter.getItemValues()).thenReturn(new ItemValuesMap());
        tracingMiniFormFragment.saveTracing(new SaveTracingEvent());
        verify(tracingRegisterPresenter, times(1)).saveRecord(any(), any(), any());
    }

    @Test
    public void test_on_save_successful() {
        tracingMiniFormFragment.onSaveSuccessful(1);
        PowerMockito.verifyStatic(times(1));
        Utils.showMessageByToast(tracingActivity, R.string.save_success, Toast.LENGTH_SHORT);
        verify(tracingActivity, times(1)).turnToFeature(any(TracingFeature.class), any(Bundle.class), any());
    }

    @Test
    public void test_on_edit_clicked() {
        List<String> paths = new ArrayList<String>();
        stub(PowerMockito.method(TracingMiniFormFragment.class, "getPhotoPathsData")).toReturn(paths);

        tracingMiniFormFragment.onEditClicked();
        verify(tracingActivity, times(1)).turnToFeature(any(TracingFeature.class), any(Bundle.class), any());
    }
}
