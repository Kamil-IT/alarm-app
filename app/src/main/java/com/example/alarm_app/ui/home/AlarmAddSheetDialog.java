package com.example.alarm_app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.alarm_app.R;
import com.example.alarm_app.ui.addalarm.AddAlarmActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.zip.Inflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AlarmAddSheetDialog extends BottomSheetDialogFragment {

    private Button btnAddNewAlarm, btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sheet_add_alarm, container, false);

        btnAddNewAlarm = v.findViewById(R.id.button_add_new_alarm_sheet);
        btnCancel = v.findViewById(R.id.button_cancel_new_alarm_sheet);

        btnAddNewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                setOnClickListenerButtonAddNewAlarm();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return v;
    }

    private void setOnClickListenerButtonAddNewAlarm() {
        Intent intent = new Intent(getContext(), AddAlarmActivity.class);
        startActivity(intent);
    }
}
