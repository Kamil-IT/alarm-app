package com.example.alarm_app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.example.alarm_app.R;
import com.example.alarm_app.ui.addalarm.AddAlarmActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton btnAddNewAlarm;

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
                Intent intent = new Intent(getContext(), AddAlarmActivity.class);
                startActivity(intent);
//                AlarmAddSheetDialog sheet = new AlarmAddSheetDialog();
//                sheet.show(getParentFragmentManager(), "Add alarm bottom sheet");
            }
        });

        return root;
    }


}