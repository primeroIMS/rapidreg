package org.unicef.rapidreg.service.cache;

import org.unicef.rapidreg.lookups.LookupOption;
import org.unicef.rapidreg.lookups.Options;
import org.unicef.rapidreg.model.Lookup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class GlobalLookupCache {
    public static List<LookupOption> lookupOptions =  new ArrayList<>();

    public static void initLookupOptions(Lookup lookups) {
        if (lookupOptions.isEmpty()) {
            String lookupString = new String(lookups.getLookupsJson().getBlob());
            LookupOption[] options = new Gson().fromJson(lookupString, LookupOption[].class);

            for (LookupOption option : options) {
                lookupOptions.add(option);
            }
        }
    }

    public static List<LookupOption> getLookupOptions() {
        return lookupOptions;
    }

    public static void clearLookups() { lookupOptions.clear(); }

    public static List<Options> getLookup(String lookup) {
        List<Options> selectedOption = new ArrayList<>();

        for (LookupOption lookups : lookupOptions) {
            String type = lookups.getType();

            if (lookup.contains(type)) {
                selectedOption = lookups.getOptions();
            }
        }

        return selectedOption;
    }
}
