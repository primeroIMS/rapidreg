package org.unicef.rapidreg.lookups;

import com.google.gson.annotations.SerializedName;


public class Option {
    private String id;
    @SerializedName("display_text")
    private String displayText;

    public Option() {}

    public Option(String id, String displayText) {
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
