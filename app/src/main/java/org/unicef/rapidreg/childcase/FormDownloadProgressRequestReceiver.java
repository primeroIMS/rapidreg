package org.unicef.rapidreg.childcase;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.StringRes;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.utils.Utils;

public class FormDownloadProgressRequestReceiver extends BroadcastReceiver {
    private final ProgressBar formSyncProgressBar;
    private final TextView formSyncTxt;
    private final AlertDialog syncFormsProgressDialog;

    public FormDownloadProgressRequestReceiver(
            final ProgressBar formSyncProgressBar,
            final TextView formSyncTxt,
            final AlertDialog syncFormsProgressDialog) {
        this.formSyncProgressBar = formSyncProgressBar;
        this.formSyncTxt = formSyncTxt;
        this.syncFormsProgressDialog = syncFormsProgressDialog;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int progress = intent.getIntExtra("progress", 0);

        if (formSyncProgressBar != null && formSyncTxt != null) {
            formSyncProgressBar.setProgress(progress);
            formSyncTxt.setText(getProgressMessageStringID(context, intent.getStringExtra("resource")));
        }

        if (progress == 100) {
            if (syncFormsProgressDialog != null) {
                syncFormsProgressDialog.dismiss();
            }

            Utils.showMessageByToast(context, R.string.sync_pull_form_success_message, Toast.LENGTH_LONG);
        }
    }

    protected String getProgressMessageStringID(final Context context, final String message) {
        try {
            String packageName = context.getPackageName();
            @StringRes int resID = context.getResources().getIdentifier(message, "string", packageName);
            return context.getString(resID);
        } catch(Exception e) {
            return message;
        }
    }
}
