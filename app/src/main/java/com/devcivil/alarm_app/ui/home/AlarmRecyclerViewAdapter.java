package com.devcivil.alarm_app.ui.home;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devcivil.alarm_app.R;
import com.devcivil.alarm_app.alarmserver.AlarmService;
import com.devcivil.alarm_app.alarmserver.model.AlarmDto;
import com.devcivil.alarm_app.alarmserver.model.Date;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.FRIDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.MONDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.SATURDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.SUNDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.THURSDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.TUESDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.WEDNESDAY;

public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.ViewHolder> {

    private AlarmService alarmService;
    private Context mContext;
    private FragmentManager fragmentManager;

    public AlarmRecyclerViewAdapter(Context mContext, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
        this.alarmService = AlarmService.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final AlarmDto alarm = Objects.requireNonNull(alarmService.getAllAlarms()).get(position);

        holder.checkBoxIsActive.setActivated(true);
        holder.checkBoxIsActive.setChecked(alarm.getActive());
        holder.textAlarmTime.setText(alarm.getTime().toString());
        holder.textDaysWhenAlarmPlay.setText(getDaysWhenAlarmPlay(holder.itemView.getContext().getResources(), alarm));
        holder.textDesc.setText(alarm.getName());
        holder.btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmOptionSheetDialog sheet = new AlarmOptionSheetDialog(alarm);
                sheet.show(fragmentManager, "Option for alarm nr " + position);
            }
        });

        holder.checkBoxIsActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.setActive(holder.checkBoxIsActive.isChecked());
                alarmService.updateAlarm(mContext, alarm);
            }
        });
    }

    private String getDaysWhenAlarmPlay(Resources resources, AlarmDto alarm) {
        StringBuilder stringBuilder = new StringBuilder();
        if (alarm.getAlarmFrequencyType().contains(MONDAY))
            stringBuilder.append(resources.getStringArray(R.array.week_days)[(int) MONDAY.getId() - 1] ).append(", ");
        if (alarm.getAlarmFrequencyType().contains(TUESDAY))
            stringBuilder.append(resources.getStringArray(R.array.week_days)[(int) TUESDAY.getId() - 1] ).append(", ");
        if (alarm.getAlarmFrequencyType().contains(WEDNESDAY))
            stringBuilder.append(resources.getStringArray(R.array.week_days)[(int) WEDNESDAY.getId() - 1] ).append(", ");
        if (alarm.getAlarmFrequencyType().contains(THURSDAY))
            stringBuilder.append(resources.getStringArray(R.array.week_days)[(int) THURSDAY.getId() - 1] ).append(", ");
        if (alarm.getAlarmFrequencyType().contains(FRIDAY))
            stringBuilder.append(resources.getStringArray(R.array.week_days)[(int) FRIDAY.getId() - 1] ).append(", ");
        if (alarm.getAlarmFrequencyType().contains(SATURDAY))
            stringBuilder.append(resources.getStringArray(R.array.week_days)[(int) SATURDAY.getId() - 1] ).append(", ");
        if (alarm.getAlarmFrequencyType().contains(SUNDAY))
            stringBuilder.append(resources.getStringArray(R.array.week_days)[(int) SUNDAY.getId() - 1] ).append(", ");

        if (alarm.getAlarmFrequencyCostume() != null){
            for (Date date :
                    alarm.getAlarmFrequencyCostume()) {
                stringBuilder.append(date.toString()).append(", ");
            }
        }

        return stringBuilder.toString().substring(0, stringBuilder.toString().length() -2);
    }

    @Override
    public int getItemCount() {
        return alarmService.getAllAlarms().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textAlarmTime, textDaysWhenAlarmPlay, textDesc;
        private SwitchMaterial checkBoxIsActive;
        private FloatingActionButton btnOption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textAlarmTime = itemView.findViewById(R.id.text_view_alarm_time);
            textDaysWhenAlarmPlay = itemView.findViewById(R.id.text_view_days_alarm_play);
            textAlarmTime = itemView.findViewById(R.id.text_view_alarm_time);
            textDesc = itemView.findViewById(R.id.text_view_alarm_description);
            checkBoxIsActive = itemView.findViewById(R.id.check_box_is_active);
            btnOption = itemView.findViewById(R.id.floating_action_button_option);
        }
    }
}
