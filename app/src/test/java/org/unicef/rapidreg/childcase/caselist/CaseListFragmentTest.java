package org.unicef.rapidreg.childcase.caselist;

import android.app.Fragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.model.User;


import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PrimeroAppConfiguration.class, Fragment.class, RecordActivity.class, CaseListFragment.class})
public class CaseListFragmentTest {

    @Mock
    CaseListAdapter caseListAdapter;

    @Mock
    CaseListPresenter caseListPresenter;

    @Mock
    User user;

    Fragment fragment;

    CaseActivity caseActivity;

    @Mock
    RecordActivity recordActivity;

    @InjectMocks
    CaseListFragment caseListFragment = new CaseListFragment();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockStatic(PrimeroAppConfiguration.class);
        doNothing().when(caseListPresenter).clearAudioFile();
        when(caseListPresenter.isFormReady()).thenReturn(true);
        when(PrimeroAppConfiguration.getCurrentUser()).thenReturn(user);
        stub(PowerMockito.method(CaseListFragment.class, "getActivity")).toReturn(recordActivity);
    }

    @Test
    public void test_get_default_spinner_states() {
        assertEquals(4, caseListFragment.getDefaultSpinnerStates().length);
    }

    @Test
    public void test_get_default_spinner_state_positions() {
        assertEquals(3, caseListFragment.getDefaultSpinnerStatePosition());
    }

    @Test
    public void test_create_record_list_adapter() {
        RecordListAdapter returnedCaseListAdapter = caseListFragment.createRecordListAdapter();
        assertEquals(caseListAdapter, returnedCaseListAdapter);
    }

    @Test
    public void test_on_gbv_case_add_clicked() {
        when(user.getRoleType()).thenReturn(User.Role.GBV);
        //fragment=PowerMockito.mock(Fragment.class);
        //recordActivity=PowerMockito.mock(RecordActivity.class);
        //doReturn(recordActivity).when(fragment).getActivity();
        doNothing().when(recordActivity).turnToFeature(any(), any(), any());
        caseListFragment.onCaseAddClicked();
        try {
            verifyPrivate(caseListFragment, times(1)).invoke("onGBVCaseAddClicked");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test_on_cp_case_add_clicked() {
        when(user.getRoleType()).thenReturn(User.Role.CP);
        fragment=PowerMockito.mock(Fragment.class);
        recordActivity=PowerMockito.mock(RecordActivity.class);
        doReturn(recordActivity).when(fragment).getActivity();
        doNothing().when(recordActivity).turnToFeature(any(), any(), any());
        caseListFragment.onCaseAddClicked();
        try {
            verifyPrivate(caseListFragment, times(1)).invoke("onCPCaseAddClicked");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test_on_default_case_add_clicked() {
        when(user.getRoleType()).thenReturn(User.Role.EMPTY_ROLE);
        fragment=PowerMockito.mock(Fragment.class);
        recordActivity=PowerMockito.mock(RecordActivity.class);
        doReturn(recordActivity).when(fragment).getActivity();
        doNothing().when(recordActivity).turnToFeature(any(), any(), any());
        caseListFragment.onCaseAddClicked();
        try {
            verifyPrivate(caseListFragment, times(1)).invoke("onCPCaseAddClicked");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
