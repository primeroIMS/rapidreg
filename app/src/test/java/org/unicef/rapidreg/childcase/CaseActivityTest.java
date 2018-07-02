package org.unicef.rapidreg.childcase;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import junit.framework.Assert;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.AppRuntime;
import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseActivity;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.childcase.caselist.CaseListFragment;
import org.unicef.rapidreg.childcase.casesearch.CaseSearchFragment;
import org.unicef.rapidreg.event.LoadCPCaseFormEvent;
import org.unicef.rapidreg.event.RedirectIncidentEvent;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.injection.component.ActivityComponent;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.utils.Utils;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.support.membermodification.MemberModifier.stub;
import static org.unicef.rapidreg.IntentSender.BUNDLE_EXTRA;
import static org.unicef.rapidreg.service.RecordService.AUDIO_FILE_PATH;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CaseActivity.class, EventBus.class, PrimeroAppConfiguration.class, Utils.class, RecordActivity.class, PrimeroApplication.class})
public class CaseActivityTest {

    @Mock
    ActivityComponent activityComponent;

    Bundle bundleExtra = PowerMockito.mock(Bundle.class);

    @Mock
    EventBus eventBus;

    @Mock
    DrawerLayout drawer;

    @Mock
    User user;

    @Mock
    Feature currentFeature;

    @Mock
    IntentSender intentSender;

    @Mock
    CasePresenter casePresenter;

    @Mock
    AppRuntime appRuntime;

    @InjectMocks
    CaseActivity caseActivity = PowerMockito.spy(new CaseActivity());

    Bundle savedInstanceState = PowerMockito.mock(Bundle.class);

    RedirectIncidentEvent redirectIncidentEvent = PowerMockito.mock(RedirectIncidentEvent.class);

    Intent intent = PowerMockito.mock(Intent.class);

    Resources resources = PowerMockito.mock(Resources.class);

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        stub(PowerMockito.method(CaseActivity.class, "getComponent")).toReturn(activityComponent);
        stub(PowerMockito.method(CaseActivity.class, "getIntent")).toReturn(intent);
        when(intent.getBundleExtra(BUNDLE_EXTRA)).thenReturn(bundleExtra);
        PowerMockito.suppress(PowerMockito.method(DrawerLayout.class, "openDrawer", int.class));
        PowerMockito.suppress(PowerMockito.method(DrawerLayout.class, "closeDrawer", int.class));
        PowerMockito.stub(PowerMockito.method(PrimeroAppConfiguration.class, "getCookie")).toReturn("");
        stub(PowerMockito.method(CaseActivity.class, "getResources")).toReturn(resources);
        PowerMockito.mockStatic(EventBus.class);
        when(EventBus.getDefault()).thenReturn(eventBus);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.doNothing().when(Utils.class, "showMessageByToast", any(Context.class),anyInt(),anyInt());
        stub(PowerMockito.method(PrimeroApplication.class, "getAppRuntime")).toReturn(appRuntime);
    }

    public void on_create_supporting_code() {
        ColorStateList caseColor = PowerMockito.mock(ColorStateList.class);
        try {
            PowerMockito.suppress(RecordActivity.class.getDeclaredMethod("onCreate", Bundle.class));
            PowerMockito.suppress(PowerMockito.method(CaseActivity.class, "setNavSelectedMenu"));
            PowerMockito.suppress(PowerMockito.method(DrawerLayout.class, "closeDrawer", int.class));
            PowerMockito.doNothing().when(caseActivity).turnToFeature(CaseFeature.LIST, null, null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_on_create_open_drawer() {
        on_create_supporting_code();
        when(intent.getBooleanExtra(IntentSender.IS_FROM_LOGIN, false)).thenReturn(true);

        caseActivity.onCreate(savedInstanceState);
        Mockito.verify(intent, times(1)).getBooleanExtra(IntentSender.IS_FROM_LOGIN, false);
        Mockito.verify(drawer, times(1)).openDrawer(GravityCompat.START);
        Mockito.verify(caseActivity, times(1)).turnToFeature(CaseFeature.LIST, null, null);
    }

    @Test
    public void test_on_create_close_drawer() {
        on_create_supporting_code();
        when(intent.getBooleanExtra(IntentSender.IS_FROM_LOGIN, false)).thenReturn(false);

        caseActivity.onCreate(savedInstanceState);
        Mockito.verify(intent, times(1)).getBooleanExtra(IntentSender.IS_FROM_LOGIN, false);
        Mockito.verify(drawer, times(1)).closeDrawer(GravityCompat.START);
        Mockito.verify(caseActivity, times(1)).turnToFeature(CaseFeature.LIST, null, null);
    }

    public void sync_form_supporting_code() {
        PowerMockito.stub(PowerMockito.method(PrimeroAppConfiguration.class, "getCurrentUser")).toReturn(user);
    }

    @Test
    public void test_send_sync_form_event_role_cp() {
        sync_form_supporting_code();
        when(user.getRoleType()).thenReturn(User.Role.CP);

        caseActivity.sendSyncFormEvent();

        Mockito.verify(eventBus, times(1)).postSticky(any(LoadCPCaseFormEvent.class));
    }

    @Test
    public void test_send_sync_form_event_role_gbv() {
        sync_form_supporting_code();
        when(user.getRoleType()).thenReturn(User.Role.GBV);

        caseActivity.sendSyncFormEvent();

        Mockito.verify(eventBus, times(2)).postSticky(any());
    }

    @Test
    public void test_send_sync_form_event_default() {
        sync_form_supporting_code();
        when(user.getRoleType()).thenReturn(User.Role.EMPTY_ROLE);

        caseActivity.sendSyncFormEvent();

        Mockito.verify(eventBus, times(1)).postSticky(any(LoadCPCaseFormEvent.class));
    }

    // TODO fix stubbing of findFragmentByTag it reports wrong cast type
    @Test
    public void test_get_record_list_fragment() {
        FragmentManager fragmentManager = PowerMockito.mock(FragmentManager.class);
        PowerMockito.stub(PowerMockito.method(CaseActivity.class, "getSupportFragmentManager")).toReturn(fragmentManager);
        android.support.v4.app.Fragment fragment = new CaseListFragment();
        doReturn(fragment).when(fragmentManager).findFragmentByTag(anyString());

        RecordListFragment recordListFragment = caseActivity.getRecordListFragment();
        Assert.assertEquals(fragment, recordListFragment);
    }

    // TODO fix stubbing of findFragmentByTag it reports wrong cast type
    @Test
    public void test_get_case_search_fragment() {
        FragmentManager fragmentManager = PowerMockito.mock(FragmentManager.class);
        PowerMockito.stub(PowerMockito.method(CaseActivity.class, "getSupportFragmentManager")).toReturn(fragmentManager);
        android.support.v4.app.Fragment fragment = new CaseSearchFragment();
        doReturn(fragment).when(fragmentManager).findFragmentByTag(anyString());

        CaseSearchFragment caseSearchFragment = caseActivity.getCaseSearchFragment();
        Assert.assertEquals(fragment, caseSearchFragment);
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

        caseActivity.processBackButton();
        // we can not verify logout because is protected but we can verify else if is not reached
        Mockito.verify(currentFeature, times(0)).isEditMode();
    }

    @Test
    public void test_process_back_button_show_quit() {
        when(currentFeature.isListMode()).thenReturn(false);
        when(currentFeature.isDeleteMode()).thenReturn(false);
        when(currentFeature.isEditMode()).thenReturn(true);

        try {
            PowerMockito.suppress(CaseActivity.class.getDeclaredMethod("showQuitDialog", int.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        caseActivity.processBackButton();
        Mockito.verify(currentFeature, times(1)).isEditMode();
        // verify turnToFeature is not called instead of protected showQuitDialog
        Mockito.verify(caseActivity, times(0)).turnToFeature(CaseFeature.LIST, null, null);
    }

    @Test
    public void test_process_back_button_go_to_list() {
        when(currentFeature.isListMode()).thenReturn(false);
        when(currentFeature.isDeleteMode()).thenReturn(false);
        when(currentFeature.isEditMode()).thenReturn(false);
        PowerMockito.doNothing().when(caseActivity).turnToFeature(CaseFeature.LIST, null, null);

        caseActivity.processBackButton();
        PowerMockito.verifyStatic();
        Utils.clearAudioFile(AUDIO_FILE_PATH);
        Mockito.verify(caseActivity, times(1)).turnToFeature(CaseFeature.LIST, null, null);
    }

    @Test
    public void test_nav_case_action_show_quit() {
        stub(PowerMockito.method(RecordActivity.class, "setShowHideSwitcherToShowState")).toReturn(null);
        when(currentFeature.isEditMode()).thenReturn(true);
        try {
            PowerMockito.suppress(CaseActivity.class.getDeclaredMethod("showQuitDialog", int.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        caseActivity.navCaseAction();

        // verify turnToFeature is not called instead of protected showQuitDialog
        Mockito.verify(caseActivity, times(0)).turnToFeature(CaseFeature.LIST, null, null);
    }

    @Test
    public void test_nav_case_action_go_to_list() {
        stub(PowerMockito.method(RecordActivity.class, "setShowHideSwitcherToShowState")).toReturn(null);
        when(currentFeature.isEditMode()).thenReturn(false);
        PowerMockito.doNothing().when(caseActivity).turnToFeature(CaseFeature.LIST, null, null);

        caseActivity.navCaseAction();

        Mockito.verify(caseActivity, times(1)).turnToFeature(CaseFeature.LIST, null, null);
    }

    @Test
    public void test_nav_tracing_action_show_quit() {
        when(currentFeature.isEditMode()).thenReturn(true);
        try {
            PowerMockito.suppress(CaseActivity.class.getDeclaredMethod("showQuitDialog", int.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        caseActivity.navTracingAction();

        // verify showTracingActivity is not called instead of protected showQuitDialog
        Mockito.verify(intentSender, times(0)).showTracingActivity(caseActivity, true);
    }

    @Test
    public void test_nav_tracing_action_go_to_list() {
        when(currentFeature.isEditMode()).thenReturn(false);
        PowerMockito.doNothing().when(intentSender).showTracingActivity(caseActivity, true);

        caseActivity.navTracingAction();

        Mockito.verify(intentSender, times(1)).showTracingActivity(caseActivity, true);
    }

    @Test
    public void test_nav_incident_action_show_quit() {
        when(currentFeature.isEditMode()).thenReturn(true);
        try {
            PowerMockito.suppress(CaseActivity.class.getDeclaredMethod("showQuitDialog", int.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        caseActivity.navIncidentAction();

        // verify turnToFeature is not called instead of protected showQuitDialog
        Mockito.verify(intentSender, times(0)).showIncidentActivity(caseActivity, true);
    }

    @Test
    public void test_nav_incident_action_go_to_list() {
        when(currentFeature.isEditMode()).thenReturn(false);
        PowerMockito.doNothing().when(intentSender).showIncidentActivity(caseActivity, true);

        caseActivity.navIncidentAction();

        Mockito.verify(intentSender, times(1)).showIncidentActivity(caseActivity, true);
    }

    @Test
    public void test_search() {
        PowerMockito.doNothing().when(caseActivity).turnToFeature(CaseFeature.SEARCH, null, null);

        caseActivity.search();

        Mockito.verify(caseActivity, times(1)).turnToFeature(CaseFeature.SEARCH, null, null);
    }

    @Test
    public void test_search_web() {
        PowerMockito.doNothing().when(caseActivity).turnToFeature(CaseFeature.SEARCH_WEB, null, null);

        caseActivity.searchWeb();

        Mockito.verify(caseActivity, times(1)).turnToFeature(CaseFeature.SEARCH_WEB, null, null);
    }

    @Test
    public void test_save() {

        caseActivity.save();

        Mockito.verify(eventBus, times(1)).postSticky(any(SaveCaseEvent.class));
    }

    @Test
    public void test_promote_sync_forms_error() {
        caseActivity.promoteSyncFormsError();

        PowerMockito.verifyStatic(Mockito.times(1));
        Utils.showMessageByToast(caseActivity, R.string.sync_forms_error, Toast.LENGTH_SHORT);
    }

    @Test
    public void test_on_destroy() {
        PowerMockito.suppress(PowerMockito.method(RecordActivity.class, "onDestroy"));
        caseActivity.onDestroy();

        Mockito.verify(eventBus, times(1)).unregister(caseActivity);
    }

    private void redirect_incident_supporting_code() {
        Bundle extra = PowerMockito.mock(Bundle.class);
        when(redirectIncidentEvent.getIncidentInfo()).thenReturn("");
        PowerMockito.doNothing().when(intentSender).showIncidentActivity(caseActivity, true, extra);
    }

    @Test
    public void test_on_redirect_incident_event_ok() {
        redirect_incident_supporting_code();
        when(casePresenter.isIncidentFormReady()).thenReturn(true);

        caseActivity.onRedirectIncidentEvent(redirectIncidentEvent);

        Mockito.verify(intentSender, times(1)).showIncidentActivity(any(CaseActivity.class), anyBoolean(), any(Bundle.class));
    }

    @Test
    public void test_on_redirect_incident_event_sync_fail() {
        redirect_incident_supporting_code();
        when(casePresenter.isIncidentFormReady()).thenReturn(false);
        doNothing().when(caseActivity).showSyncFormDialog(anyString());
        PowerMockito.when(appRuntime.isIncidentFormSyncFail()).thenReturn(true);
        when(resources.getString(R.string.sync_forms_message)).thenReturn("");

        caseActivity.onRedirectIncidentEvent(redirectIncidentEvent);

        Mockito.verify(caseActivity, times(1)).showSyncFormDialog(anyString());
    }

    @Test
    public void test_on_redirect_incident_event_syncing() {
        redirect_incident_supporting_code();
        when(casePresenter.isIncidentFormReady()).thenReturn(false);
        doNothing().when(caseActivity).showSyncFormDialog(anyString());
        PowerMockito.when(appRuntime.isIncidentFormSyncFail()).thenReturn(false);

        caseActivity.onRedirectIncidentEvent(redirectIncidentEvent);

        verifyStatic();
        Utils.showMessageByToast(caseActivity, R.string.forms_is_syncing_msg, Toast.LENGTH_SHORT);
    }
}
