package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.unicef.rapidreg.utils.TextUtils;

import java.util.Date;


public class RecordModel extends BaseModel {
    public static final int CASE = 0;
    public static final int TRACING = 1;

    public static final String ALERT_NOTE_TYPE = "notes";
    public static final String ALERT_KEY = "alerts";
    public static final String ALERT_PROP = "form_sidebar_id";
    public static final String ALERT_PROP_DATE = "date";

    public static final int EMPTY_AGE = -1;
    public static final String COLUMN_UNIQUE_ID = "unique_id";
    public static final String COLUMN_SHORT_ID = "short_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_CAREGIVER = "caregiver";
    public static final String COLUMN_REGISTRATION_DATE = "registration_date";
    public static final String COLUMN_CREATED_BY = "created_by";
    public static final String COLUMN_OWNED_BY = "owned_by";
    public static final String COLUMN_LOCATION = "location";


    @PrimaryKey(autoincrement = true)
    public long id;
    @Column(name = "name", defaultValue = "")
    private String name = "";
    @Column(name = "age")
    private int age;
    @Column(name = "caregiver", defaultValue = "")
    private String caregiver = "";
    @Column(name = "case_json")
    private Blob content;
    @Column(name = "audio")
    private Blob audio;
    @Column(name = "is_synced")
    private boolean isSynced;
    @Column(name = "sync_log")
    private String syncLog;
    @Column(name = "_id")
    private String internalId;
    @Column(name = "_rev")
    private String internalRev;
    @Column(name = "unique_id")
    private String uniqueId;
    @Column(name = "unique_identifier")
    private String uniqueIdentifier;
    @Column(name = "short_id")
    private String shortId;
    @Column(name = "registration_date")
    private Date registrationDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "server_url")
    private String serverUrl;
    @Column(name = "owned_by")
    private String ownedBy;
    @Column(name = "created_date")
    private Date createDate;
    @Column(name = "last_updated_date")
    private Date lastUpdatedDate;
    @Column(name = "last_synced_date")
    private Date lastSyncedDate;
    @Column(name = "type")
    private int type;

    @Column
    private boolean isAudioSynced;

    @Column(name = "is_invalidated")
    private boolean isInvalidated;

    @Column(name = "note_alerts")
    private String noteAlerts;

    public RecordModel(long id) {
        this.id = id;
    }

    public RecordModel() {
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(String ownedBy) {
        this.ownedBy = ownedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCaregiver() {
        return caregiver;
    }

    public void setCaregiver(String caregiver) {
        this.caregiver = caregiver;
    }

    public Blob getContent() {
        return content;
    }

    public void setContent(Blob content) {
        this.content = content;
    }

    public Blob getAudio() {
        return audio;
    }

    public void setAudio(Blob audio) {
        this.audio = audio;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public String getSyncLog() {
        return syncLog;
    }

    public void setSyncLog(String syncLog) {
        this.syncLog = syncLog;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getInternalRev() {
        return internalRev;
    }

    public void setInternalRev(String internalRev) {
        this.internalRev = internalRev;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUniqueIdentifier() { return uniqueIdentifier; }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Date getLastSyncedDate() {
        return lastSyncedDate;
    }

    public void setLastSyncedDate(Date lastSyncedDate) {
        this.lastSyncedDate = lastSyncedDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isAudioSynced() {
        return isAudioSynced;
    }

    public void setAudioSynced(boolean audioSynced) {
        isAudioSynced = audioSynced;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public boolean isInvalidated() { return isInvalidated; }

    public void setInvalidated(boolean invalidated) { isInvalidated = invalidated; }

    public void setNoteAlerts(String noteAlerts) { this.noteAlerts = noteAlerts; }

    public String getNoteAlerts() { return noteAlerts; }

    public boolean hasNoteAlerts() {
        boolean hasNoteAlert = false;

        if (!TextUtils.isEmpty(getNoteAlerts())) {
            for (String as : getNoteAlerts().split(",")) {
                if (as.equals(ALERT_NOTE_TYPE)) {
                    hasNoteAlert = true;
                }
            }
        }

        return hasNoteAlert;
    }


    @Override
    public String toString() {
        return "RecordModel{" +
                "isAudioSynced=" + isAudioSynced +
                ", type=" + type +
                ", lastSyncedDate=" + lastSyncedDate +
                ", lastUpdatedDate=" + lastUpdatedDate +
                ", createDate=" + createDate +
                ", ownedBy='" + ownedBy + '\'' +
                ", serverUrl='" + serverUrl + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", registrationDate=" + registrationDate +
                ", shortId='" + shortId + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                ", internalRev='" + internalRev + '\'' +
                ", internalId='" + internalId + '\'' +
                ", syncLog='" + syncLog + '\'' +
                ", isSynced=" + isSynced +
                ", isInvalidated=" + isInvalidated +
                ", audio=" + audio +
                ", content=" + content +
                ", caregiver='" + caregiver + '\'' +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
