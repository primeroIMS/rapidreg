package org.unicef.rapidreg.incident.incidentlist;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

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
import org.unicef.rapidreg.incident.IncidentFeature;
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
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PrimeroApplication.class, IncidentListFragment.class, Utils.class})
public class IncidentListFragmentTest {
    @Mock
    IncidentListAdapter incidentListAdapter;

    @Mock
    IncidentListPresenter incidentListPresenter;

    @Mock
    AppRuntime appRuntime;

    @Spy
    @InjectMocks
    IncidentListFragment incidentListFragment= new IncidentListFragment();

    @Mock
    RecordActivity recordActivity;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        doNothing().when(incidentListPresenter).clearAudioFile();
        mockStatic(PrimeroApplication.class);
        mockStatic(Utils.class);
        stub(PowerMockito.method(PrimeroApplication.class, "getAppRuntime")).toReturn(appRuntime);
        stub(PowerMockito.method(IncidentListFragment.class, "getActivity")).toReturn(recordActivity);
        doNothing().when(Utils.class, "showMessageByToast", any(Context.class),anyInt(),anyInt());
    }

    @Test
    public void test_create_presenter() {
        assertEquals(incidentListPresenter, incidentListFragment.createPresenter());
    }

    @Test
    public void test_get_default_spinner_states() {
        assertEquals(4, incidentListFragment.getDefaultSpinnerStates().length);
    }

    @Test
    public void test_get_default_spinner_state_positions() {
        assertEquals(3, incidentListFragment.getDefaultSpinnerStatePosition());
    }

    @Test
    public void test_create_record_list_adapter() {
        RecordListAdapter returnedIncidentListAdapter = incidentListFragment.createRecordListAdapter();
        assertEquals(incidentListAdapter, returnedIncidentListAdapter);
    }

    @Test
    // isFormReady false
    // isIncidentFormSyncFail true
    public void test_on_incident_add_clicked_1() {
        Resources resources = PowerMockito.mock(Resources.class);
        when(incidentListPresenter.isFormReady()).thenReturn(false);
        when(appRuntime.isIncidentFormSyncFail()).thenReturn(true);
        doReturn(resources).when(incidentListFragment).getResources();
        when(resources.getString(anyInt())).thenReturn("");
        doNothing().when(incidentListFragment).showSyncFormDialog(any());
        incidentListFragment.onIncidentAddClicked();
        verify(incidentListPresenter, times(1)).clearAudioFile();
        verify(incidentListFragment, times(1)).showSyncFormDialog(anyString());
    }

    @Test
    // isFormReady false
    // isIncidentFormSyncFail false
    public void test_on_incident_add_clicked_2() {
        Resources resources = PowerMockito.mock(Resources.class);
        when(incidentListPresenter.isFormReady()).thenReturn(false);
        when(appRuntime.isIncidentFormSyncFail()).thenReturn(false);
        when(resources.getString(anyInt())).thenReturn("");

        incidentListFragment.onIncidentAddClicked();
        verify(incidentListPresenter, times(1)).clearAudioFile();
        verifyStatic(Utils.class);
        Utils.showMessageByToast(recordActivity, R.string.forms_is_syncing_msg, Toast.LENGTH_SHORT);
    }

    @Test
    // isFormReady true
    public void test_on_incident_add_clicked_3() {
        Resources resources = PowerMockito.mock(Resources.class);
        when(incidentListPresenter.isFormReady()).thenReturn(true);

        incidentListFragment.onIncidentAddClicked();
        verify(incidentListPresenter, times(1)).clearAudioFile();
        verify(recordActivity, times(1)).turnToFeature(IncidentFeature.ADD_MINI, null, null);
    }
}
