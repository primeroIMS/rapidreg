package org.unicef.rapidreg.service.cache;

import org.unicef.rapidreg.lookups.LookupOption;
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
            for (LookupOption option : lookups.toGson()) {
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

    public static boolean containsLocation(String key) { return key.contains("Location"); }
}
