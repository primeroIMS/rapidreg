package org.unicef.rapidreg.repository;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;

import org.unicef.rapidreg.model.Tracing;

import java.util.List;

public interface TracingDao {
    List<Tracing> getAll(String ownedBy, String url);

    Tracing save(Tracing tracing);

    Tracing update(Tracing tracing);

    Tracing getTracingByUniqueId(String id);

    List<Tracing> getAllTracingsOrderByDate(boolean isASC, String ownedBy, String url);

    List<Tracing> getAllTracingsByOperatorGroup(String ownedBy, String url, OperatorGroup operatorGroup);

    Tracing getTracingById(long tracingId);

    Tracing getByInternalId(String id);

    Tracing deleteByRecordId(long recordId);

    Tracing delete(Tracing deleteTracing);

    List<Tracing> getALLSyncedRecords(String ownedBy);
}
