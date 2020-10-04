package com.devcivil.alarm_app.ui.alarmmodify;

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

import com.devcivil.alarm_app.MainActivity;
import com.devcivil.alarm_app.R;
import com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType;
import com.devcivil.alarm_app.alarmserver.model.RingType;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.CUSTOM;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.FRIDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.MONDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.SATURDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.SUNDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.THURSDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.TUESDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.WEDNESDAY;

public class ModifyAlarmActivity extends AppCompatActivity {

    private Button btnCostume, btnRingtone, btnTurnOfType, btnSnooze, btnAddNewAlarm;
    private Chip chipMon, chipTue, chipWed, chipThu, chipFri, chipSat, chipSun;
    private TimePicker timePicker;
    private TextView textAlarmBe, textCostume, textRingtone, textTurnOfType, textSnooze;
    private EditText textLabel;
    private ScrollView scrollViewMain;
    private Context mContext;

    private ModifyAlarmVewModel modifyAlarmModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modifyAlarmModel = new ViewModelProvider(this).get(ModifyAlarmVewModel.class);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_add_alarm);

        modifyAlarmModel.prepareDataFromIntent(getIntent());
        initVariables();
        setDefaultValues();
        createListenersToSimpleChoose();

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
                modifyAlarmModel.createAlertDialogWithItems(ModifyAlarmActivity.this, R.array.snooze_duration, R.string.snooze, textSnooze).show();
            }
        });
        btnRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlarmRingingDialogViewAdapter adapter = new AlarmRingingDialogViewAdapter();
                final AlertDialog alertDialog = new MaterialAlertDialogBuilder(mContext)
                        .setTitle(R.string.ringtone)
                        .setNegativeButton(R.string.cancel, null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                adapter.killAllAlarms();
                            }
                        })
                        .create();
                View view = getLayoutInflater().inflate(R.layout.content_alarm_ringing_recycler, null);
                RecyclerView recycleViewSounds = view.findViewById(R.id.recycle_view_all_sounds);
                adapter.setOnClickRingtoneListener(new AlarmRingingDialogViewAdapter.OnClickRingtoneListener() {
                    @Override
                    public void onClick(RingType ringType) {
                        modifyAlarmModel.setRingTypeGiven(ringType);
                        textRingtone.setText(getResources().getStringArray(R.array.ringtone_types)[(int) ringType.getId()]);
                        alertDialog.dismiss();
                    }
                });
                recycleViewSounds.setAdapter(adapter);
                recycleViewSounds.setLayoutManager(new LinearLayoutManager(ModifyAlarmActivity.this));
                alertDialog.setView(view);

                alertDialog.show();
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
                        textCostume.setText(modifyAlarmModel.dateFormatter.format(time));
                    }
                });
                datePicker.show(getSupportFragmentManager(), getString(R.string.costume));
            }
        });

        btnAddNewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyAlarmModel.createNewAlarm(
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute(),
                        0,
                        textLabel.getText().toString(),
                        getFrequencyTypesFromView(),
                        textCostume.getText().toString()
                );
//                DialogAlarmSummary dialogAlarmSummary = new DialogAlarmSummary(
//                        ModifyAlarmActivity.class,
//                        modifyAlarmModel.getInfoAboutValues(time, alarmFrequencyTypes, label, finalCostumeDate),
//                        isToUpdate) {
//                    @Override
//                    public void onClickedPositiveButton(DialogInterface dialog, int which) {
//                        super.onClickedPositiveButton(dialog, which);
//                        Intent intent = new Intent(mContext, MainActivity.class);
//                        startActivity(intent);
//                    }
//                };
//                dialogAlarmSummary.show();

                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    private void setDefaultValues() {
        textLabel.setInputType(InputType.TYPE_NULL);
        timePicker.setIs24HourView(true);

        textLabel.setText(modifyAlarmModel.getAlarmDefault().getName());
        textTurnOfType.setText(getResources().getStringArray(R.array.alarm_turn_off_type)[(int) modifyAlarmModel.getAlarmDefault().getAlarmTurnOffType().getId()]);
        textSnooze.setText(getResources().getStringArray(R.array.snooze_duration)[(int) modifyAlarmModel.getAlarmDefault().getSnooze().getId()]);
        textRingtone.setText(getResources().getStringArray(R.array.ringtone_types)[(int) modifyAlarmModel.getAlarmDefault().getRingType().getId()]);

        if (modifyAlarmModel.getAlarmDefault().getAlarmFrequencyType().contains(MONDAY))
            chipMon.setChecked(true);
        if (modifyAlarmModel.getAlarmDefault().getAlarmFrequencyType().contains(TUESDAY))
            chipTue.setChecked(true);
        if (modifyAlarmModel.getAlarmDefault().getAlarmFrequencyType().contains(WEDNESDAY))
            chipWed.setChecked(true);
        if (modifyAlarmModel.getAlarmDefault().getAlarmFrequencyType().contains(THURSDAY))
            chipThu.setChecked(true);
        if (modifyAlarmModel.getAlarmDefault().getAlarmFrequencyType().contains(FRIDAY))
            chipFri.setChecked(true);
        if (modifyAlarmModel.getAlarmDefault().getAlarmFrequencyType().contains(SATURDAY))
            chipSat.setChecked(true);
        if (modifyAlarmModel.getAlarmDefault().getAlarmFrequencyType().contains(SUNDAY))
            chipSun.setChecked(true);

        if (modifyAlarmModel.getAlarmDefault().getAlarmFrequencyType().contains(CUSTOM)) {
            if (modifyAlarmModel.getAlarmDefault().getAlarmFrequencyCostume().size() != 0) {
                com.devcivil.alarm_app.alarmserver.model.Date date = modifyAlarmModel.getAlarmDefault().getAlarmFrequencyCostume().get(0);
                Calendar calendar = Calendar.getInstance();
                calendar.set(date.getYear(), date.getMonth(), date.getDay());
                textCostume.setText(modifyAlarmModel.dateFormatter.format(calendar.getTime()));
            }
        }


        if (modifyAlarmModel.getAlarmDefault().getTime() != null) {
            timePicker.setCurrentHour(modifyAlarmModel.getAlarmDefault().getTime().getHours());
            timePicker.setCurrentMinute(modifyAlarmModel.getAlarmDefault().getTime().getMinutes());
        }

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