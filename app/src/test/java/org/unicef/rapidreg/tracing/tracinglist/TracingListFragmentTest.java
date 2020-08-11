package org.unicef.rapidreg.tracing.tracinglist;

import android.content.Context;
import android.content.res.Resources;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.AppRuntime;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.utils.Utils;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

@RunWith(PowerMockRunner.class)

@PrepareForTest({PrimeroApplication.class, Utils.class, TracingListFragment.class})
public class TracingListFragmentTest {

    @Mock
    TracingListAdapter tracingListAdapter;

    @Mock
    TracingListPresenter tracingListPresenter;

    @Mock
    AppRuntime appRuntime;

    @InjectMocks
    TracingListFragment tracingListFragment = spy(new TracingListFragment());


    @Mock
    RecordActivity recordActivity;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        doNothing().when(tracingListPresenter).clearAudioFile();
        mockStatic(PrimeroApplication.class);
        mockStatic(Utils.class);
        stub(PowerMockito.method(PrimeroApplication.class, "getAppRuntime")).toReturn(appRuntime);
        stub(PowerMockito.method(TracingListFragment.class, "getActivity")).toReturn(recordActivity);
        doNothing().when(Utils.class, "showMessageByToast", any(Context.class),anyInt(),anyInt());
    }


    @Test
    public void test_get_default_spinner_states() {
        assertEquals(2, tracingListFragment.getDefaultSpinnerStates().length);
    }

    @Test
    public void test_get_default_spinner_state_positions() {
        assertEquals(1, tracingListFragment.getDefaultSpinnerStatePosition());
    }

    @Test
    public void test_create_record_list_adapter() {
        RecordListAdapter returnedTracingListAdapter = tracingListFragment.createRecordListAdapter();
        assertEquals(tracingListAdapter, returnedTracingListAdapter);
    }

    @Test
    // isFormReady false
    // isIncidentFormSyncFail true
    public void test_on_tracing_add_clicked_1() {
        Resources resources = PowerMockito.mock(Resources.class);
        when(tracingListPresenter.isFormReady()).thenReturn(false);
        when(appRuntime.isTracingFormSyncFail()).thenReturn(true);

        doReturn(resources).when(tracingListFragment).getResources();
        when(resources.getString(R.string.tracing_request)).thenReturn("");
        doNothing().when(tracingListFragment).showSyncFormDialog(any());
        tracingListFragment.onTracingAddClicked();
        verify(tracingListPresenter, times(1)).clearAudioFile();
        verify(tracingListFragment, times(1)).showSyncFormDialog(anyString());

    }
}
