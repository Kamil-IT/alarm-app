package com.example.alarm_app.ui.alarmmodify;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.alarm_app.MainActivity;
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
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.CUSTOM;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.FRIDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.MONDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.SATURDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.SUNDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.THURSDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.TUESDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.WEDNESDAY;

public class ModifyAlarmActivity extends AppCompatActivity {

    public static final String EXTRA_ID_ALARM_UPDATE = "alarm_to_update";
    public static final String EXTRA_ID_IS_UPDATE = "is_to_update";
    private Boolean isToUpdate;

    private Button btnCostume, btnRingtone, btnTurnOfType, btnSnooze;
    private FloatingActionButton btnAddNewAlarm;
    private Chip chipMon, chipTue, chipWed, chipThu, chipFri, chipSat, chipSun;
    private TimePicker timePicker;
    private TextView textAlarmBe, textCostume, textRingtone, textTurnOfType, textSnooze;
    private EditText textLabel;
    private ScrollView scrollViewMain;
    private Context mContext;

    private TurnOffType turnOffTypeGiven;
    private Snooze snoozeGiven;
    private RingType ringTypeGiven;

    private AlarmDto alarmDefault = new AlarmDto(
            null,
            "",
            "",
            "",
            null,
            0L,
            RingType.BIRDS,
            null,
            Collections.<AlarmFrequencyType>emptyList(),
            true,
            Collections.<com.example.alarm_app.alarmserver.model.Date>emptyList(),
            TurnOffType.NORMAL,
            Snooze.MIN_5
    );

    @SuppressLint("SimpleDateFormat")
    DateFormat dateFormatter = new SimpleDateFormat("dd, MMM yyyy");
    @SuppressLint("SimpleDateFormat")
    DateFormat dateFormatterToService = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_add_alarm);

        prepareDataFromIntent();
        initVariables();
        setDefaultValues();
        createListenersToSimpleChoose();

    }

    private void prepareDataFromIntent() {
        Gson gson = new Gson();
        AlarmDto alarmDto = gson.fromJson(getIntent().getStringExtra(EXTRA_ID_ALARM_UPDATE), AlarmDto.class);
        isToUpdate = getIntent().getBooleanExtra(EXTRA_ID_IS_UPDATE, false);
        if (alarmDto != null) {
            alarmDefault = alarmDto;
            isToUpdate = true;
        }
    }

    private void createListenersToSimpleChoose() {
//        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//            @Override
//            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//                StringBuilder timeToStartAlarm = new StringBuilder();
//                timeToStartAlarm.append(getString(R.string.alarm_will_start_in));
//                timeToStartAlarm.append(" ");
//                textAlarmBe.setText("");
//            }
//        });

        btnTurnOfType.setVisibility(View.GONE);
        textTurnOfType.setVisibility(View.GONE);
//        btnTurnOfType.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createAlertDialogWithItems(R.array.alarm_turn_off_type, R.string.turn_off_type, textTurnOfType).show();
//            }
//        });
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
                        textCostume.setText(dateFormatter.format(time));
                    }
                });
                datePicker.show(getSupportFragmentManager(), getString(R.string.costume));
            }
        });

        final Context context = this;
        btnAddNewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Time time = new Time(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                final List<AlarmFrequencyType> alarmFrequencyTypes = getFrequencyTypesFromView();
                final RingType ringType = ringTypeGiven;
                final TurnOffType turnOffType = turnOffTypeGiven;
                final Snooze snooze = snoozeGiven;
                final String label = textLabel.getText().toString();
                String costumeDate;
                try {
                    costumeDate = dateFormatterToService.format(
                            Objects.requireNonNull(
                                    dateFormatter.parse(
                                            textCostume.getText().toString())));
                } catch (ParseException e) {
                    costumeDate = "";
                }

                if (alarmFrequencyTypes.isEmpty() && textCostume.getText().length() != 0) {
                    throw new IllegalArgumentException("alarmFrequencyTypes.isEmpty() have to be empty or textCostume.getText().length() != 0");
                }

                final String finalCostumeDate = costumeDate;
                DialogAlarmSummary dialogAlarmSummary = new DialogAlarmSummary(
                        context,
                        getInfoAboutValues(time, alarmFrequencyTypes, label, finalCostumeDate),
                        isToUpdate) {
                    @Override
                    public void onClickedPositiveButton(DialogInterface dialog, int which) {
                        super.onClickedPositiveButton(dialog, which);
                        List<com.example.alarm_app.alarmserver.model.Date> dates = new ArrayList<>();
//                                Checked is costume
                        if (alarmFrequencyTypes.contains(CUSTOM) && textCostume.getText().toString().length() == 0) {
                            Calendar currentDate = Calendar.getInstance();
                            currentDate.setTime(new Date());
                            currentDate.set(Calendar.HOUR_OF_DAY, time.getHours());
                            currentDate.set(Calendar.MINUTE, time.getMinutes());
                            currentDate.set(Calendar.SECOND, time.getSeconds());
                            if (currentDate.getTime().before(new Date())){
                                dates.add(new com.example.alarm_app.alarmserver.model.Date(
                                        currentDate.get(Calendar.DAY_OF_MONTH) + 1,
                                        currentDate.get(Calendar.MONTH) + 1,
                                        currentDate.get(Calendar.YEAR)));
                            } else {
                                dates.add(new com.example.alarm_app.alarmserver.model.Date(
                                        currentDate.get(Calendar.DAY_OF_MONTH),
                                        currentDate.get(Calendar.MONTH) + 1,
                                        currentDate.get(Calendar.YEAR)));
                            }
                        } else if (!finalCostumeDate.equals("")) {
                            dates.add(new com.example.alarm_app.alarmserver.model.Date(
                                    Integer.valueOf(finalCostumeDate.substring(0, 2)),
                                    Integer.valueOf(finalCostumeDate.substring(3, 5)),
                                    Integer.valueOf(finalCostumeDate.substring(6))));
                        }

                        AlarmDto alarmDto = new AlarmDto(
                                label,
                                time,
                                ringType,
                                alarmFrequencyTypes,
                                true,
                                dates,
                                turnOffType,
                                snooze
                        );
                        alarmDto.setTimeCreateInMillis(System.currentTimeMillis());
                        if (isToUpdate) {
                            alarmDto.setId(alarmDefault.getId());
                            alarmDto.setDescription(alarmDefault.getDescription());
                            alarmDto.setRingName(alarmDefault.getRingName());
                            alarmDto.setTimeCreateInMillis(alarmDefault.getTimeCreateInMillis());
                            AlarmService.getInstance().updateAlarm(mContext, alarmDto);
                        } else {
                            AlarmService.getInstance().creteAlarm(mContext, alarmDto);
                        }
                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);
                    }
                };
                dialogAlarmSummary.show();
            }
        });
    }

    private List<String> getInfoAboutValues(Time time, List<AlarmFrequencyType> alarmFrequencyTypes, String label, String costumeDate) {
        List<String> infoAboutValues = new ArrayList<>();
        infoAboutValues.add("Time: " + time);
        infoAboutValues.add("Custom date: " + costumeDate);
        StringBuilder stringBuilder = new StringBuilder();
        for (AlarmFrequencyType frequencyType :
                alarmFrequencyTypes) {
            if (frequencyType.getId() <= 7) {
                stringBuilder.append(getResources().getStringArray(
                        R.array.week_days)[(int) frequencyType.getId() - 1])
                        .append(" ");
            } else if (frequencyType == CUSTOM){
                stringBuilder.append(getBaseContext().getString(R.string.single)).append(" ");
            }
        }
        infoAboutValues.add("Alarm frequency types: " + stringBuilder.toString());
        infoAboutValues.add("Ring Type: " + textRingtone.getText().toString());
        infoAboutValues.add("Turn Off Type: " + textTurnOfType.getText().toString());
        infoAboutValues.add("Snooze: " + textSnooze.getText().toString());
        infoAboutValues.add("Label: " + label);
        return infoAboutValues;
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

        textLabel.setText(alarmDefault.getName());
        textTurnOfType.setText(getResources().getStringArray(R.array.alarm_turn_off_type)[(int) alarmDefault.getAlarmTurnOffType().getId()]);
        textSnooze.setText(getResources().getStringArray(R.array.snooze_duration)[(int) alarmDefault.getSnooze().getId()]);
        textRingtone.setText(getResources().getStringArray(R.array.ringtone_types)[(int) alarmDefault.getRingType().getId()]);

        if (alarmDefault.getAlarmFrequencyType().contains(MONDAY)) chipMon.setChecked(true);
        if (alarmDefault.getAlarmFrequencyType().contains(TUESDAY)) chipTue.setChecked(true);
        if (alarmDefault.getAlarmFrequencyType().contains(WEDNESDAY)) chipWed.setChecked(true);
        if (alarmDefault.getAlarmFrequencyType().contains(THURSDAY)) chipThu.setChecked(true);
        if (alarmDefault.getAlarmFrequencyType().contains(FRIDAY)) chipFri.setChecked(true);
        if (alarmDefault.getAlarmFrequencyType().contains(SATURDAY)) chipSat.setChecked(true);
        if (alarmDefault.getAlarmFrequencyType().contains(SUNDAY)) chipSun.setChecked(true);

        if (alarmDefault.getAlarmFrequencyType().contains(CUSTOM)) {
            if (alarmDefault.getAlarmFrequencyCostume().size() != 0){
                com.example.alarm_app.alarmserver.model.Date date = alarmDefault.getAlarmFrequencyCostume().get(0);
                Calendar calendar = Calendar.getInstance();
                calendar.set(date.getYear(), date.getMonth(), date.getDay());
                textCostume.setText(dateFormatter.format(calendar.getTime()));
            }
        }


        if (alarmDefault.getTime() != null) {
            timePicker.setCurrentHour(alarmDefault.getTime().getHours());
            timePicker.setCurrentMinute(alarmDefault.getTime().getMinutes());
        }

        turnOffTypeGiven = alarmDefault.getAlarmTurnOffType();
        snoozeGiven = alarmDefault.getSnooze();
        ringTypeGiven = alarmDefault.getRingType();

        scrollViewMain.setFillViewport(true);
        textAlarmBe.setText("");
    }

    private void initVariables() {
        mContext = this;

        scrollViewMain = findViewById(R.id.scroll_view_fragment_add_alarm);

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

    private List<AlarmFrequencyType> getFrequencyTypesFromView() {
        List<AlarmFrequencyType> alarmFrequencyTypes = new ArrayList<>();
        if (chipMon.isChecked()) alarmFrequencyTypes.add(MONDAY);
        if (chipTue.isChecked()) alarmFrequencyTypes.add(TUESDAY);
        if (chipWed.isChecked()) alarmFrequencyTypes.add(WEDNESDAY);
        if (chipThu.isChecked()) alarmFrequencyTypes.add(THURSDAY);
        if (chipFri.isChecked()) alarmFrequencyTypes.add(FRIDAY);
        if (chipSat.isChecked()) alarmFrequencyTypes.add(SATURDAY);
        if (chipSun.isChecked()) alarmFrequencyTypes.add(SUNDAY);
        if (textCostume.getText().toString().length() != 0) alarmFrequencyTypes.add(CUSTOM);
        if (alarmFrequencyTypes.size() == 0) alarmFrequencyTypes.add(CUSTOM);
        return alarmFrequencyTypes;
    }
}