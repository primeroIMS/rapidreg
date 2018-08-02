package org.unicef.rapidreg.base.record.recordlist.spinner;

import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;

public enum SpinnerState {
    AGE_ASC(R.drawable.age_up, R.string.age_asc, R.string.age),
    AGE_DES(R.drawable.age_down, R.string.age_desc, R.string.age),
    REG_DATE_ASC(R.drawable.date_up, R.string.reg_date_asc, R.string.registration_date),
    REG_DATE_DES(R.drawable.date_down, R.string.reg_date_desc, R.string.registration_date),
    INQUIRY_DATE_ASC(R.drawable.date_up, R.string.inquiry_date_asc, R.string.inquiry_date),
    INQUIRY_DATE_DES(R.drawable.date_down, R.string.inquiry_date_desc, R.string.inquiry_date),
    INTERVIEW_DATE_ASC(R.drawable.date_up, R.string.interview_date_asc, R.string.date_of_interview),
    INTERVIEW_DATE_DES(R.drawable.date_down, R.string.interview_date_desc, R.string.date_of_interview);

    private int resId;
    private int longName;
    private int shortName;

    SpinnerState(final int resId, final int longName, final int shortName) {
        this.resId = resId;
        this.longName = longName;
        this.shortName = shortName;
    }

    public int getResId() {
        return resId;
    }

    public int getLongName() {
        return longName;
    }

    public int getShortName() {
        return shortName;
    }
}