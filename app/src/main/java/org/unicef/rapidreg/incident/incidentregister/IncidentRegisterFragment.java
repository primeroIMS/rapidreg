package org.unicef.rapidreg.incident.incidentregister;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.incident.IncidentFeature;
import org.unicef.rapidreg.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import static org.unicef.rapidreg.forms.Field.TYPE_INCIDENT_MINI_FORM_PROFILE;

public class IncidentRegisterFragment extends RecordRegisterFragment {
    @Inject
    IncidentRegisterPresenter incidentRegisterPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.formSwitcher.setOnClickListener(new IncidentFormSwitcherClickListener(
                new WeakReference<>(this),
                IncidentFeature.DETAILS_MINI,
                IncidentFeature.EDIT_MINI,
                IncidentFeature.ADD_MINI,
                ANIM_TO_MINI
        ));
    }


    @Override
    protected RecordRegisterAdapter createRecordRegisterAdapter() {
        List<Field> fields = incidentRegisterPresenter.getValidFields(FragmentPagerItem.getPosition(getArguments()));
        addProfileFieldForDetailsPage(0, TYPE_INCIDENT_MINI_FORM_PROFILE, fields);

        RecordRegisterAdapter recordRegisterAdapter = new RecordRegisterAdapter(getActivity(),
                fields,
                incidentRegisterPresenter.getDefaultItemValues(),
                incidentRegisterPresenter.getFieldValueVerifyResult(),
                false);
        return recordRegisterAdapter;
    }

    @Override
    public IncidentRegisterPresenter createPresenter() {
        return incidentRegisterPresenter;
    }

    @Override
    public void onSaveSuccessful(long recordId) {
        Utils.showMessageByToast(getActivity(), R.string.save_success, Toast.LENGTH_SHORT);
    }
}
