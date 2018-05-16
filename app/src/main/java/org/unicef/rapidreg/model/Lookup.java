package org.unicef.rapidreg.model;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroDatabaseConfiguration;
import org.unicef.rapidreg.lookups.LookupList;
import org.unicef.rapidreg.lookups.Option;

@Table(database = PrimeroDatabaseConfiguration.class)
public class Lookup extends BaseModel {
    static final String locationKey = "Location";

    @PrimaryKey(autoincrement = true)
    @Column
    private int id;

    @Column(name = "lookups_json")
    private Blob lookupsJson;

    @Column(name = "server_url")
    private String serverUrl;

    @Column(name = "locale")
    private String locale;

    public Lookup() {}

    public Lookup(Blob lookups) {
        this.lookupsJson = lookups;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLookupsJson(Blob lookups) {
        this.lookupsJson = lookups;
    }

    public void setServerUrl(String url) {
        this.serverUrl = url;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public Blob getLookupsJson() {
        return lookupsJson;
    }

    public LookupList[] toGson() {
        String lookupString = new String(getLookupsJson().getBlob());
        LookupList[] result = new Gson().fromJson(lookupString, LookupList[].class);

        for (LookupList lookupOption: result) {
            if (lookupOption.getType().equals(locationKey)) {
                for(Option option: lookupOption.getOptions()) {
                    option.setLocation(true);
                }
             }
        }

        return result;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
