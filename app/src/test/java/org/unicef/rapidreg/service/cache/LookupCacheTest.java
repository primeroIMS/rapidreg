package org.unicef.rapidreg.service.cache;

import com.raizlabs.android.dbflow.data.Blob;

import org.junit.Before;
import org.junit.Test;
import org.unicef.rapidreg.lookups.Option;
import org.unicef.rapidreg.model.Lookup;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class LookupCacheTest {
    private GlobalLookupCache lookupCache = new GlobalLookupCache();

    @Before
    public void setUp() throws Exception {
        String lookupString = "[\n" +
                "    {\n" +
                "      \"type\": \"lookup-1\",\n" +
                "      \"options\": [\n" +
                "        { \"id\": \"id1\", \"display_text\": \"Lookup 1\" },\n" +
                "        { \"id\": \"id2\", \"display_text\": \"Lookup 2\" },\n" +
                "        { \"id\": \"id3\", \"display_text\": \"Lookup 3\" },\n" +
                "        { \"id\": \"id4\", \"display_text\": \"Lookup 4\" }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"lookup-2\",\n" +
                "      \"options\": [\n" +
                "        { \"id\": \"id1\", \"display_text\": \"Lookup 1\" },\n" +
                "        { \"id\": \"id2\", \"display_text\": \"Lookup 2\" },\n" +
                "        { \"id\": \"id3\", \"display_text\": \"Lookup 3\" }\n" +
                "      ]\n" +
                "    }]";
        Blob lookupBlob = new Blob(lookupString.getBytes());
        lookupCache.initLookupOptions(new Lookup(lookupBlob));
    }

    @Test
    public void should_set_options() throws Exception {
        assertThat(lookupCache.lookupOptions.size(), is(2));
    }

    @Test
    public void should_return_option_by_index() throws Exception {
        assertThat(lookupCache.getSelectOptionIndex(lookupCache.getLookup("lookup-1"), "id2"), is(1));
    }

    @Test
    public void should_return_single_selected_option() throws Exception {
        Option expectedOption = new Option("id3", "Lookup 3");

        assertThat(lookupCache.getSingleSelectedOptions(lookupCache.getLookup("lookup-1"), "id3"), is(samePropertyValuesAs(expectedOption)));
        assertThat(lookupCache.getSingleSelectedOptions(lookupCache.getLookup("lookup-1"), "Lookup 3"), is(samePropertyValuesAs(expectedOption)));
    }

    @Test
    public void should_return_multiple_selected_options()  throws Exception {
        List<String> expectedOutput = asList("Lookup 1", "Lookup 4");
        List<String> selectedOptions = asList("id1", "Lookup 4");
        assertThat(lookupCache.getSelectedOptions(lookupCache.getLookup("lookup-1"), selectedOptions), is(equalTo(expectedOutput)));
    }

    @Test
    public void should_return_lookup_from_cache()  throws Exception {
        List<Option> expectedOutput = new ArrayList<>();
        expectedOutput.add(new Option("id1", "Lookup 1"));
        expectedOutput.add(new Option("id2", "Lookup 2"));
        expectedOutput.add(new Option("id3", "Lookup 3"));
        assertThat(lookupCache.getLookup("lookup-2"), samePropertyValuesAs(expectedOutput));
    }

    @Test
    public void should_clear_lookup_cache() throws Exception {
        assertThat(lookupCache.lookupOptions.size(), is(2));
        lookupCache.clearLookups();
        assertThat(lookupCache.lookupOptions.size(), is(0));
    }
}
