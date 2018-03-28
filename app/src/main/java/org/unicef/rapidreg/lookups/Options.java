package org.unicef.rapidreg.lookups;

import com.google.gson.annotations.SerializedName;

public class Options {
    private String id;
    @SerializedName("display_text")
    private String displayText;

    public Options() {}

    public Options(String id, String displayText) {
        this.id = id;
        this.displayText = displayText;
    }

    public String getId() {
        return id;
    }

    public String getDisplayText() {
        return displayText;
    }
}
