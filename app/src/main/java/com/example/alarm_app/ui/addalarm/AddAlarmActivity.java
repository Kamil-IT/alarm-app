package com.example.alarm_app.ui.addalarm;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.alarm_app.R;
import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.model.AlarmDto;
import com.example.alarm_app.alarmserver.model.AlarmFrequencyType;
import com.example.alarm_app.alarmserver.model.RingType;
import com.example.alarm_app.alarmserver.model.Snooze;
import com.example.alarm_app.alarmserver.model.Time;
import com.example.alarm_app.alarmserver.model.TurnOffType;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.CUSTOM;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.FRIDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.MONDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.SATURDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.SINGLE;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.SUNDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.THURSDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.TUESDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.WEDNESDAY;

public class AddAlarmActivity extends AppCompatActivity {

    private Button btnCostume, btnRingtone, btnTurnOfType, btnSnooze;
    private FloatingActionButton btnAddNewAlarm;
    private Chip chipMon, chipTue, chipWed, chipThu, chipFri, chipSat, chipSun;
    private TimePicker timePicker;
    private TextView textAlarmBe, textCostume, textRingtone, textTurnOfType, textSnooze;
    private EditText textLabel;
    private Context mContext;

    private TurnOffType turnOffTypeGiven;
    private Snooze snoozeGiven;
    private RingType ringTypeGiven;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        mContext = this;

        initVariables();
        setDefaultValues();
        createListenersToSimpleChoose();

        btnAddNewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Time time = new Time(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                final Set<AlarmFrequencyType> alarmFrequencyTypes = getFrequencyTypesFromView();
                final RingType ringType = ringTypeGiven;
                final TurnOffType turnOffType = turnOffTypeGiven;
//                TODO: make converter
                final Snooze snooze = snoozeGiven;
                String label = textLabel.getText().toString();

                if (alarmFrequencyTypes.isEmpty() && textCostume.getText().length() != 0) {
                    throw new IllegalArgumentException("alarmFrequencyTypes.isEmpty() have to be empty or textCostume.getText().length() != 0");
                }
                List<String> infoAboutValues = new ArrayList<>();
                infoAboutValues.add("Time: " + time);
                infoAboutValues.add("Alarm frequency types: " + alarmFrequencyTypes);
                infoAboutValues.add("Ring Type: " + textRingtone.getText().toString());
                infoAboutValues.add("Turn Off Type: " + textTurnOfType.getText().toString());
                infoAboutValues.add("Snooze: " + textSnooze.getText().toString());
                infoAboutValues.add("Label: " + label);

                new MaterialAlertDialogBuilder(mContext)
                        .setItems(infoAboutValues.toArray(new CharSequence[0]), null)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                AlarmDto alarmDto = new AlarmDto(
                                        "",
                                        time,
                                        ringType,
                                        alarmFrequencyTypes,
                                        true,
                                        Collections.<com.example.alarm_app.alarmserver.model.Date>emptyList(),
                                        turnOffType,
                                        snooze
                                );
                                AlarmService.getInstance().creteAlarm(mContext, alarmDto);
                            }
                        })
                        .setTitle(R.string.create_new_alarm)
                .create()
                .show();
            }
        });

    }

    private void createListenersToSimpleChoose() {
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TODO: Implement it as counter to alarm start
                textAlarmBe.setText("");
            }
        });

        textLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(mContext);
                editText.setHint(R.string.label);

                if (textLabel.getText().toString().length() != 0) {
                    editText.setText(textLabel.getText().toString());
                }
                new MaterialAlertDialogBuilder(mContext)
                        .setTitle(R.string.label)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                textLabel.setText(editText.getText());
                            }
                        })
                        .setView(editText)
                        .create().show();
            }
        });

        btnCostume.setOnClickListener(new View.OnClickListener() {
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
        });

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
    }

    private AlertDialog createAlertDialogWithItems(@ArrayRes final int arrayItemsId, @StringRes final int titleId, final TextView textView) {
        return new MaterialAlertDialogBuilder(mContext)
                .setTitle(titleId)
                .setNegativeButton(R.string.cancel, null)
                .setItems(arrayItemsId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String[] turnOfTypes = getResources().getStringArray(arrayItemsId);
                        textView.setText(turnOfTypes[which]);

                        switch (titleId) {

                            case R.string.turn_off_type:
                                for (TurnOffType turnOffType :
                                        TurnOffType.values()) {
                                    if (turnOffType.getId() == which) {
                                        turnOffTypeGiven = turnOffType;
                                        break;
                                    }
                                }
                                break;
                            case R.string.snooze:
                                for (Snooze snooze :
                                        Snooze.values()) {
                                    if (snooze.getId() == which) {
                                        snoozeGiven = snooze;
                                        break;
                                    }
                                }
                                break;
                            case R.string.ringtone:
                                for (RingType ringtone :
                                        RingType.values()) {
                                    if (ringtone.getId() == which) {
                                        ringTypeGiven = ringtone;
                                        break;
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create();
    }

    private void setDefaultValues() {
        textLabel.setInputType(InputType.TYPE_NULL);
        timePicker.setIs24HourView(true);
        textTurnOfType.setText(getResources().getStringArray(R.array.alarm_turn_off_type)[0]);
        textSnooze.setText(getResources().getStringArray(R.array.snooze_duration)[5]);
        textRingtone.setText(getResources().getStringArray(R.array.ringtone_types)[1]);

        turnOffTypeGiven = TurnOffType.NORMAL;
        snoozeGiven = Snooze.MIN_5;
        ringTypeGiven = RingType.BIRDS;
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

    private Set<AlarmFrequencyType> getFrequencyTypesFromView() {
        Set<AlarmFrequencyType> alarmFrequencyTypes = new HashSet<>();
        if (chipMon.isChecked()) alarmFrequencyTypes.add(MONDAY);
        if (chipTue.isChecked()) alarmFrequencyTypes.add(TUESDAY);
        if (chipWed.isChecked()) alarmFrequencyTypes.add(WEDNESDAY);
        if (chipThu.isChecked()) alarmFrequencyTypes.add(THURSDAY);
        if (chipFri.isChecked()) alarmFrequencyTypes.add(FRIDAY);
        if (chipSat.isChecked()) alarmFrequencyTypes.add(SATURDAY);
        if (chipSun.isChecked()) alarmFrequencyTypes.add(SUNDAY);
        if (textLabel.getText().toString().length() != 0) alarmFrequencyTypes.add(CUSTOM);
        if (alarmFrequencyTypes.size() == 0) alarmFrequencyTypes.add(SINGLE);
        return alarmFrequencyTypes;
    }
}