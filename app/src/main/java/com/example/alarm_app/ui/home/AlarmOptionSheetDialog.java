package com.example.alarm_app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.alarm_app.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AlarmOptionSheetDialog extends BottomSheetDialogFragment {

    private Button btnUpdateAlarm, btnDelete, btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sheet_alarm_options, container, false);

        btnUpdateAlarm = v.findViewById(R.id.button_update_option_alarm_sheet);
        btnCancel = v.findViewById(R.id.button_cancel_option_alarm_sheet);
        btnDelete = v.findViewById(R.id.button_delete_option_alarm_sheet);

        btnUpdateAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
//                TODO: implement it like add new alarm
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                //                TODO: implement it like add new alarm
            }
        });

        return v;
    }
}
