package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.unicef.rapidreg.PrimeroDatabaseConfiguration;

import java.util.Date;

@Table(database = PrimeroDatabaseConfiguration.class)
public class Case extends RecordModel {

    @Column(name = COLUMN_LOCATION)
    private String location;

    @Column(name = "last_note_alert_date")
    private Date lastNoteAlertDate;

    public Case() {
    }

    public Case(long id) {
        super(id);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getLastNoteAlertDate() {
        return lastNoteAlertDate;
    }

    public void setLastNoteAlertDate(Date lastNoteAlertDate) {
        this.lastNoteAlertDate = lastNoteAlertDate;
    }
}
