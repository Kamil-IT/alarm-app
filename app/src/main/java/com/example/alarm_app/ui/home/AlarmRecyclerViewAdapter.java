package com.example.alarm_app.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.alarm_app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.ViewHolder> {

    List<String> strings = Arrays.asList("asdasda", "asdasd", "gsf", "dasda", "asdas", "dasda", "asdas");

    Context context;

    public AlarmRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
//        Impl data to fields
    }

    @Override
    public int getItemCount() {
        return strings.size();
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
