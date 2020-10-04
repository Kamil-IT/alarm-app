package com.devcivil.alarm_app.ui.alarmmodify;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.TextView;

import com.devcivil.alarm_app.R;
import com.devcivil.alarm_app.alarmserver.AlarmService;
import com.devcivil.alarm_app.alarmserver.model.AlarmDto;
import com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType;
import com.devcivil.alarm_app.alarmserver.model.RingType;
import com.devcivil.alarm_app.alarmserver.model.Snooze;
import com.devcivil.alarm_app.alarmserver.model.Time;
import com.devcivil.alarm_app.alarmserver.model.TurnOffType;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.AndroidViewModel;

import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.CUSTOM;

public class ModifyAlarmVewModel extends AndroidViewModel {

    public static final String EXTRA_ID_ALARM_UPDATE = "alarm_to_update";
    public static final String EXTRA_ID_IS_UPDATE = "is_to_update";
    private Boolean isToUpdate;

    private TurnOffType turnOffTypeGiven;
    private Snooze snoozeGiven;
    private RingType ringTypeGiven;

    @SuppressLint("SimpleDateFormat")
    final DateFormat dateFormatter = new SimpleDateFormat("dd, MMM yyyy");
    @SuppressLint("SimpleDateFormat")
    final DateFormat dateFormatterToService = new SimpleDateFormat("dd-MM-yyyy");

    private AlarmDto alarmDefault = new AlarmDto(
            null,
            "",
            "",
            null,
            0L,
            RingType.BIRDS,
            Collections.<AlarmFrequencyType>emptyList(),
            true,
            Collections.<com.devcivil.alarm_app.alarmserver.model.Date>emptyList(),
            TurnOffType.NORMAL,
            Snooze.MIN_5
    );

    public ModifyAlarmVewModel(@NonNull Application application) {
        super(application);
        this.turnOffTypeGiven = this.alarmDefault.getAlarmTurnOffType();
        this.snoozeGiven = this.alarmDefault.getSnooze();
        this.ringTypeGiven = this.alarmDefault.getRingType();
    }


    void prepareDataFromIntent(Intent intent) {
        Gson gson = new Gson();
        AlarmDto alarmDto = gson.fromJson(intent.getStringExtra(EXTRA_ID_ALARM_UPDATE), AlarmDto.class);
        isToUpdate = intent.getBooleanExtra(EXTRA_ID_IS_UPDATE, false);
        if (alarmDto != null) {
            alarmDefault = alarmDto;
            isToUpdate = true;
        }
    }

    AlertDialog createAlertDialogWithItems(Context context, @ArrayRes final int arrayItemsId, @StringRes final int titleId, final TextView textView) {
        return new MaterialAlertDialogBuilder(context)
                .setTitle(titleId)
                .setNegativeButton(R.string.cancel, null)
                .setItems(arrayItemsId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String[] turnOfTypes = getApplication().getResources().getStringArray(arrayItemsId);
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

    List<String> getInfoAboutValues(Time time, List<AlarmFrequencyType> alarmFrequencyTypes,
                                            String label, String costumeDate, TextView textRingtone,
                                            TextView textTurnOfType, TextView textSnooze) {
        List<String> infoAboutValues = new ArrayList<>();
        infoAboutValues.add("Time: " + time);
        infoAboutValues.add("Custom date: " + costumeDate);
        StringBuilder stringBuilder = new StringBuilder();
        for (AlarmFrequencyType frequencyType :
                alarmFrequencyTypes) {
            if (frequencyType.getId() <= 7) {
                stringBuilder.append(getApplication().getResources().getStringArray(
                        R.array.week_days)[(int) frequencyType.getId() - 1])
                        .append(" ");
            } else if (frequencyType == CUSTOM) {
                stringBuilder.append(getApplication().getBaseContext().getString(R.string.single)).append(" ");
            }
        }
        infoAboutValues.add("Alarm frequency types: " + stringBuilder.toString());
        infoAboutValues.add("Ring Type: " + textRingtone.getText().toString());
        infoAboutValues.add("Turn Off Type: " + textTurnOfType.getText().toString());
        infoAboutValues.add("Snooze: " + textSnooze.getText().toString());
        infoAboutValues.add("Label: " + label);
        return infoAboutValues;
    }

    void createNewAlarm(int hour, int minutes, int seconds, String label, List<AlarmFrequencyType> alarmFrequencyTypes, String costumeAlarms){
        Time time = new Time(hour, minutes, seconds);
        RingType ringType = ringTypeGiven;
        TurnOffType turnOffType = turnOffTypeGiven;
        Snooze snooze = snoozeGiven;
        String costumeDate;
        try {
            costumeDate = dateFormatterToService.format(
                    Objects.requireNonNull(
                            dateFormatter.parse(costumeAlarms)));
        } catch (ParseException e) {
            costumeDate = "";
        }

        if (alarmFrequencyTypes.isEmpty() && costumeAlarms.length() != 0) {
            throw new IllegalArgumentException("alarmFrequencyTypes.isEmpty() have to be empty or textCostume.getText().length() != 0");
        }

        List<com.devcivil.alarm_app.alarmserver.model.Date> dates = new ArrayList<>();
//                                Checked is costume
        if (alarmFrequencyTypes.contains(CUSTOM) && costumeDate.length() == 0) {
            Calendar currentDate = Calendar.getInstance();
            currentDate.setTime(new Date());
            currentDate.set(Calendar.HOUR_OF_DAY, time.getHours());
            currentDate.set(Calendar.MINUTE, time.getMinutes());
            currentDate.set(Calendar.SECOND, time.getSeconds());
            if (currentDate.getTime().before(new Date())) {
                dates.add(new com.devcivil.alarm_app.alarmserver.model.Date(
                        currentDate.get(Calendar.DAY_OF_MONTH) + 1,
                        currentDate.get(Calendar.MONTH) + 1,
                        currentDate.get(Calendar.YEAR)));
            } else {
                dates.add(new com.devcivil.alarm_app.alarmserver.model.Date(
                        currentDate.get(Calendar.DAY_OF_MONTH),
                        currentDate.get(Calendar.MONTH) + 1,
                        currentDate.get(Calendar.YEAR)));
            }
        } else if (!costumeDate.equals("")) {
            dates.add(new com.devcivil.alarm_app.alarmserver.model.Date(
                    Integer.valueOf(costumeDate.substring(0, 2)),
                    Integer.valueOf(costumeDate.substring(3, 5)),
                    Integer.valueOf(costumeDate.substring(6))));
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
            alarmDto.setTimeCreateInMillis(alarmDefault.getTimeCreateInMillis());
            AlarmService.getInstance().updateAlarm(getApplication(), alarmDto);
        } else {
            AlarmService.getInstance().creteAlarm(getApplication(), alarmDto);
        }
    }

    public TurnOffType getTurnOffTypeGiven() {
        return turnOffTypeGiven;
    }

    public void setTurnOffTypeGiven(TurnOffType turnOffTypeGiven) {
        this.turnOffTypeGiven = turnOffTypeGiven;
    }

    public Snooze getSnoozeGiven() {
        return snoozeGiven;
    }

    public void setSnoozeGiven(Snooze snoozeGiven) {
        this.snoozeGiven = snoozeGiven;
    }

    public RingType getRingTypeGiven() {
        return ringTypeGiven;
    }

    public void setRingTypeGiven(RingType ringTypeGiven) {
        this.ringTypeGiven = ringTypeGiven;
    }

    public AlarmDto getAlarmDefault() {
        return alarmDefault;
    }
}
