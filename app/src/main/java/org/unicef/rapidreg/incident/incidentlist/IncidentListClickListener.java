package org.unicef.rapidreg.incident.incidentlist;

import android.os.Bundle;
import android.view.View;

import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.incident.IncidentFeature;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.utils.StreamUtil;
import org.unicef.rapidreg.utils.Utils;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static org.unicef.rapidreg.service.RecordService.AUDIO_FILE_PATH;

public class IncidentListClickListener implements View.OnClickListener {
    private final WeakReference<RecordActivity> recordActivity;
    private final long recordId;
    private final RecordModel record;

    public IncidentListClickListener(
            final WeakReference<RecordActivity> recordActivity,
            final long recordId,
            final RecordModel record) {
        this.recordActivity = recordActivity;
        this.recordId = recordId;
        this.record = record;
    }

    @Override
    public void onClick(View view) {
        if (recordActivity.get() != null) {
            Bundle args = new Bundle();
            args.putLong(IncidentService.INCIDENT_PRIMARY_ID, recordId);
            recordActivity.get().turnToFeature(IncidentFeature.DETAILS_MINI, args, null);
            try {
                Utils.clearAudioFile(AUDIO_FILE_PATH);
                if (record.getAudio() != null) {
                    StreamUtil.writeFile(record.getAudio().getBlob(), RecordService
                            .AUDIO_FILE_PATH);
                }
            } catch (IOException e) {
            }
        }
    }
}
