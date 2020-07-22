package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.queriable.StringQuery;

import org.unicef.rapidreg.model.Lookup;
import org.unicef.rapidreg.model.LookupResponse;
import org.unicef.rapidreg.model.Lookup_Table;
import org.unicef.rapidreg.repository.LookupDao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.raizlabs.android.dbflow.sql.language.SQLite.*;

public class LookupDaoImpl implements LookupDao {
    @Override
    public Lookup getByServerUrlAndLocale(String apiBaseUrl, String locale) {
        final int CHUNK_SIZE = 100000;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        LookupResponse lookupResult = select(Lookup_Table.id, new Method("LENGTH", Lookup_Table.lookups_json).as("blobSize"))
                .from(Lookup.class)
                .where(new Method("LENGTH", Lookup_Table.lookups_json).greaterThan(CHUNK_SIZE))
                .and(Lookup_Table.server_url.eq(apiBaseUrl))
                .and(Lookup_Table.locale.eq(locale))
                .queryCustomSingle(LookupResponse.class);

        int remainingChunks = lookupResult.getBlobSize();

        int i = 0;
        while(remainingChunks > 0) {
            int chunkSize = remainingChunks > CHUNK_SIZE ? CHUNK_SIZE : remainingChunks;
            StringQuery query = new StringQuery(Lookup.class, "SELECT substr(lookups_json," + (i * CHUNK_SIZE + 1) + "," +  chunkSize + ") as lookupsJson from Lookup WHERE id=" + lookupResult.getId());
            query.execute();

            LookupResponse results  = (LookupResponse) query.queryCustomSingle(LookupResponse.class);
            try {
                outputStream.write(results.getLookupsJson().getBlob());
            } catch (IOException e) {
                e.printStackTrace();
            }

            i++;
            remainingChunks -= chunkSize;
        }
        Blob lookupsBlob = new Blob(outputStream.toByteArray());

        Lookup lookup = new Lookup();
        lookup.setLookupsJson(lookupsBlob);

        return lookup;
    }

    public void streamLookups() {

    }

    @Override
    public void save(Lookup lookups) {
        lookups.save();
    }

    @Override
    public void update(Lookup lookups) {
        lookups.update();
    }
}
