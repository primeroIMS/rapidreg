package org.unicef.rapidreg.repository;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;

import org.unicef.rapidreg.model.Incident;

import java.util.List;

public interface IncidentDao {

    List<Incident> getAll(String ownedBy, String url);

    Incident getIncidentByUniqueId(String id);

    List<Incident> getAllIncidentsOrderByDate(boolean isASC, String ownedBy, String url);

    List<Incident> getAllIncidentsOrderByAge(boolean isASC, String ownedBy, String url);

    List<Incident> getIncidentListByOperatorGroup(String ownedBy, String url, OperatorGroup operatorGroup);

    Incident getIncidentById(long incidentId);

    Incident getByInternalId(String id);

    Incident getFirst();

    Incident save(Incident incident);

    Incident update(Incident incident);

    List<Incident> getAllIncidentsByCaseUniqueId(String caseUniqueId);

    Incident delete(Incident deleteIncident);

    List<Incident> getALLSyncedRecords(String ownedBy);
}
