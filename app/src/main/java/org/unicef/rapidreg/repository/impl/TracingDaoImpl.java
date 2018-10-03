package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;

import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.Tracing_Table;
import org.unicef.rapidreg.repository.TracingDao;

import java.util.List;

public class TracingDaoImpl implements TracingDao {

    public static IProperty[] selectFields = new IProperty[]{
            Tracing_Table.id,
            Tracing_Table.name,
            Tracing_Table.age,
            Tracing_Table.caregiver,
            Tracing_Table.case_json,
            Tracing_Table.is_synced,
            Tracing_Table.sync_log,
            Tracing_Table._id,
            Tracing_Table._rev,
            Tracing_Table.unique_id,
            Tracing_Table.unique_identifier,
            Tracing_Table.short_id,
            Tracing_Table.registration_date,
            Tracing_Table.created_by,
            Tracing_Table.server_url,
            Tracing_Table.owned_by,
            Tracing_Table.created_date,
            Tracing_Table.last_updated_date,
            Tracing_Table.last_synced_date,
            Tracing_Table.type,
            Tracing_Table.isAudioSynced,
            Tracing_Table.is_invalidated,
            Tracing_Table.note_alerts
    };

    @Override
    public Tracing save(Tracing tracing) {
        tracing.save();
        return tracing;
    }

    @Override
    public Tracing update(Tracing tracing) {
        tracing.update();
        return tracing;
    }

    @Override
    public List<Tracing> getAll(String ownedBy, String url) {
        return SQLite
                .select(selectFields)
                .from(Tracing.class)
                .where(Tracing_Table.owned_by.eq(ownedBy))
                .and(Tracing_Table.server_url.eq(url))
                .orderBy(Tracing_Table.registration_date, false)
                .queryList();
    }

    @Override
    public Tracing getTracingByUniqueId(String uniqueId) {
        return SQLite.select(selectFields).from(Tracing.class)
                .where(Tracing_Table.unique_id.eq(uniqueId))
                .querySingle();
    }

    @Override
    public List<Tracing> getAllTracingsOrderByDate(boolean isASC, String ownedBy, String url) {
        return isASC ? getTracingsByDateASC(ownedBy, url) : getTracingsByDateDES(ownedBy, url);
    }

    @Override
    public List<Tracing> getAllTracingsByConditionGroup(String ownedBy, String url, ConditionGroup conditionGroup) {
        return SQLite.select(selectFields).from(Tracing.class)
                .where(conditionGroup)
                .and(Tracing_Table.owned_by.eq(ownedBy))
                .and(Tracing_Table.server_url.eq(url))
                .orderBy(Tracing_Table.registration_date, false)
                .queryList();
    }

    @Override
    public Tracing getTracingById(long tracingId) {
        return SQLite.select(selectFields).from(Tracing.class).where(Tracing_Table.id.eq(tracingId)).querySingle();
    }

    @Override
    public Tracing getByInternalId(String id) {
        return SQLite.select(selectFields).from(Tracing.class).where(Tracing_Table._id.eq(id)).querySingle();
    }

    @Override
    public Tracing deleteByRecordId(long recordId) {
        Tracing deleteTracing = getTracingById(recordId);
        if (deleteTracing != null) {
            deleteTracing.delete();
        }
        return deleteTracing;
    }

    @Override
    public Tracing delete(Tracing deleteTracing) {
        if (deleteTracing != null) {
            deleteTracing.delete();
        }
        return deleteTracing;
    }

    @Override
    public List<Tracing> getALLSyncedRecords(String ownedBy) {
        return SQLite.select(Tracing_Table.id)
                .from(Tracing.class)
                .where(Tracing_Table.is_synced.eq(true))
                .and(Tracing_Table.owned_by.eq(ownedBy))
                .queryList();
    }

    private List<Tracing> getTracingsByDateASC(String ownedBy, String url) {
        return getCurrentServerUserCondition(ownedBy, url)
                .orderBy(Tracing_Table.registration_date, true)
                .queryList();
    }

    private List<Tracing> getTracingsByDateDES(String ownedBy, String url) {
        return getCurrentServerUserCondition(ownedBy, url)
                .orderBy(Tracing_Table.registration_date, false)
                .queryList();
    }

    private Where<Tracing> getCurrentServerUserCondition(String ownedBy, String url) {
        IProperty[] selectedListFields = new IProperty[]{
                Tracing_Table.id,
                Tracing_Table.name,
                Tracing_Table.age,
                Tracing_Table._id,
                Tracing_Table.unique_id,
                Tracing_Table.unique_identifier,
                Tracing_Table.short_id,
                Tracing_Table.registration_date,
                Tracing_Table.owned_by,
                Tracing_Table.type,
                Tracing_Table.is_invalidated,
                Tracing_Table.note_alerts
        };
        
        return SQLite
                .select(selectedListFields)
                .from(Tracing.class)
                .where(Tracing_Table.owned_by.eq(ownedBy))
                .and(Tracing_Table.server_url.eq(url));
    }
}
