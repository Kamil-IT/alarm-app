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

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.ViewHolder> {

    private AlarmService alarmService;
    private Context context;
    private FragmentManager fragmentManager;

    public AlarmRecyclerViewAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        AlarmDto alarm = alarmService.getAllAlarms().get(position);

        holder.checkBoxIsActive.setActivated(alarm.getActive());
        holder.textAlarmTime.setText(alarm.getTime().toString());
//        TODO: add costumes message when create enum for it
        holder.textDaysWhenAlarmPlay.setText(alarm.getAlarmFrequencyType());
        holder.textDesc.setText(alarm.getDescription());

        holder.btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmOptionSheetDialog sheet = new AlarmOptionSheetDialog();
                sheet.show(fragmentManager, "Option for alarm nr " + position);
                //        TODO: Add implementation to option button
            }
        });
    }

    @Override
    public int getItemCount() {
        if (alarmService.getAllAlarms() == null){
            return 0;
        }
        return alarmService.getAllAlarms().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

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
