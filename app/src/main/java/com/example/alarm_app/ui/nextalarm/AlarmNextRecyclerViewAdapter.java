package com.example.alarm_app.ui.nextalarm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alarm_app.R;
import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.model.AlarmFor14Days;
import com.example.alarm_app.alarmserver.model.Time;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmNextRecyclerViewAdapter extends RecyclerView.Adapter<AlarmNextRecyclerViewAdapter.ViewHolder> {

    private AlarmService alarmService;
    private Context mContext;
    private FragmentManager fragmentManager;

    public AlarmNextRecyclerViewAdapter(Context mContext, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
        this.alarmService = AlarmService.getInstance();

        alarmService.addListener(new AlarmService.OnDataSetChanged() {
            @Override
            public void dataChanged() {
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final AlarmFor14Days alarm = alarmService.getSortedActiveAlarmsFor14Days().get(position);
        Time time = new Time(alarm.getAlarmBe().getHours(),
                alarm.getAlarmBe().getMinutes(), alarm.getAlarmBe().getSeconds());
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        holder.checkBoxIsActive.setActivated(true);
        holder.checkBoxIsActive.setChecked(alarm.getActive());
        holder.textAlarmTime.setText(time.toString());
//        TODO: add costumes message when create enum for it
        holder.textDaysWhenAlarmPlay.setText(df.format(alarm.getAlarmBe()));
        holder.textDesc.setText(alarm.getName());
        holder.btnOption.setVisibility(View.GONE);


        holder.checkBoxIsActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.setActive(holder.checkBoxIsActive.isChecked());
//                TODO: disable next alarm add it after create service for keeping all alarms
//                alarmService.updateAlarm(mContext, alarm);
            }
        });
    }

    @Override
    public int getItemCount() {
        List<AlarmFor14Days> allAlarms = alarmService.getSortedActiveAlarmsFor14Days();
        if (allAlarms == null) {
            return 0;
        }
        return allAlarms.size();
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
