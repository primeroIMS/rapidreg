package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.structure.BaseQueryModel;

import org.unicef.rapidreg.PrimeroDatabaseConfiguration;

@QueryModel(database = PrimeroDatabaseConfiguration.class)
public class LookupResponse extends BaseQueryModel {
    @Column
    private int id;

    @Column
    private int blobSize;

    @Column
    private Blob lookupsJson;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Blob getLookupsJson() {
        return lookupsJson;
    }

    public void setLookupsJson(Blob lookupsJson) {
        this.lookupsJson = lookupsJson;
    }

    public int getBlobSize() {
        return blobSize;
    }

    public void setBlobSize(int blobSize) {
        this.blobSize = blobSize;
    }
}
