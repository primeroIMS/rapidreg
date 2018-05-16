package org.unicef.rapidreg.base.record.recordsearch;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.record.recordlist.RecordListView;
import org.unicef.rapidreg.exception.LocaleNotFoundException;
import org.unicef.rapidreg.utils.Utils;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class RecordSearchPresenter extends MvpBasePresenter<RecordListView> {
    protected Date getDate(String value) {
        Locale locale = null;

        try {
            locale = Utils.getLocale(PrimeroAppConfiguration.getDefaultLanguage());
        } catch (LocaleNotFoundException e) {
            Log.e("getDate", "Could not find locale");
            return null;
        }

        try {
            java.util.Date date = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, locale).parse(value);
            return new Date(date.getTime());
        } catch (ParseException e) {
            Log.e("getDate", "Could not parse date");
            return null;
        }
    }

    protected abstract List<Long> getSearchResult(Map<String, String> searchConditions);

    public static final class CONSTANT {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String AGE_FROM = "age_from";
        public static final String AGE_TO = "age_to";
        public static final String CAREGIVER = "caregiver";
        public static final String REGISTRATION_DATE = "registration_date";
        public static final String DATE_OF_INQUIRY = "date_of_inquiry";
        public static final String LOCATION = "location";
        public static final String SURVIVOR_CODE = "survivor_code";
        public static final String TYPE_OF_VIOLENCE = "type_of_violence";
    }
}
