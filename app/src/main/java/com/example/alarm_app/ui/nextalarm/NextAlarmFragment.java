package com.example.alarm_app.ui.nextalarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alarm_app.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NextAlarmFragment extends Fragment {

    private NextAlarmViewModel nextAlarmViewModel;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        nextAlarmViewModel =
                ViewModelProviders.of(this).get(NextAlarmViewModel.class);
        View root = inflater.inflate(R.layout.fragment_next_alarm, container, false);

        //        Recycle view all alarms
        recyclerView = root.findViewById(R.id.recycle_view_next_alarms);
        AlarmNextRecyclerViewAdapter adapter = new AlarmNextRecyclerViewAdapter(
                inflater.getContext(), getParentFragmentManager());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        return root;
    }
}