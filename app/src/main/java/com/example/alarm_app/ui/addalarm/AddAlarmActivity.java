package com.example.alarm_app.ui.addalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.alarm_app.R;
import com.example.alarm_app.alarmserver.model.AlarmFrequencyType;
import com.example.alarm_app.alarmserver.model.Time;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddAlarmActivity extends AppCompatActivity {

    private Button btnCostume, btnRingtone, btnTurnOfType, Snooze, btnAddNewAlarm;
    private Chip chipMon, chipTue, chipWed, chiThu, chipFri, chipSat, chipSun;
    private TimePicker timePicker;
    private TextView textAlarmBe;
    private EditText textLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        btnCostume = findViewById(R.id.button_costume);
        btnRingtone = findViewById(R.id.button_ringtone);
        btnTurnOfType = findViewById(R.id.button_turn_off_type);
        Snooze = findViewById(R.id.button_snooze);
        btnAddNewAlarm = findViewById(R.id.button_add_new_alarm);

        chipMon = findViewById(R.id.monday_chip);
        chipTue = findViewById(R.id.tuesday_chip);
        chipWed = findViewById(R.id.wednesday_chip);
        chiThu = findViewById(R.id.thursday_chip);
        chipFri = findViewById(R.id.friday_chip);
        chipSat = findViewById(R.id.saturday_chip);
        chipSun = findViewById(R.id.sunday_chip);

        timePicker = findViewById(R.id.time_picker_add_alarm);

        textAlarmBe = findViewById(R.id.text_time_to_start_alarm);

        textLabel = findViewById(R.id.label);

        textLabel.setInputType(InputType.TYPE_NULL);

        timePicker.setIs24HourView(true);
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TODO: textAlarmBe show time to alarm
            }
        });

//        btnAddNewAlarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Time time = new Time(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
//                List<AlarmFrequencyType> alarmFrequencyTypes = frequencyStringPrepareStringFrequencyType();
//
//            }
//        });

    }

    private List<AlarmFrequencyType> frequencyStringPrepareStringFrequencyType() {
        List<AlarmFrequencyType> alarmFrequencyTypes = new ArrayList<>();
        if (chipMon.isChecked()) alarmFrequencyTypes.add(AlarmFrequencyType.MONDAY);
        if (chipTue.isChecked()) alarmFrequencyTypes.add(AlarmFrequencyType.TUESDAY);
        if (chipWed.isChecked()) alarmFrequencyTypes.add(AlarmFrequencyType.WEDNESDAY);
        if (chiThu.isChecked())  alarmFrequencyTypes.add(AlarmFrequencyType.THURSDAY);
        if (chipFri.isChecked()) alarmFrequencyTypes.add(AlarmFrequencyType.FRIDAY);
        if (chipSat.isChecked()) alarmFrequencyTypes.add(AlarmFrequencyType.SATURDAY);
        if (chipSun.isChecked()) alarmFrequencyTypes.add(AlarmFrequencyType.SUNDAY);
        return alarmFrequencyTypes;
    }
}