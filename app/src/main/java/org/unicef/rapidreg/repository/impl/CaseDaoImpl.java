package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;

import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.Case_Table;
import org.unicef.rapidreg.repository.CaseDao;

import java.util.List;

public class CaseDaoImpl implements CaseDao {
    public static IProperty[] selectFields = new IProperty[]{
            Case_Table.id,
            Case_Table.location,
            Case_Table.name,
            Case_Table.age,
            Case_Table.caregiver,
            Case_Table.case_json,
            Case_Table.is_synced,
            Case_Table.sync_log,
            Case_Table._id,
            Case_Table._rev,
            Case_Table.unique_id,
            Case_Table.unique_identifier,
            Case_Table.short_id,
            Case_Table.registration_date,
            Case_Table.created_by,
            Case_Table.server_url,
            Case_Table.owned_by,
            Case_Table.created_date,
            Case_Table.last_updated_date,
            Case_Table.last_synced_date,
            Case_Table.type,
            Case_Table.isAudioSynced,
            Case_Table.is_invalidated,
            Case_Table.note_alerts
    };

    @Override
    public List<Case> getAll(String ownedBy, String url) {
        return SQLite
                .select(selectFields)
                .from(Case.class)
                .where(Case_Table.owned_by.eq(ownedBy))
                .and(Case_Table.server_url.eq(url))
                .orderBy(Case_Table.registration_date, false)
                .queryList();
    }

    @Override
    public Case getCaseByUniqueId(String uniqueId) {
        return SQLite.select(selectFields).from(Case.class).where(Case_Table.unique_id.eq(uniqueId))
                .querySingle();
    }

    @Override
    public List<Case> getAllCasesOrderByDate(boolean isASC, String ownedBy, String url) {
        return isASC ? getCasesByDateASC(ownedBy, url) : getCasesByDateDES(ownedBy, url);
    }

    @Override
    public List<Case> getAllCasesOrderByDateAndNoteAlert(boolean isASC, String ownedBy, String url) {
        return isASC ? getCasesByDateASCAndNoteAlerts(ownedBy, url) : getCasesByDateDESAndNoteAlerts(ownedBy, url);
    }

    @Override
    public List<Case> getAllCasesOrderByAge(boolean isASC, String ownedBy, String url) {
        return isASC ? getCasesByAgeASC(ownedBy, url) : getCasesByAgeDES(ownedBy, url);
    }

    @Override
    public List<Case> getCaseListByConditionGroup(String ownedBy, String url, ConditionGroup conditionGroup) {
        return SQLite.select(selectFields).from(Case.class)
                .where(conditionGroup)
                .and(Case_Table.owned_by.eq(ownedBy))
                .and(Case_Table.server_url.eq(url))
                .orderBy(Case_Table.registration_date, false)
                .queryList();
    }

    @Override
    public Case getCaseById(long caseId) {
        return SQLite.select(selectFields).from(Case.class).where(Case_Table.id.eq(caseId)).querySingle();
    }

    @Override
    public Case getByInternalId(String id) {
        return SQLite.select(selectFields).from(Case.class).where(Case_Table._id.eq(id)).querySingle();
    }

    @Override
    public Case save(Case childCase) {
        childCase.save();
        return childCase;
    }

    @Override
    public Case update(Case childCase) {
        childCase.update();
        return childCase;
    }

    @Override
    public Case deleteByRecordId(Long recordId) {
        Case childCase = getCaseById(recordId);
        if (childCase != null) {
            childCase.delete();
        }
        return childCase;
    }

    @Override
    public Case delete(Case deleteCase) {
        if (deleteCase != null) {
            deleteCase.delete();
        }
        return deleteCase;
    }

    @Override
    public List<Case> getALLSyncedRecords(String ownedBy) {
        return SQLite.select(Case_Table.id)
                .from(Case.class)
                .where(Case_Table.is_synced.eq(true))
                .and(Case_Table.owned_by.eq(ownedBy))
                .queryList();
    }

    private List<Case> getCasesByAgeASC(String ownedBy, String url) {
        return getCurrentServerUserCondition(ownedBy, url)
                .orderBy(Case_Table.age, true)
                .queryList();
    }

    private List<Case> getCasesByAgeDES(String ownedBy, String url) {
        return getCurrentServerUserCondition(ownedBy, url)
                .orderBy(Case_Table.age, false)
                .queryList();
    }

    private List<Case> getCasesByDateASC(String ownedBy, String url) {
        return getCurrentServerUserCondition(ownedBy, url)
                .orderBy(Case_Table.registration_date, true)
                .queryList();
    }

    private List<Case> getCasesByDateDES(String ownedBy, String url) {
        return getCurrentServerUserCondition(ownedBy, url)
                .orderBy(Case_Table.registration_date, false)
                .queryList();
    }

    private List<Case> getCasesByDateASCAndNoteAlerts(String ownedBy, String url) {
        return getCurrentServerUserCondition(ownedBy, url)
                .and(Case_Table.note_alerts.like("%notes%"))
                .orderBy(Case_Table.last_note_alert_date, true)
                .orderBy(Case_Table.last_updated_date, true)
                .orderBy(Case_Table.registration_date, true)
                .queryList();
    }

    private List<Case> getCasesByDateDESAndNoteAlerts(String ownedBy, String url) {
        return getCurrentServerUserCondition(ownedBy, url)
                .and(Case_Table.note_alerts.like("%notes%"))
                .orderBy(Case_Table.last_note_alert_date, false)
                .orderBy(Case_Table.last_updated_date, false)
                .orderBy(Case_Table.registration_date, false)
                .queryList();
    }

    private Where<Case> getCurrentServerUserCondition(String ownedBy, String url) {
        IProperty[] selectedListFields = new IProperty[]{
                Case_Table.id,
                Case_Table.name,
                Case_Table.age,
                Case_Table._id,
                Case_Table.unique_id,
                Case_Table.unique_identifier,
                Case_Table.short_id,
                Case_Table.registration_date,
                Case_Table.owned_by,
                Case_Table.type,
                Case_Table.is_invalidated,
                Case_Table.note_alerts,
                Case_Table.last_note_alert_date
        };

        return SQLite
                .select(selectedListFields)
                .from(Case.class)
                .where(Case_Table.owned_by.eq(ownedBy))
                .and(Case_Table.server_url.eq(url));
    }
}
