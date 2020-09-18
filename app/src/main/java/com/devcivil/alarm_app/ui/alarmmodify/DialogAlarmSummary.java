package com.devcivil.alarm_app.ui.alarmmodify;

import android.content.Context;
import android.content.DialogInterface;

import com.devcivil.alarm_app.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import androidx.annotation.NonNull;

public class DialogAlarmSummary extends MaterialAlertDialogBuilder {

    private boolean isToUpdate;

    public DialogAlarmSummary(@NonNull Context context, List<String> infoAboutValues, boolean isToUpdated) {
        super(context);
        this.isToUpdate = isToUpdated;

        setItems(infoAboutValues.toArray(new CharSequence[0]), null);
        setNegativeButton(R.string.cancel, null);
        setPositiveButton();
        setTitle();
        create();
    }

    private void setPositiveButton() {
        int positiveButtonRes;
        if (isToUpdate){
            positiveButtonRes = R.string.update;
        } else {
            positiveButtonRes = R.string.create;
        }
        super.setPositiveButton(positiveButtonRes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickedPositiveButton(dialog, which);
            }
        });
    }

    /**
     * Method to override
     */
    public void onClickedPositiveButton(DialogInterface dialog, int which) {
        dialog.dismiss();
    }

    private void setTitle() {
        if (isToUpdate) {
            super.setTitle(R.string.update);
        } else {
            super.setTitle(R.string.create);
        }

    }
}
