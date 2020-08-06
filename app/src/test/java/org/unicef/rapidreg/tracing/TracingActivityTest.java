package org.unicef.rapidreg.tracing;

import android.app.FragmentManager;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import android.widget.Toast;

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
import org.unicef.rapidreg.event.LoadTracingFormEvent;
import org.unicef.rapidreg.event.SaveTracingEvent;
import org.unicef.rapidreg.injection.component.ActivityComponent;
import org.unicef.rapidreg.tracing.tracinglist.TracingListFragment;
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
import static org.unicef.rapidreg.service.RecordService.AUDIO_FILE_PATH;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TracingActivity.class, PrimeroAppConfiguration.class, EventBus.class, Utils.class, RecordActivity.class, KeyboardUtils.class})
public class TracingActivityTest {

    @Mock
    ActivityComponent activityComponent;

    @Mock
    DrawerLayout drawer;

    @Mock
    EventBus eventBus;

    @Mock
    Feature currentFeature;

    @Mock
    IntentSender intentSender;

    @InjectMocks
    TracingActivity tracingActivity = PowerMockito.spy(new TracingActivity());

    Resources resources = PowerMockito.mock(Resources.class);
    Bundle savedInstanceState = PowerMockito.mock(Bundle.class);
    Bundle bundleExtra = PowerMockito.mock(Bundle.class);

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        doReturn(activityComponent).when(tracingActivity).getComponent();
        PowerMockito.mockStatic(EventBus.class);
        when(EventBus.getDefault()).thenReturn(eventBus);
        PowerMockito.mockStatic(Utils.class);
        doNothing().when(Utils.class, "clearAudioFile", anyString());
        PowerMockito.suppress(TracingActivity.class.getDeclaredMethod("showQuitDialog", int.class));
        stub(PowerMockito.method(RecordActivity.class, "setShowHideSwitcherToShowState")).toReturn(null);
        PowerMockito.doNothing().when(tracingActivity).turnToFeature(TracingFeature.LIST, null, null);

        PowerMockito.mockStatic(KeyboardUtils.class);
        PowerMockito.doNothing().when(KeyboardUtils.class, "hideKeyboard", any(TracingActivity.class));
    }

    @Test
    public void test_on_create() {

        try {
            PowerMockito.suppress(RecordActivity.class.getDeclaredMethod("onCreate", Bundle.class));
            PowerMockito.suppress(PowerMockito.method(TracingActivity.class, "setNavSelectedMenu"));
            PowerMockito.suppress(PowerMockito.method(DrawerLayout.class, "closeDrawer", int.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        tracingActivity.onCreate(savedInstanceState);
        Mockito.verify(tracingActivity, times(1)).turnToFeature(TracingFeature.LIST, null, null);
    }

    @Test
    public void test_send_sync_form_event() {
        PowerMockito.stub(PowerMockito.method(PrimeroAppConfiguration.class, "getCookie")).toReturn("");

        tracingActivity.sendSyncFormEvent();
        Mockito.verify(eventBus, times(1)).postSticky(any(LoadTracingFormEvent.class));
    }

    @Test
    @Ignore("TODO fix stubbing of findFragmentByTag it reports wrong cast type")
    public void test_get_record_list_fragment() {
        FragmentManager fragmentManager = PowerMockito.mock(FragmentManager.class);
        PowerMockito.stub(PowerMockito.method(TracingActivity.class, "getSupportFragmentManager")).toReturn(fragmentManager);
        Fragment fragment = new TracingListFragment();
        doReturn(fragment).when(fragmentManager).findFragmentByTag(anyString());

        RecordListFragment recordListFragment = tracingActivity.getRecordListFragment();
        Assert.assertEquals(fragment, recordListFragment);
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

        tracingActivity.processBackButton();
        // we can not verify logout because is protected but we can verify else if is not reached
        Mockito.verify(currentFeature, times(0)).isEditMode();
    }

    @Test
    public void test_process_back_button_show_quit() {
        when(currentFeature.isListMode()).thenReturn(false);
        when(currentFeature.isDeleteMode()).thenReturn(false);
        when(currentFeature.isEditMode()).thenReturn(true);

        try {
            PowerMockito.suppress(TracingActivity.class.getDeclaredMethod("showQuitDialog", int.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        tracingActivity.processBackButton();
        Mockito.verify(currentFeature, times(1)).isEditMode();
        // verify turnToFeature is not called instead of protected showQuitDialog
        Mockito.verify(tracingActivity, times(0)).turnToFeature(TracingFeature.LIST, null, null);
    }

    @Test
    public void test_process_back_button_go_to_list() {
        when(currentFeature.isListMode()).thenReturn(false);
        when(currentFeature.isDeleteMode()).thenReturn(false);
        when(currentFeature.isEditMode()).thenReturn(false);
        PowerMockito.doNothing().when(tracingActivity).turnToFeature(TracingFeature.LIST, null, null);

        tracingActivity.processBackButton();
        //PowerMockito.verifyStatic();
        Utils.clearAudioFile(AUDIO_FILE_PATH);
        Mockito.verify(tracingActivity, times(1)).turnToFeature(TracingFeature.LIST, null, null);
    }

    @Test
    public void test_nav_case_action() {
        when(currentFeature.isEditMode()).thenReturn(true);

        tracingActivity.navCaseAction();

        // verify showCasesActivity is not called instead of protected showQuitDialog
        Mockito.verify(intentSender, times(0)).showCasesActivity(tracingActivity, true, false);
    }

    @Test
    public void test_nav_case_action_go_to_cases() {
        when(currentFeature.isEditMode()).thenReturn(false);
        PowerMockito.doNothing().when(intentSender).showCasesActivity(tracingActivity, true, false);

        tracingActivity.navCaseAction();

        Mockito.verify(intentSender, times(1)).showCasesActivity(tracingActivity, true, false);
    }

    @Test
    public void test_nav_tracing_action_show_quit() {
        when(currentFeature.isEditMode()).thenReturn(true);

        tracingActivity.navTracingAction();

        // verify turnToFeature is not called instead of protected showQuitDialog
        Mockito.verify(tracingActivity, times(0)).turnToFeature(TracingFeature.LIST, null, null);
    }


    @Test
    public void test_nav_tracing_action_go_to_tracing() {
        when(currentFeature.isEditMode()).thenReturn(false);
        PowerMockito.doNothing().when(intentSender).showTracingActivity(tracingActivity, true);

        tracingActivity.navTracingAction();

        Mockito.verify(tracingActivity, times(1)).turnToFeature(TracingFeature.LIST, null, null);
    }

    @Test
    public void test_nav_incident_action_show_quit() {
        when(currentFeature.isEditMode()).thenReturn(true);
        try {
            PowerMockito.suppress(TracingActivity.class.getDeclaredMethod("showQuitDialog", int.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        tracingActivity.navIncidentAction();

        // verify showIncidentActivity is not called instead of protected showQuitDialog
        Mockito.verify(intentSender, times(0)).showIncidentActivity(tracingActivity, true);
    }

    @Test
    public void test_nav_incident_action_go_to_list() {
        when(currentFeature.isEditMode()).thenReturn(false);
        PowerMockito.doNothing().when(intentSender).showIncidentActivity(tracingActivity, true);

        tracingActivity.navIncidentAction();

        Mockito.verify(intentSender, times(1)).showIncidentActivity(tracingActivity, true);
    }

    @Test
    public void test_search() {
        PowerMockito.doNothing().when(tracingActivity).turnToFeature(TracingFeature.SEARCH, null, null);

        tracingActivity.search();

        Mockito.verify(tracingActivity, times(1)).turnToFeature(TracingFeature.SEARCH, null, null);
    }

    @Test
    public void test_save() {

        tracingActivity.save();

        Mockito.verify(eventBus, times(1)).postSticky(any(SaveTracingEvent.class));
    }

    @Test
    public void test_promote_sync_forms_error() {
        tracingActivity.promoteSyncFormsError();

        PowerMockito.verifyStatic(Utils.class, times(1));
        Utils.showMessageByToast(tracingActivity, R.string.sync_forms_error, Toast.LENGTH_SHORT);
    }
}
