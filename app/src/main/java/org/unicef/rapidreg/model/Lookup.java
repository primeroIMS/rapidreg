package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroDatabaseConfiguration;

@Table(database = PrimeroDatabaseConfiguration.class)
public class Lookup extends BaseModel {

    @PrimaryKey(autoincrement = true)
    @Column
    private int id;

    @Column(name = "lookups_json")
    private Blob lookupsJson;

    @Column(name = "server_url")
    private String serverUrl;

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
}
