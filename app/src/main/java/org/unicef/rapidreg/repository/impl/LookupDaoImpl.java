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
    final private int CHUNK_SIZE = 100000;

    @Override
    public Lookup getByServerUrlAndLocale(String apiBaseUrl, String locale) {
        LookupResponse lookupResult = select(Lookup_Table.id, new Method("LENGTH", Lookup_Table.lookups_json).as("blobSize"))
                .from(Lookup.class)
                .where(new Method("LENGTH", Lookup_Table.lookups_json).greaterThan(CHUNK_SIZE))
                .and(Lookup_Table.server_url.eq(apiBaseUrl))
                .and(Lookup_Table.locale.eq(locale))
                .queryCustomSingle(LookupResponse.class);

        if (lookupResult != null) {
            return buildLookups(lookupResult);
        }

        return null;
    }

    private Lookup buildLookups(LookupResponse lookupResult) {
        ByteArrayOutputStream outputStream = lookupsOutputStream(lookupResult);
        Blob lookupsBlob = new Blob(outputStream.toByteArray());

        Lookup lookup = new Lookup();
        lookup.setLookupsJson(lookupsBlob);

        return lookup;
    }

    private ByteArrayOutputStream lookupsOutputStream(LookupResponse lookupResult) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int remainingChunks = lookupResult.getBlobSize();
        int i = 0;

        while(remainingChunks > 0) {
            int chunkSize = remainingChunks > CHUNK_SIZE ? CHUNK_SIZE : remainingChunks;

            String queryString = "SELECT substr(lookups_json," + (i * CHUNK_SIZE + 1) + "," +  chunkSize + ") as lookupsJson from Lookup WHERE id=" + lookupResult.getId();

            StringQuery query = new StringQuery(Lookup.class, queryString);

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

        return outputStream;
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
