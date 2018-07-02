package org.unicef.rapidreg.tracing.tracinglist;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.ViewSwitcher;

import com.raizlabs.android.dbflow.data.Blob;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class TracingListAdapterTest {

    @Mock
    List<Long> recordList;

    @Mock
    TracingService tracingService;

    @Mock
    Tracing record;

    @Mock
    ViewSwitcher viewSwitcher;

    @Mock
    CheckBox deleteStateCheckBox;

    @Mock
    Context context;

    @InjectMocks
    RecordListAdapter.RecordListViewHolder holder = PowerMockito.mock(RecordListAdapter.RecordListViewHolder.class);

    @InjectMocks
    TracingListAdapter tracingListAdapter = PowerMockito.spy(new TracingListAdapter(context));

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void test_on_bind_viewholder() {
        int position = 0;
        tracingListAdapter.setRecordList(recordList);
        when(recordList.get(Matchers.anyInt())).thenReturn(1l);
        when(tracingService.getById(Matchers.anyLong())).thenReturn(record);
        when(record.getContent()).thenReturn(new Blob("{\"sex\": \"M\", \"relation_age\": \"10\"}".getBytes()));
        String shortUUID = "abc";
        when(tracingService.getShortUUID(Matchers.anyString())).thenReturn(shortUUID);

        PowerMockito.doNothing().when(viewSwitcher).setDisplayedChild(Matchers.anyInt());
        when(deleteStateCheckBox.getTag()).thenReturn(new Object());
        PowerMockito.doNothing().when(deleteStateCheckBox).setChecked(Matchers.anyBoolean());

        ItemValuesMap itemValues = PowerMockito.mock(ItemValuesMap.class);

        String sex = "M";
        when(itemValues.getAsString(RecordService.SEX)).thenReturn(sex);
        String age = "10";
        when(itemValues.getAsString(RecordService.RELATION_AGE)).thenReturn(age);

        tracingListAdapter.onBindViewHolder(holder, position);
        verify(holder, times(1)).setValues(sex, shortUUID, age, record);
        verify(holder, times(1)).setViewOnClickListener(any());
    }

    @Test
    public void test_remove_records() {
        List<Long> removeIds = new ArrayList<Long>() {{
            add(Long.valueOf(1l));
            add(Long.valueOf(2l));
            add(Long.valueOf(3l));
        }};
        when(tracingListAdapter.getRecordWillBeDeletedList()).thenReturn(removeIds);
        when(tracingService.deleteByRecordId(anyLong())).thenReturn(record);
        tracingListAdapter.removeRecords();
        verify(tracingService, times(3)).deleteByRecordId(anyLong());
        verify(tracingService, times(1)).execSQL(anyString());
    }
}
