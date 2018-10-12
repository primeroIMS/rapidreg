package org.unicef.rapidreg.incident;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;

import junit.framework.Assert;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseActivity;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.event.LoadGBVIncidentFormEvent;
import org.unicef.rapidreg.event.SaveIncidentEvent;
import org.unicef.rapidreg.incident.incidentlist.IncidentListFragment;
import org.unicef.rapidreg.injection.component.ActivityComponent;
import org.unicef.rapidreg.utils.KeyboardUtils;
import org.unicef.rapidreg.utils.Utils;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.support.membermodification.MemberModifier.stub;
import static org.unicef.rapidreg.IntentSender.BUNDLE_EXTRA;
import static org.unicef.rapidreg.service.CaseService.CASE_ID;
import static org.unicef.rapidreg.service.IncidentService.INCIDENT_ID;
import static org.unicef.rapidreg.service.RecordService.AUDIO_FILE_PATH;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IncidentActivity.class, RecordActivity.class, DrawerLayout.class, Utils.class, EventBus.class, KeyboardUtils.class})
public class IncidentActivityTest {

    @Mock
    ActivityComponent activityComponent;

    @Mock
    DrawerLayout drawer;

    @Mock
    IncidentPresenter incidentPresenter;

    @Mock
    Feature currentFeature;

    @Mock
    IntentSender intentSender;

    @Mock
    EventBus eventBus;

    Resources resources = PowerMockito.mock(Resources.class);
    Bundle savedInstanceState = PowerMockito.mock(Bundle.class);
    Bundle bundleExtra = PowerMockito.mock(Bundle.class);

    @InjectMocks
    IncidentActivity incidentActivity = PowerMockito.spy(new IncidentActivity());

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        stub(PowerMockito.method(IncidentActivity.class, "getComponent")).toReturn(activityComponent);
        Intent intent = PowerMockito.mock(Intent.class);
        stub(PowerMockito.method(IncidentActivity.class, "getIntent")).toReturn(intent);
        when(intent.getBundleExtra(BUNDLE_EXTRA)).thenReturn(bundleExtra);
        stub(PowerMockito.method(IncidentActivity.class, "getResources")).toReturn(resources);
        PowerMockito.mockStatic(Utils.class);
        doNothing().when(Utils.class, "clearAudioFile", anyString());
        PowerMockito.mockStatic(EventBus.class);
        when(EventBus.getDefault()).thenReturn(eventBus);

        PowerMockito.mockStatic(KeyboardUtils.class);
        PowerMockito.doNothing().when(KeyboardUtils.class, "hideKeyboard", any(IncidentActivity.class));

    }

    private void supporting_code() {
        ColorStateList incidentColor = PowerMockito.mock(ColorStateList.class);
        try {
            PowerMockito.suppress(RecordActivity.class.getDeclaredMethod("onCreate", Bundle.class));
            PowerMockito.suppress(PowerMockito.method(IncidentActivity.class, "setNavSelectedMenu"));
            PowerMockito.suppress(PowerMockito.method(DrawerLayout.class, "closeDrawer", int.class));
            PowerMockito.doNothing().when(incidentActivity).turnToFeature(IncidentFeature.LIST, null, null);
            PowerMockito.doNothing().when(incidentActivity).turnToFeature(IncidentFeature.ADD_MINI, bundleExtra, null);
            PowerMockito.doNothing().when(incidentActivity).turnToFeature(IncidentFeature.DETAILS_MINI, bundleExtra, null);
            PowerMockito.doNothing().when(incidentActivity).showSyncFormDialog(anyString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // navigate to show incident list
    @Test
    public void test_on_create_show_incident_list() {
        supporting_code();
        when(incidentPresenter.isFormReady()).thenReturn(false);
        when(resources.getString(R.string.child_incident)).thenReturn(anyString());

        incidentActivity.onCreate(savedInstanceState);
        Mockito.verify(incidentActivity, times(1)).turnToFeature(IncidentFeature.LIST, null, null);
        Mockito.verify(incidentActivity, times(1)).showSyncFormDialog(any());
    }

    // navigate to incident MINI feature
    @Test
    public void test_on_create_show_incident_mini() {
        supporting_code();
        when(incidentPresenter.isFormReady()).thenReturn(true);
        when(bundleExtra.containsKey(CASE_ID)).thenReturn(true);

        incidentActivity.onCreate(savedInstanceState);
        Mockito.verify(incidentActivity, times(1)).turnToFeature(IncidentFeature.LIST, null, null);
        Mockito.verify(incidentActivity, times(1)).turnToFeature(IncidentFeature.ADD_MINI, bundleExtra, null);
    }

    // navigate to incident DETAIL feature
    @Test
    public void test_on_create_show_incident_detail() {
        supporting_code();
        when(incidentPresenter.isFormReady()).thenReturn(true);
        when(bundleExtra.containsKey(CASE_ID)).thenReturn(false);
        when(bundleExtra.containsKey(INCIDENT_ID)).thenReturn(true);

        incidentActivity.onCreate(savedInstanceState);
        Mockito.verify(incidentActivity, times(1)).turnToFeature(IncidentFeature.LIST, null, null);
        Mockito.verify(incidentActivity, times(1)).turnToFeature(IncidentFeature.DETAILS_MINI, bundleExtra, null);
    }

    @Test
    public void test_process_back_button_logout() {
        when(currentFeature.isListMode()).thenReturn(true);
        when(currentFeature.isDeleteMode()).thenReturn(false);

        try {
            PowerMockito.suppress(BaseActivity.class.getDeclaredMethod("logOut"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        incidentActivity.processBackButton();
        // we can not verify logout because is protected but we can verify else if is not reached
        Mockito.verify(currentFeature, times(0)).isEditMode();
    }

    @Test
    public void test_process_back_button_show_quit() {
        when(currentFeature.isListMode()).thenReturn(false);
        when(currentFeature.isDeleteMode()).thenReturn(false);
        when(currentFeature.isEditMode()).thenReturn(true);

        try {
            PowerMockito.suppress(IncidentActivity.class.getDeclaredMethod("showQuitDialog", int.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        incidentActivity.processBackButton();
        Mockito.verify(currentFeature, times(1)).isEditMode();
        // verify turnToFeature is not called instead of protected showQuitDialog
        Mockito.verify(incidentActivity, times(0)).turnToFeature(IncidentFeature.LIST, null, null);

    }

    @Test
    public void test_process_back_button_go_to_list() {
        when(currentFeature.isListMode()).thenReturn(false);
        when(currentFeature.isDeleteMode()).thenReturn(false);
        when(currentFeature.isEditMode()).thenReturn(false);
        PowerMockito.doNothing().when(incidentActivity).turnToFeature(IncidentFeature.LIST, null, null);

        incidentActivity.processBackButton();
        PowerMockito.verifyStatic();
        Utils.clearAudioFile(AUDIO_FILE_PATH);
        Mockito.verify(incidentActivity, times(1)).turnToFeature(IncidentFeature.LIST, null, null);

    }

    @Test
    public void test_nav_incident_action_show_quit() {
        when(currentFeature.isEditMode()).thenReturn(true);
        try {
            PowerMockito.suppress(IncidentActivity.class.getDeclaredMethod("showQuitDialog", int.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        incidentActivity.navIncidentAction();

        // verify turnToFeature is not called instead of protected showQuitDialog
        Mockito.verify(incidentActivity, times(0)).turnToFeature(IncidentFeature.LIST, null, null);
    }

    @Test
    public void test_nav_incident_action_go_to_list() {
        when(currentFeature.isEditMode()).thenReturn(false);
        PowerMockito.doNothing().when(incidentActivity).turnToFeature(IncidentFeature.LIST, null, null);

        incidentActivity.navIncidentAction();

        Mockito.verify(incidentActivity, times(1)).turnToFeature(IncidentFeature.LIST, null, null);
    }

    @Test
    public void test_nav_tracing_action_show_quit() {
        when(currentFeature.isEditMode()).thenReturn(true);
        try {
            PowerMockito.suppress(IncidentActivity.class.getDeclaredMethod("showQuitDialog", int.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        incidentActivity.navTracingAction();

        // verify showTracingActivity is not called instead of protected showQuitDialog
        Mockito.verify(intentSender, times(0)).showTracingActivity(incidentActivity, true);
    }

    @Test
    public void test_nav_tracing_action_go_to_tracing() {
        when(currentFeature.isEditMode()).thenReturn(false);
        PowerMockito.doNothing().when(intentSender).showTracingActivity(incidentActivity, true);

        incidentActivity.navTracingAction();

        Mockito.verify(intentSender, times(1)).showTracingActivity(incidentActivity, true);
    }





    @Test
    public void test_nav_case_action() {
        when(currentFeature.isEditMode()).thenReturn(true);
        try {
            PowerMockito.suppress(IncidentActivity.class.getDeclaredMethod("showQuitDialog", int.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        incidentActivity.navCaseAction();

        // verify showCasesActivity is not called instead of protected showQuitDialog
        Mockito.verify(intentSender, times(0)).showCasesActivity(incidentActivity, true, false);
    }

    @Test
    public void test_nav_case_action_go_to_cases() {
        when(currentFeature.isEditMode()).thenReturn(false);
        PowerMockito.doNothing().when(intentSender).showCasesActivity(incidentActivity, true, false);

        incidentActivity.navCaseAction();

        Mockito.verify(intentSender, times(1)).showCasesActivity(incidentActivity, true, false);
    }

    @Test
    public void test_search_form_not_ready() {
        when(incidentPresenter.isFormReady()).thenReturn(false);
        doNothing().when(incidentActivity).showSyncFormDialog(anyString());
        when(resources.getString(R.string.child_incident)).thenReturn("");

        incidentActivity.search();
        Mockito.verify(incidentActivity, times(1)).showSyncFormDialog(anyString());

    }

    @Test
    public void test_search_form_is_ready() {
        when(incidentPresenter.isFormReady()).thenReturn(true);
        PowerMockito.doNothing().when(incidentActivity).turnToFeature(IncidentFeature.SEARCH, null, null);

        incidentActivity.search();
        Mockito.verify(incidentActivity, times(1)).turnToFeature(IncidentFeature.SEARCH, null, null);
    }

    @Test
    public void test_save() {

        incidentActivity.save();

        Mockito.verify(eventBus, times(1)).postSticky(any(SaveIncidentEvent.class));
    }

    @Test
    public void test_send_sync_form_event() {
        PowerMockito.stub(PowerMockito.method(PrimeroAppConfiguration.class, "getCookie")).toReturn("");
        incidentActivity.sendSyncFormEvent();

        Mockito.verify(eventBus, times(1)).postSticky(any(LoadGBVIncidentFormEvent.class));
    }

    @Test
    @Ignore(" TODO fix stubbing of findFragmentByTag it reports wrong cast type")
    public void test_get_record_list_fragment() {
        FragmentManager fragmentManager = PowerMockito.mock(FragmentManager.class);
        PowerMockito.stub(PowerMockito.method(IncidentActivity.class, "getSupportFragmentManager")).toReturn(fragmentManager);
        Fragment fragment = new IncidentListFragment();
        doReturn(fragment).when(fragmentManager).findFragmentByTag(anyString());
        RecordListFragment recordListFragment = incidentActivity.getRecordListFragment();
        Assert.assertEquals(fragment, recordListFragment);
    }
}
