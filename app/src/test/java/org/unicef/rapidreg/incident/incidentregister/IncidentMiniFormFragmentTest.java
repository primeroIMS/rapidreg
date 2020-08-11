package org.unicef.rapidreg.incident.incidentregister;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.RecyclerView;
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
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.event.SaveIncidentEvent;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.incident.IncidentActivity;
import org.unicef.rapidreg.incident.IncidentFeature;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.support.membermodification.MemberModifier.stub;
import static org.unicef.rapidreg.service.CaseService.CASE_ID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IncidentMiniFormFragment.class, RecordRegisterFragment.class, EventBus.class, Utils.class})
public class IncidentMiniFormFragmentTest {

    @Mock
    FragmentComponent fragmentComponent;

    @Mock
    FloatingActionButton editButton;

    @Mock
    IncidentActivity incidentActivity;

    @Mock
    IncidentRegisterPresenter incidentRegisterPresenter;

    @Mock
    RecordRegisterAdapter recordRegisterAdapter;

    @Mock
    Bundle arguments;

    @Mock
    Feature featureMock;

    @Mock
    TextView topInfoMessage;

    @Mock
    TextView formSwitcher;

    @InjectMocks
    IncidentMiniFormFragment incidentMiniFormFragment = PowerMockito.spy(new IncidentMiniFormFragment());

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        doReturn(fragmentComponent).when(incidentMiniFormFragment).getComponent();
        doReturn(incidentActivity).when(incidentMiniFormFragment).getActivity();
        doReturn(arguments).when(incidentMiniFormFragment).getArguments();
        mockStatic(Utils.class);
        PowerMockito.doNothing().when(Utils.class, "showMessageByToast", any(Context.class),anyInt(),anyInt());
    }

    @Test
    public void test_on_create_view() {
        LayoutInflater inflater = PowerMockito.mock(LayoutInflater.class);
        ViewGroup container = PowerMockito.mock(ViewGroup.class);
        Bundle savedInstanceState = PowerMockito.mock(Bundle.class);
        incidentMiniFormFragment.onCreateView(inflater, container, savedInstanceState);
        verify(fragmentComponent, times(1)).inject(incidentMiniFormFragment);
        verify(inflater, times(1)).inflate(R.layout.fragment_register, container, false);
    }

    @Test
    public void test_on_init_view_content() throws NoSuchFieldException {
        FieldSetter.setField(incidentMiniFormFragment, incidentMiniFormFragment.getClass().getSuperclass().getSuperclass().getDeclaredField("fieldList"), PowerMockito.mock(RecyclerView.class));
        doReturn(true).when(featureMock).isDetailMode();
        doReturn(featureMock).when(incidentActivity).getCurrentFeature();
        doNothing().when(formSwitcher).setText(anyInt());
        doNothing().when((RecordRegisterFragment)incidentMiniFormFragment).addProfileFieldForDetailsPage(anyInt(), anyList());
        incidentMiniFormFragment.onInitViewContent();
        verify((RecordRegisterFragment)incidentMiniFormFragment, times(1)).onInitViewContent();
        verify(editButton, times(1)).setVisibility(View.VISIBLE);
    }

    @Test
    @Ignore("TODO fix mocking of EventBus.getDefault()")
    public void test_on_start() {
        doNothing().when((MvpFragment)incidentMiniFormFragment).onStart();
        PowerMockito.mockStatic(EventBus.class);
        EventBus eventBus = PowerMockito.mock(EventBus.class);
        stub(PowerMockito.method(EventBus.class, "getDefault")).toReturn(eventBus);
        incidentMiniFormFragment.onStart();
        verify(eventBus, times(1)).register(incidentMiniFormFragment);
    }

    @Test
    @Ignore("TODO fix mocking of EventBus.getDefault()")
    public void test_on_stop() {
        doNothing().when((MvpFragment)incidentMiniFormFragment).onStop();
        PowerMockito.mockStatic(EventBus.class);
        EventBus eventBus = PowerMockito.mock(EventBus.class);
        stub(PowerMockito.method(EventBus.class, "getDefault")).toReturn(eventBus);
        incidentMiniFormFragment.onStop();
        verify(eventBus, times(1)).register(incidentMiniFormFragment);
    }

    @Test
    public void test_create_record_register_adapter() {
        int position = 0;
        List<Field> fields = new ArrayList<Field>();

        // Mock protected inherited method
        try {
            PowerMockito.doNothing().when(incidentMiniFormFragment, "addProfileFieldForDetailsPage", anyInt(), anyString(), any());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RecordRegisterAdapter recordRegisterAdapter = incidentMiniFormFragment.createRecordRegisterAdapter();
        Assert.assertThat(recordRegisterAdapter, CoreMatchers.instanceOf(RecordRegisterAdapter.class));
    }

    @Test
    public void test_save_incident() {
        String caseId = "123";
        ItemValuesMap recordRegisterData = Mockito.mock(ItemValuesMap.class);
        doReturn(recordRegisterData).when(incidentMiniFormFragment).getRecordRegisterData();
        when(recordRegisterAdapter.getItemValues()).thenReturn(new ItemValuesMap());
        when(arguments.getString(CASE_ID)).thenReturn(caseId);
        incidentMiniFormFragment.saveIncident(new SaveIncidentEvent());
        verify(recordRegisterData, times(1)).addStringItem(CASE_ID, caseId);
        verify(incidentRegisterPresenter, times(1)).saveRecord(any(), any(), any());
    }

    @Test
    public void test_on_save_successful() {
        incidentMiniFormFragment.onSaveSuccessful(1);
        PowerMockito.verifyStatic(Utils.class,times(1));
        Utils.showMessageByToast(incidentActivity, R.string.save_success, Toast.LENGTH_SHORT);
        verify(incidentActivity, times(1)).turnToFeature(any(IncidentFeature.class), any(Bundle.class), any());
    }

    @Test
    public void test_on_edit_clicked() {
        incidentMiniFormFragment.onEditClicked();
        verify(incidentActivity, times(1)).turnToFeature(any(IncidentFeature.class), any(Bundle.class), any());
    }

    @Test
    @Ignore("TODO fix mocking of clickListener since onSwitcherChecked does not longer exist")
    public void test_on_switcher_checked() {
        when(incidentActivity.getCurrentFeature()).thenReturn(IncidentFeature.DETAILS_FULL);
        // incidentMiniFormFragment.onSwitcherChecked();
        verify(incidentActivity, times(1)).turnToFeature(any(IncidentFeature.class), any(), any());
    }
}
