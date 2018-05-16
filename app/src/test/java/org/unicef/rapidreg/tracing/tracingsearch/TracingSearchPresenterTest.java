package org.unicef.rapidreg.tracing.tracingsearch;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.service.TracingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public class TracingSearchPresenterTest {

    @Mock
    TracingService tracingService;

    @InjectMocks
    TracingSearchPresenter tracingSearchPresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(Log.class);
    }

    @Test
    public void should_get_tracing_list_when_get_search_result() throws Exception {
        Map<String, String> searchConditions = mock(Map.class);
        List<Long> list = new ArrayList<>();

        when(searchConditions.get(anyString())).thenReturn("0");
        when(tracingService.getSearchResult(anyString(),anyString(),anyInt(),anyInt(),any()))
                .thenReturn(list);

        assertThat("Should return same list", tracingSearchPresenter.getSearchResult(searchConditions), is(list));
    }
}