package org.unicef.rapidreg.incident.incidentregister;

import android.os.Bundle;
import android.view.View;

import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.incident.IncidentFeature;
import org.unicef.rapidreg.service.RecordService;

import java.lang.ref.WeakReference;

import static org.unicef.rapidreg.service.CaseService.CASE_ID;

public class IncidentFormSwitcherClickListener implements View.OnClickListener {
    private final WeakReference<IncidentRegisterFragment> incidentRegisterFragment;
    private final int[] anim_to_mini;

    public IncidentFormSwitcherClickListener(
            final WeakReference<IncidentRegisterFragment> incidentRegisterFragment,
            final int[] anim_to_mini) {
        this.incidentRegisterFragment = incidentRegisterFragment;
        this.anim_to_mini = anim_to_mini;
    }

    @Override
    public void onClick(View view) {
        IncidentRegisterFragment currentFragment = incidentRegisterFragment.get();
        if (currentFragment != null) {
            Bundle args = new Bundle();
            args.putSerializable(RecordService.ITEM_VALUES, currentFragment.getRecordRegisterData());
            args.putSerializable(RecordService.VERIFY_MESSAGE, currentFragment.getFieldValueVerifyResult());
            args.putString(CASE_ID, currentFragment.getArguments().getString(CASE_ID, null));

            RecordActivity recordActivity = (RecordActivity) currentFragment.getActivity();

            Feature feature = recordActivity.getCurrentFeature().isDetailMode() ?
                    IncidentFeature.DETAILS_MINI : recordActivity.getCurrentFeature()
                    .isAddMode() ?
                    IncidentFeature.ADD_MINI : IncidentFeature.EDIT_MINI;
            recordActivity.turnToFeature(feature, args, this.anim_to_mini);
        }
    }
}
