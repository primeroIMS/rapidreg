package org.unicef.rapidreg.utils;


import android.content.Context;
import android.nfc.FormatException;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.primitives.Ints;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.exception.LocaleNotFoundException;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static String toStringResult(List<String> result) {
        String res = "";

        if (result == null) {
            return res;
        }

        for (int i = 0; i < result.size(); i++) {
            String item = result.get(i);
            if (i == 0) {
                res += item;
            } else {
                res += ", " + item;
            }
        }

        return res;
    }

    public static Map convert(String mapString) {
        Map res = new HashMap<>();

        String[] pairs = mapString.substring(1, mapString.length() - 1).split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.trim().split("=");

            checkFormat(keyValue.length);
            res.put(keyValue[0], keyValue[1]);
        }
        return res;
    }

    private static void checkFormat(int len) {
        if (len < 2) {
            throw new IllegalArgumentException();
        }
    }

    public static Date getRegisterDateAsDdMmYyyy(String registrationDateString) {
        SimpleDateFormat registrationDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        try {
            java.util.Date date = registrationDateFormat.parse(registrationDateString);
            return new Date(date.getTime());
        } catch (ParseException e) {
            return new Date(System.currentTimeMillis());
        }
    }

    public static Date getRegisterDateByYyyyMmDd(String registrationDateString) {
        SimpleDateFormat registrationDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        try {
            java.util.Date date = registrationDateFormat.parse(registrationDateString);
            return new Date(date.getTime());
        } catch (ParseException e) {
            return new Date(System.currentTimeMillis());
        }
    }

    public static String parseDisplayDate(final String date, final String locale) {
        String formatedDate = "";
        Date parsedDate;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        DateFormat dateFormat = null;

        try {
            dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale(locale));
        } catch (LocaleNotFoundException e) {
            Log.e("parseDisplayDate", "Could not find locale");
        }

        try {
           parsedDate = simpleDateFormat.parse(date);
           formatedDate =  dateFormat.format(parsedDate);
        } catch (ParseException e) {
            Log.e("parseDisplayDate", "Could not parse date");
        }

        return formatedDate;
    }

    public static Locale getLocale(final String locale) throws LocaleNotFoundException {
        for (Locale loc : Locale.getAvailableLocales()) {
            if (loc.toString().equals(locale.replace("-", "_"))) {
                return loc;
            }
        }

        throw new LocaleNotFoundException("Locale not found");
    }

    public static void clearAudioFile(String fileName) {
        File file = new File(fileName);
        file.delete();
    }

    public static void showMessageByToast(Context context, int ResId, int duration) {
        Toast toast = Toast.makeText(context.getApplicationContext(), ResId, duration);
        initToastStyle(context, toast);
    }

    public static void showMessageByToast(Context context, String message, int duration) {
        Toast toast = Toast.makeText(context.getApplicationContext(), message, duration);
        initToastStyle(context, toast);
    }

    private static void initToastStyle(Context context, Toast toast) {
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(context.getResources().getDimension(R.dimen.toast_text_size));
        toast.show();
    }

    public static boolean isErrorCode(final int errorCode) {
        return (errorCode >= 400 && errorCode <= 600);
    }

}
