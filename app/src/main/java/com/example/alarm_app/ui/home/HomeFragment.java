package com.example.alarm_app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alarm_app.R;
import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.model.AlarmDto;
import com.example.alarm_app.alarmserver.model.Time;
import com.example.alarm_app.ui.modifyalarm.AddUpdateAlarmActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton btnAddNewAlarm;
    private TextView textNextAlarmWillBe;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

//        Recycle view all alarms
        recyclerView = root.findViewById(R.id.recycle_view_all_alarms);
        AlarmRecyclerViewAdapter adapter = new AlarmRecyclerViewAdapter(
                inflater.getContext(), getParentFragmentManager());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

//        Add alarm sheet
        btnAddNewAlarm = root.findViewById(R.id.button_add_new_alarm);
        btnAddNewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddUpdateAlarmActivity.class);
                startActivity(intent);
            }
        });


        textNextAlarmWillBe = root.findViewById(R.id.text_view_next_alarm_be);
        AlarmService.getInstance().addListener(new AlarmService.OnDataSetChanged() {
            @Override
            public void dataChanged() {
                String strNextAlar = "";
                List<AlarmDto> sortedAlarmsWithOutNotActive = AlarmService.getInstance().getSortedAlarmsWithOutNotActive();

                if (sortedAlarmsWithOutNotActive.size() == 0) {
                    strNextAlar = getString(R.string.text_view_next_alarm_be);
                }
                else {
                    AlarmDto alarmDto = sortedAlarmsWithOutNotActive.get(0);
                    Time time = alarmDto.getTime();
//                    Add day of week or date
                    strNextAlar = time.toString();
                }
                textNextAlarmWillBe.setText(strNextAlar);
            }
        });
        return root;
    }

}