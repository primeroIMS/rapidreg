package org.unicef.rapidreg.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {
    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
