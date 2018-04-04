package org.unicef.rapidreg.lookups;

import java.util.List;

public class LookupList {
    private String type;
    private List<Option> options;

    public String getType() {
        return type;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(final List<Option> options) {
        this.options = options;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
