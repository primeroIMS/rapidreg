package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;

import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.model.Incident_Table;
import org.unicef.rapidreg.repository.IncidentDao;

import java.util.List;

public class IncidentDaoImpl implements IncidentDao {
    public static IProperty[] selectFields = new IProperty[]{
        Incident_Table.id,
        Incident_Table.survivor_code,
        Incident_Table.type_of_violence,
        Incident_Table.location,
        Incident_Table.case_unique_id,
        Incident_Table.incident_case_id,
        Incident_Table.date_of_first_report,
        Incident_Table.name,
        Incident_Table.caregiver,
        Incident_Table.case_json,
        Incident_Table.sync_log,
        Incident_Table._id,
        Incident_Table._rev,
        Incident_Table.unique_id,
        Incident_Table.unique_identifier,
        Incident_Table.short_id,
        Incident_Table.registration_date,
        Incident_Table.created_by,
        Incident_Table.server_url,
        Incident_Table.owned_by,
        Incident_Table.created_date,
        Incident_Table.last_updated_date,
        Incident_Table.last_synced_date,
        Incident_Table.type,
        Incident_Table.isAudioSynced,
        Incident_Table.is_invalidated,
        Incident_Table.note_alerts
    };

    @Override
    public List<Incident> getAll(String ownedBy, String url) {
        return SQLite
                .select(selectFields)
                .from(Incident.class)
                .where(Incident_Table.owned_by.eq(ownedBy))
                .and(Incident_Table.server_url.eq(url))
                .orderBy(Incident_Table.registration_date, false)
                .queryList();
    }

    @Override
    public Incident getIncidentByUniqueId(String uniqueId) {
        return SQLite.select(selectFields).from(Incident.class).where(Incident_Table.unique_id.eq(uniqueId))
                .querySingle();
    }

    @Override
    public List<Incident> getAllIncidentsOrderByDate(boolean isASC, String ownedBy, String url) {
        return isASC ? getIncidentsByDateASC(ownedBy, url) : getIncidentsByDateDES(ownedBy, url);
    }

    @Override
    public List<Incident> getAllIncidentsOrderByAge(boolean isASC, String ownedBy, String url) {
        return isASC ? getIncidentsByAgeASC(ownedBy, url) : getIncidentsByAgeDES(ownedBy, url);
    }

    @Override
    public List<Incident> getIncidentListByOperatorGroup(String ownedBy, String url, OperatorGroup operatorGroup) {
        return SQLite.select(selectFields).from(Incident.class)
                .where(operatorGroup)
                .and(Incident_Table.owned_by.eq(ownedBy))
                .and(Incident_Table.server_url.eq(url))
                .orderBy(Incident_Table.registration_date, false)
                .queryList();
    }

    @Override
    public Incident getIncidentById(long incidentId) {
        return SQLite.select(selectFields).from(Incident.class).where(Incident_Table.id.eq(incidentId))
                .querySingle();
    }

    @Override
    public Incident getByInternalId(String id) {
        return SQLite.select(selectFields).from(Incident.class).where(Incident_Table._id.eq(id)).querySingle();
    }

    @Override
    public Incident getFirst() {
        return SQLite.select().from(Incident.class).querySingle();
    }

    @Override
    public Incident save(Incident incident) {
        incident.save();
        return incident;
    }

    @Override
    public Incident update(Incident incident) {
        incident.update();
        return incident;
    }

    @Override
    public List<Incident> getAllIncidentsByCaseUniqueId(String caseUniqueId) {
        return SQLite
                .select(selectFields)
                .from(Incident.class)
                .where(OperatorGroup.clause().and(Incident_Table.case_unique_id.eq(caseUniqueId)))
                .orderBy(Incident_Table.registration_date, false)
                .queryList();
    }

    @Override
    public Incident delete(Incident deleteIncident) {
        if (deleteIncident != null) {
            deleteIncident.delete();
        }
        return deleteIncident;
    }

    @Override
    public List<Incident> getALLSyncedRecords(String ownedBy) {
        return SQLite.select(Incident_Table.id)
                .from(Incident.class)
                .where(Incident_Table.is_synced.eq(true))
                .and(Incident_Table.owned_by.eq(ownedBy))
                .queryList();
    }

    private List<Incident> getIncidentsByAgeASC(String ownedBy, String url) {
        return getCurrentServerCondition(ownedBy, url)
                .orderBy(Incident_Table.age, true)
                .queryList();
    }

    private List<Incident> getIncidentsByAgeDES(String ownedBy, String url) {
        return getCurrentServerCondition(ownedBy, url)
                .orderBy(Incident_Table.age, false)
                .queryList();
    }

    private List<Incident> getIncidentsByDateASC(String ownedBy, String url) {
        return getCurrentServerCondition(ownedBy, url)
                .orderBy(Incident_Table.registration_date, true)
                .queryList();
    }

    private List<Incident> getIncidentsByDateDES(String ownedBy, String url) {
        return getCurrentServerCondition(ownedBy, url)
                .orderBy(Incident_Table.registration_date, false)
                .queryList();
    }

    private Where<Incident> getCurrentServerCondition(String ownedBy, String url) {
        IProperty[] selectedListFields = new IProperty[]{
                Incident_Table.id,
                Incident_Table.name,
                Incident_Table.age,
                Incident_Table._id,
                Incident_Table.unique_id,
                Incident_Table.unique_identifier,
                Incident_Table.short_id,
                Incident_Table.registration_date,
                Incident_Table.owned_by,
                Incident_Table.type,
                Incident_Table.is_invalidated,
                Incident_Table.note_alerts
        };
        
        return SQLite
                .select(selectedListFields)
                .from(Incident.class)
                .where(Incident_Table.owned_by.eq(ownedBy))
                .and(Incident_Table.server_url.eq(url));
    }
}
