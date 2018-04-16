package org.unicef.rapidreg.service.cache;

import org.unicef.rapidreg.lookups.LookupList;
import org.unicef.rapidreg.lookups.Option;
import org.unicef.rapidreg.model.Lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalLookupCache {
    public static Map<String, List> lookupOptions =  new HashMap<>();

    public static void initLookupOptions(Lookup lookups) {
        if (lookupOptions.isEmpty()) {
            for (LookupList option : lookups.toGson()) {
                lookupOptions.put(option.getType(), option.getOptions());
            }
        }
    }

    public static void clearLookups() { lookupOptions.clear(); }

    public static List<Option> getLookup(String lookup) {
        List<Option> options = new ArrayList<>();

        if (lookupOptions.containsKey(lookup)) {
            options = lookupOptions.get(lookup);
        }

        return  options;
    }

    public static int getSelectOptionIndex(List<Option> options, String result) {
        Integer selectedIndex = -1;

        for (Option option: options) {
            if (option.getId().equals(result)) {
                selectedIndex = options.indexOf(option);
            }
        }
        return selectedIndex;
    }

    public static List<String> getSelectedOptions(List<Option> options, List<String> results) {
        List<String> selected = new ArrayList<>();

        for (Option option: options) {
            if (results.contains(option.getId())) {
                selected.add(option.getDisplayText());
            }
        }

        return selected;
    }

    public static Option getSingleSelectedOptions(List<Option> options, String result) {
        Option selected = new Option();

        for (Option option: options) {
            if (option.getId().equals(result) || option.getDisplayText().equals(result)) {
                selected = option;
                break;
            }
        }

        return selected;
    }

    public static String translationValueByLookup(final String lookup, final String value) {
        return getSingleSelectedOptions(getLookup(lookup), value).getDisplayText();
    }
}
