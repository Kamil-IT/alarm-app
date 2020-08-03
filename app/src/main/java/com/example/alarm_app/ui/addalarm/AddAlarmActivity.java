package com.example.alarm_app.ui.addalarm;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddAlarmActivity extends AppCompatActivity {

    private Button btnCostume, btnRingtone, btnTurnOfType, btnSnooze;
    private FloatingActionButton btnAddNewAlarm;
    private Chip chipMon, chipTue, chipWed, chipThu, chipFri, chipSat, chipSun;
    private TimePicker timePicker;
    private TextView textAlarmBe, textCostume, textRingtone, textTurnOfType, textSnooze;
    private EditText textLabel;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        mContext = this;

        initVariables();
        setDefaultValues();

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TODO: textAlarmBe show time to alarm
            }
        });

        btnCostume.setOnClickListener(getOnClickListenerForBtnCostume());
        btnTurnOfType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialogWithItems(R.array.alarm_turn_off_type, R.string.turn_off_type, textTurnOfType).show();
            }
        });
        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialogWithItems(R.array.snooze_duration, R.string.snooze, textSnooze).show();
            }
        });
        btnRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialogWithItems(R.array.ringtone_types, R.string.ringtone, textRingtone).show();
            }
        });

        btnAddNewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Time time = new Time(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                List<AlarmFrequencyType> alarmFrequencyTypes = frequencyStringPrepareStringFrequencyType();

                if (alarmFrequencyTypes.isEmpty() && textCostume.getText().length() != 0) {
                    throw new IllegalArgumentException("alarmFrequencyTypes.isEmpty() have to be empty or textCostume.getText().length() != 0");
                }
            }
        });

    }

    private AlertDialog createAlertDialogWithItems(@ArrayRes final int arrayItemsId,
                                                   @StringRes int titleId,
                                                   final TextView textView) {
        return new MaterialAlertDialogBuilder(mContext)
                .setTitle(titleId)
                .setNegativeButton(R.string.cancel, null)
                .setItems(arrayItemsId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String[] turnOfTypes = getResources().getStringArray(arrayItemsId);
                        textView.setText(turnOfTypes[which]);
                    }
                })
                .create();
    }

    private View.OnClickListener getOnClickListenerForBtnCostume() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText(R.string.costume)
                        .build();
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        Calendar instance = Calendar.getInstance();
                        instance.setTimeInMillis(selection);
                        Date time = instance.getTime();
                        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd, MMM yyyy");

                        textCostume.setText(formatter.format(time));
                    }
                });
                datePicker.show(getSupportFragmentManager(), String.valueOf(R.string.costume));
            }
        };
    }

    private void setDefaultValues() {
        textLabel.setInputType(InputType.TYPE_NULL);
        timePicker.setIs24HourView(true);
        textTurnOfType.setText(getResources().getStringArray(R.array.alarm_turn_off_type)[0]);
        textSnooze.setText(getResources().getStringArray(R.array.snooze_duration)[5]);
        textRingtone.setText(getResources().getStringArray(R.array.ringtone_types)[0]);
    }

    private void initVariables() {
        btnCostume = findViewById(R.id.button_costume);
        btnRingtone = findViewById(R.id.button_ringtone);
        btnTurnOfType = findViewById(R.id.button_turn_off_type);
        btnSnooze = findViewById(R.id.button_snooze);
        btnAddNewAlarm = findViewById(R.id.button_add_new_alarm);

        chipMon = findViewById(R.id.monday_chip);
        chipTue = findViewById(R.id.tuesday_chip);
        chipWed = findViewById(R.id.wednesday_chip);
        chipThu = findViewById(R.id.thursday_chip);
        chipFri = findViewById(R.id.friday_chip);
        chipSat = findViewById(R.id.saturday_chip);
        chipSun = findViewById(R.id.sunday_chip);

        timePicker = findViewById(R.id.time_picker_add_alarm);

        textAlarmBe = findViewById(R.id.text_time_to_start_alarm);

        textLabel = findViewById(R.id.label);

        textCostume = findViewById(R.id.text_costume);
        textRingtone = findViewById(R.id.text_ringtone);
        textTurnOfType = findViewById(R.id.text_turn_of_type);
        textSnooze = findViewById(R.id.text_snooze);
    }

    private List<AlarmFrequencyType> frequencyStringPrepareStringFrequencyType() {
        List<AlarmFrequencyType> alarmFrequencyTypes = new ArrayList<>();
        if (chipMon.isChecked()) alarmFrequencyTypes.add(AlarmFrequencyType.MONDAY);
        if (chipTue.isChecked()) alarmFrequencyTypes.add(AlarmFrequencyType.TUESDAY);
        if (chipWed.isChecked()) alarmFrequencyTypes.add(AlarmFrequencyType.WEDNESDAY);
        if (chipThu.isChecked()) alarmFrequencyTypes.add(AlarmFrequencyType.THURSDAY);
        if (chipFri.isChecked()) alarmFrequencyTypes.add(AlarmFrequencyType.FRIDAY);
        if (chipSat.isChecked()) alarmFrequencyTypes.add(AlarmFrequencyType.SATURDAY);
        if (chipSun.isChecked()) alarmFrequencyTypes.add(AlarmFrequencyType.SUNDAY);
        return alarmFrequencyTypes;
    }
}