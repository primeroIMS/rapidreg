package org.unicef.rapidreg.childcase.caseregister;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.transition.Visibility;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

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
import org.powermock.reflect.Whitebox;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseActivity;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterWrapperFragment;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.casephoto.CasePhotoAdapter;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.Utils;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
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
import static org.unicef.rapidreg.childcase.CaseFeature.DETAILS_CP_FULL;
import static org.unicef.rapidreg.childcase.caseregister.CaseRegisterPresenter.MODULE_CASE_CP;

@RunWith(PowerMockRunner.class)

@PrepareForTest({CaseMiniFormFragment.class, RecordRegisterFragment.class, EventBus.class, Fragment.class, Utils.class, RecordActivity.class})
public class CaseMiniFormFragmentTest {
    @Mock
    CaseRegisterPresenter caseRegisterPresenter;

    @Mock
    CasePhotoAdapter casePhotoAdapter;

    @Mock
    RecordRegisterWrapperFragment recordRegisterWrapperFragment;

    @Mock
    FragmentComponent fragmentComponent;

    @Mock
    Fragment fragment;

    @Mock
    FragmentActivity fragmentActivity;

    //@Mock
    RecordActivity recordActivity;

    @Mock
    BaseActivity baseActivity;

    @Mock
    CaseActivity caseActivity;

    @Mock
    FloatingActionButton editButton;

    @Mock
    TextView topInfoMessage;

    @Mock
    RecordRegisterAdapter recordRegisterAdapter;

    @InjectMocks
    RecordRegisterFragment recordRegisterFragment = PowerMockito.mock(RecordRegisterFragment.class);

    @InjectMocks
    CaseMiniFormFragment caseMiniFormFragment = PowerMockito.spy(new CaseMiniFormFragment());

    @Mock
    Feature featureMock;


    @Before
    public void setUp() throws Exception {
        initMocks(this);
        doNothing().when((RecordRegisterFragment)caseMiniFormFragment).onViewCreated(any(View.class), any(Bundle.class));
        mockStatic(Utils.class);
        PowerMockito.doNothing().when(Utils.class, "showMessageByToast", any(Context.class),anyInt(),anyInt());

        when(recordRegisterAdapter.getItemValues()).thenReturn(new ItemValuesMap());
        stub(PowerMockito.method(CaseMiniFormFragment.class, "getComponent")).toReturn(fragmentComponent);
        stub(PowerMockito.method(CaseMiniFormFragment.class, "getActivity")).toReturn(caseActivity);
        stub(PowerMockito.method(RecordRegisterFragment.class, "onCreateView")).toReturn(PowerMockito.mock(View.class));
    }

    @Test
    public void test_create_presenter() {
        assertNotNull(caseMiniFormFragment);
        assertEquals(caseRegisterPresenter, caseMiniFormFragment.createPresenter());
    }

    @Test
    public void test_on_create_view() {
        LayoutInflater inflater = PowerMockito.mock(LayoutInflater.class);
        ViewGroup container = PowerMockito.mock(ViewGroup.class);
        Bundle savedInstanceState = PowerMockito.mock(Bundle.class);
        caseMiniFormFragment.onCreateView(inflater, container, savedInstanceState);
        verify(fragmentComponent, times(1)).inject(caseMiniFormFragment);
        verify(inflater, times(1)).inflate(R.layout.fragment_register, container, false);
    }

    @Test
    @Ignore("TODO fix of stubbing of setShowHideSwitcherToShowState")
    public void test_on_view_created() {
        View view = PowerMockito.mock(View.class);
        Bundle savedInstanceState = PowerMockito.mock(Bundle.class);
        stub(PowerMockito.method(RecordActivity.class, "setShowHideSwitcherToShowState")).toReturn(null);
        caseMiniFormFragment.onViewCreated(view, savedInstanceState);
        verify(caseActivity, times(1)).setShowHideSwitcherToShowState();
        verify(caseMiniFormFragment, times(1)).initTopWarning();
    }

    @Test
    public void test_on_init_view_content() {
        Whitebox.setInternalState(caseMiniFormFragment, "fieldList", PowerMockito.mock(RecyclerView.class));
        when(featureMock.isDetailMode()).thenReturn(false);
        when(caseActivity.getCurrentFeature()).thenReturn(featureMock);
        doNothing().when((RecordRegisterFragment)caseMiniFormFragment).addProfileFieldForDetailsPage(anyInt(), anyList());
        caseMiniFormFragment.onInitViewContent();
        verify((RecordRegisterFragment)caseMiniFormFragment, times(1)).onInitViewContent();
        verify(editButton, times(1)).setVisibility(anyInt());
    }

    @Test
    @Ignore("TODO fix of stubbing of initTopWarning")
    public void test_init_top_warning() {
        when(caseRegisterPresenter.getCaseIsInvalidated(anyString())).thenReturn(true);
        caseMiniFormFragment.initTopWarning();
        assertEquals(topInfoMessage.getVisibility(), View.VISIBLE);
    }

    @Test
    @Ignore("TODO fix of stubbing of initTopWarning")
    public void test_init_top_warning2() {
        when(caseRegisterPresenter.getCaseIsInvalidated(anyString())).thenReturn(true);
        caseMiniFormFragment.initTopWarning();
        assertEquals(topInfoMessage.getVisibility(), View.VISIBLE);
    }

    @Test
    @Ignore("TODO fix mocking of EventBus.getDefault()")
    public void test_on_start() {
        doNothing().when((MvpFragment)caseMiniFormFragment).onStart();
        PowerMockito.mockStatic(EventBus.class);
        EventBus eventBus = PowerMockito.mock(EventBus.class);
        PowerMockito.stub(PowerMockito.method(EventBus.class, "getDefault")).toReturn(eventBus);
        caseMiniFormFragment.onStart();
        verify(eventBus, times(1)).register(caseMiniFormFragment);
    }

    @Test
    @Ignore("TODO fix mocking of EventBus.getDefault()")
    public void test_on_stop() {
        doNothing().when((MvpFragment)caseMiniFormFragment).onStop();
        PowerMockito.mockStatic(EventBus.class);
        EventBus eventBus = PowerMockito.mock(EventBus.class);
        //when(EventBus.getDefault()).thenReturn(eventBus);
        stub(PowerMockito.method(EventBus.class, "getDefault")).toReturn(eventBus);
        caseMiniFormFragment.onStop();
        verify(eventBus, times(1)).unregister(any());
    }

    @Test
    public void test_save_case() {
        caseMiniFormFragment.saveCase(new SaveCaseEvent());
        Mockito.verify(caseRegisterPresenter, Mockito.times(1)).saveRecord(any(), any(), any());
    }

    @Test
    public void test_on_save_successful() {
        when(caseRegisterPresenter.getCaseType()).thenReturn(MODULE_CASE_CP);
        caseMiniFormFragment.onSaveSuccessful(1);
        PowerMockito.verifyStatic(Utils.class, times(1));
        Utils.showMessageByToast(caseActivity, R.string.save_success, Toast.LENGTH_SHORT);
        Mockito.verify(caseRegisterPresenter, Mockito.times(1)).getCaseType();
        Mockito.verify(caseActivity, Mockito.times(1)).turnToFeature(any(), any(), any());
    }

    @Test
    public void test_on_edit_clicked() {
        stub(PowerMockito.method(CaseMiniFormFragment.class, "getPhotoPathsData")).toReturn(new ArrayList<String>());
        caseMiniFormFragment.onEditClicked();
        Mockito.verify(caseActivity, Mockito.times(1)).turnToFeature(any(), any(), any());
    }

    @Test
    @Ignore("TODO fix cause of ClassCastException of Collections$EmptyList into ArrayList")
    public void test_on_switcher_checked() {
        when(caseActivity.getCurrentFeature()).thenReturn(DETAILS_CP_FULL);
        caseMiniFormFragment.onSwitcherChecked();
        Mockito.verify(caseActivity, Mockito.times(1)).turnToFeature(any(), any(), any());
    }
}
