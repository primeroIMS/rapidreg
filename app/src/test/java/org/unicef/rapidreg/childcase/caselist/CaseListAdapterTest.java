package org.unicef.rapidreg.childcase.caselist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;
import android.widget.ViewSwitcher;

import com.raizlabs.android.dbflow.data.Blob;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordlist.RecordListViewHolder;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.service.CaseService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PrimeroAppConfiguration.class, RecyclerView.Adapter.class, RecordListAdapter.class})
public class CaseListAdapterTest {
    @Mock
    CaseService caseService;

    @Mock
    Context context;

    @Mock
    List<Long> recordList;

    @Mock
    Case record;

    @Mock
    ViewSwitcher viewSwitcher;

    @Mock
    CheckBox deleteStateCheckBox;

    @Mock
    List<Long> recordWillBeDeletedList;

    @Mock
    User user;

    @InjectMocks
    RecordListViewHolder holder = PowerMockito.mock(RecordListViewHolder.class);

    RecyclerView.Adapter adapter = PowerMockito.mock(RecyclerView.Adapter.class);

    @InjectMocks
    private CaseListAdapter caseListAdapter = PowerMockito.spy(new CaseListAdapter(context));

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockStatic(PrimeroAppConfiguration.class);
        when(recordList.get(anyInt())).thenReturn(1l);
        when(caseService.getById(anyLong())).thenReturn(record);
        when(record.getContent()).thenReturn(new Blob("{}".getBytes()));
        when(user.getRoleType()).thenReturn(User.Role.GBV);
        when(PrimeroAppConfiguration.getCurrentUser()).thenReturn(user);
        PowerMockito.suppress(PowerMockito.method(RecyclerView.Adapter.class, "notifyDataSetChanged"));
        PowerMockito.doNothing().when(viewSwitcher).setDisplayedChild(anyInt());
        when(deleteStateCheckBox.getTag()).thenReturn(new Object());
        when(recordWillBeDeletedList.contains(any())).thenReturn(false);
        PowerMockito.doNothing().when(deleteStateCheckBox).setChecked(anyBoolean());
        PowerMockito.suppress(PowerMockito.method(RecordListAdapter.class, "removeRecords"));
    }

    @Test
    public void testNonNull() {
        assertNotNull(caseListAdapter);
    }

    @Test
    public void test_on_bind_viewholder() {
        caseListAdapter.setRecordList(recordList);
        caseListAdapter.toggleViews(true);
        caseListAdapter.onBindViewHolder(holder, 0);
        verify(recordList, times(1)).get(anyInt());
        verify(caseService, times(1)).getById(anyInt());
    }

    @Test
    public void test_remove_records() {
        List<Long> removeIds = new ArrayList<Long>() {{
            add(Long.valueOf(1l));
            add(Long.valueOf(2l));
            add(Long.valueOf(3l));
        }};
        when(caseListAdapter.getRecordWillBeDeletedList()).thenReturn(removeIds);
        when(caseService.deleteByRecordId(anyLong())).thenReturn(record);
        caseListAdapter.removeRecords();
        verify(caseService, times(3)).deleteByRecordId(anyLong());
        verify(caseService, times(1)).execSQL(anyString());
    }
}
