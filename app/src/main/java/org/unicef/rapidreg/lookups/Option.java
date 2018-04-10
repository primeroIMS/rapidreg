package org.unicef.rapidreg.lookups;

import com.google.gson.annotations.SerializedName;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.utils.TextUtils;


public class Option {
    private String id;
    @SerializedName("display_text")
    private String displayText;
    private boolean isLocation;

    public Option() {}

    public Option(final String id, final String displayText) {
        this.id = id;
        this.displayText = displayText;
        this.isLocation = false;
    }

    public String getId() {
        return id;
    }

    public String getDisplayText() {
        if (isLocation) {
            return TextUtils.truncateByDoubleColons(displayText, PrimeroAppConfiguration
                    .getCurrentSystemSettings()
                    .getDistrictLevel());
        } else {
            return displayText;
        }
    }

    public boolean isLocation() {
        return isLocation;
    }

    public void setLocation(final boolean isLocation) {
        this.isLocation = isLocation;
    }
}
