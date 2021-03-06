package com.devcivil.alarm_app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.devcivil.alarm_app.R;
import com.devcivil.alarm_app.alarmserver.AlarmService;
import com.devcivil.alarm_app.alarmserver.model.AlarmDto;
import com.devcivil.alarm_app.ui.alarmmodify.ModifyAlarmActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.devcivil.alarm_app.ui.alarmmodify.ModifyAlarmVewModel.EXTRA_ID_ALARM_UPDATE;
import static com.devcivil.alarm_app.ui.alarmmodify.ModifyAlarmVewModel.EXTRA_ID_IS_UPDATE;

public class AlarmOptionSheetDialog extends BottomSheetDialogFragment {

    private Button btnUpdateAlarm, btnDelete, btnCancel;
    private AlarmDto alarm;

    public AlarmOptionSheetDialog(AlarmDto alarm) {
        super();
        this.alarm = alarm;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_alarm_options, container, false);

        btnUpdateAlarm = v.findViewById(R.id.button_update_option_alarm_sheet);
        btnCancel = v.findViewById(R.id.button_cancel_option_alarm_sheet);
        btnDelete = v.findViewById(R.id.button_delete_option_alarm_sheet);

        btnUpdateAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                setButtonUpdateAlarmOnClickListener();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                setButtonCancelAlarmOnClickListener();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                setButtonDeleteAlarmOnClickListener();
            }
        });
        return v;
    }

    public void setButtonDeleteAlarmOnClickListener() {
        if (alarm.getId() != null && !"".equals(alarm.getId())){
            AlarmService.getInstance().deleteById(getContext(), alarm.getId());
        } else {
            AlarmService.getInstance().deleteStaticByTimeCreate(alarm.getTimeCreateInMillis());
        }
    }

    public void setButtonCancelAlarmOnClickListener() {
    }

    public void setButtonUpdateAlarmOnClickListener() {
        Intent intent = new Intent(getContext(), ModifyAlarmActivity.class);
        Gson gson = new Gson();
        String alarmInJason = gson.toJson(alarm);
        intent.putExtra(EXTRA_ID_ALARM_UPDATE, alarmInJason);
        intent.putExtra(EXTRA_ID_IS_UPDATE, true);
        startActivity(intent);
    }
}
