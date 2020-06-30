package org.unicef.rapidreg.incident.incidentregister;

import android.os.Bundle;
import android.view.View;
import android.util.Log;

import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.incident.IncidentFeature;
import org.unicef.rapidreg.service.RecordService;

import java.lang.ref.WeakReference;

import static org.unicef.rapidreg.service.CaseService.CASE_ID;

public class IncidentFormSwitcherClickListener<T extends RecordRegisterFragment>  implements View.OnClickListener {
    private final WeakReference<T> recordRegisterFragment;
    private final int[] anim_to;
    private final IncidentFeature detailsMode;
    private final IncidentFeature editMode;
    private final IncidentFeature addMode;

    public IncidentFormSwitcherClickListener(
            final WeakReference<T> recordRegisterFragment,
            final IncidentFeature detailsMode,
            final IncidentFeature editMode,
            final IncidentFeature addMode,
            final int[] anim_to) {
        this.recordRegisterFragment = recordRegisterFragment;
        this.anim_to = anim_to;
        this.detailsMode = detailsMode;
        this.editMode = editMode;
        this.addMode = addMode;
    }

    @Override
    public void onClick(View view) {
        T currentFragment = recordRegisterFragment.get();
        if (currentFragment != null) {
            Bundle args = new Bundle();
            args.putSerializable(RecordService.ITEM_VALUES, currentFragment.getRecordRegisterData());
            args.putSerializable(RecordService.VERIFY_MESSAGE, currentFragment.getFieldValueVerifyResult());
            args.putString(CASE_ID, currentFragment.getArguments().getString(CASE_ID, null));

            RecordActivity recordActivity = (RecordActivity) currentFragment.getActivity();

            Feature feature = recordActivity.getCurrentFeature().isDetailMode() ?
                    detailsMode : recordActivity.getCurrentFeature()
                    .isAddMode() ? addMode :editMode;
            recordActivity.turnToFeature(feature, args, this.anim_to);
        }
    }
}
