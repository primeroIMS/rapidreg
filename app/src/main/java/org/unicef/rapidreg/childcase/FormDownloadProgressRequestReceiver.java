package org.unicef.rapidreg.childcase;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.StringRes;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.utils.Utils;

public class FormDownloadProgressRequestReceiver extends BroadcastReceiver {
    private ProgressBar formSyncProgressBar;
    private TextView formSyncTxt;
    private AlertDialog syncFormsProgressDialog;

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

    public ProgressBar getFormSyncProgressBar() {
        return formSyncProgressBar;
    }

    public void setFormSyncProgressBar(ProgressBar formSyncProgressBar) {
        this.formSyncProgressBar = formSyncProgressBar;
    }

    public TextView getFormSyncTxt() {
        return formSyncTxt;
    }

    public void setFormSyncTxt(TextView formSyncTxt) {
        this.formSyncTxt = formSyncTxt;
    }

    public AlertDialog getSyncFormsProgressDialog() {
        return syncFormsProgressDialog;
    }

    public void setSyncFormsProgressDialog(AlertDialog syncFormsProgressDialog) {
        this.syncFormsProgressDialog = syncFormsProgressDialog;
    }
}
