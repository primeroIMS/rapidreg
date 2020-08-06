package org.unicef.rapidreg.incident.incidentlist;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.ViewSwitcher;

import com.raizlabs.android.dbflow.data.Blob;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordlist.RecordListViewHolder;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class IncidentListAdapterTest {
    @Mock
    RecordActivity context;

    @Mock
    IncidentService incidentService;

    @InjectMocks
    IncidentListAdapter incidentListAdapter = PowerMockito.spy(new IncidentListAdapter(context));

    @Mock
    List<Long> recordList;

    @Mock
    Incident record;

    @Mock
    ViewSwitcher viewSwitcher;

    @Mock
    CheckBox deleteStateCheckBox;

    @InjectMocks
    RecordListViewHolder holder = PowerMockito.mock(RecordListViewHolder.class);

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void test_on_bind_viewholder() {

        int position = 0;
        incidentListAdapter.setRecordList(recordList);
        when(recordList.get(anyInt())).thenReturn(1l);
        when(incidentService.getById(anyLong())).thenReturn(record);
        when(record.getContent()).thenReturn(new Blob("{\"sex\": \"M\", \"age\": \"10\"}".getBytes()));
        String shortUUID = "abc";
        when(incidentService.getShortUUID(any())).thenReturn(shortUUID);
        PowerMockito.doNothing().when(viewSwitcher).setDisplayedChild(anyInt());
        when(deleteStateCheckBox.getTag()).thenReturn(new Object());
        PowerMockito.doNothing().when(deleteStateCheckBox).setChecked(anyBoolean());
        ItemValuesMap itemValues = PowerMockito.mock(ItemValuesMap.class);
        String sex = "M";
        when(itemValues.getAsString(RecordService.SEX)).thenReturn(sex);
        String age = "10";
        when(itemValues.getAsString(RecordService.AGE)).thenReturn(age);

        incidentListAdapter.onBindViewHolder(holder, position);
        verify(holder, times(1)).disableRecordImageView();
        verify(holder, times(1)).setValues(sex, shortUUID, age, record, recordList, Collections.emptyList(), 0);
        verify(holder, times(1)).setViewOnClickListener(any());
        verify(holder, times(1)).disableRecordGenderView();
    }

    @Test
    public void test_remove_records() {
        List<Long> removeIds = new ArrayList<Long>() {{
            add(Long.valueOf(1l));
            add(Long.valueOf(2l));
            add(Long.valueOf(3l));
        }};
        when(incidentListAdapter.getRecordWillBeDeletedList()).thenReturn(removeIds);
        when(incidentService.deleteByRecordId(anyLong())).thenReturn(record);
        incidentListAdapter.removeRecords();
        verify(incidentService, times(3)).deleteByRecordId(anyLong());
        verify(incidentService, times(1)).execSQL(anyString());
    }
}

