package org.unicef.rapidreg.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatEditText;

public class VerifyWithoutPopupEditText extends AppCompatEditText {
    public VerifyWithoutPopupEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        setCompoundDrawables(null, null, icon, null);
    }
}
