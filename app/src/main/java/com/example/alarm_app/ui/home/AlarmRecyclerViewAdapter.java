package com.example.alarm_app.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alarm_app.R;
import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.model.AlarmDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.ViewHolder> {

    private AlarmService alarmService;
    private Context mContext;
    private FragmentManager fragmentManager;

    public AlarmRecyclerViewAdapter(Context mContext, FragmentManager fragmentManager) {
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
        final AlarmDto alarm = Objects.requireNonNull(alarmService.getAllAlarms()).get(position);

        holder.checkBoxIsActive.setActivated(true);
        holder.checkBoxIsActive.setChecked(alarm.getActive());
        holder.textAlarmTime.setText(alarm.getTime().toString());
        holder.textDaysWhenAlarmPlay.setText(alarm.getAlarmFrequencyType().toString());
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
