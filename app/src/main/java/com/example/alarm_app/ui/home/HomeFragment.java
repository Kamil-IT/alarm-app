package com.example.alarm_app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alarm_app.R;
import com.example.alarm_app.ui.alarmmodify.ModifyAlarmActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton btnAddNewAlarm;
    private TextView textNextAlarmWillBe;
    private SwipeRefreshLayout swipeRefresh;

    private Observer<String> observerNextAlarmText = new Observer<String>() {
        @Override
        public void onChanged(String s) {
            textNextAlarmWillBe.setText(s);
        }
    };
    private Observer<Boolean> observerIsDataRefreshed = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
            if (!aBoolean) {
                Toast.makeText(getContext(), R.string.error_con_to_service, Toast.LENGTH_SHORT).show();
            }
            swipeRefresh.setRefreshing(false);
        }
    };
    private Observer<Boolean> observerDataChangedAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        final Context context = requireContext();

//        Recycle view all alarms
        recyclerView = root.findViewById(R.id.recycle_view_all_alarms);
        final AlarmRecyclerViewAdapter adapter = new AlarmRecyclerViewAdapter(
                inflater.getContext(), getParentFragmentManager());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        observerDataChangedAdapter = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                adapter.notifyDataSetChanged();
            }
        };
        homeViewModel.getDataChanged().observe(getViewLifecycleOwner(), observerDataChangedAdapter);


//        Add alarm sheet
        btnAddNewAlarm = root.findViewById(R.id.button_add_new_alarm);
        btnAddNewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ModifyAlarmActivity.class);
                startActivity(intent);
            }
        });

        textNextAlarmWillBe = root.findViewById(R.id.text_view_next_alarm_be);
        homeViewModel.getTimeOfNextAlarmLiveData().observe(getViewLifecycleOwner(), observerNextAlarmText);

        swipeRefresh = root.findViewById(R.id.swipe_to_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homeViewModel.OnSwipeRefresh(context);
                homeViewModel.getIsDataRefreshLiveData().observe(getViewLifecycleOwner(), observerIsDataRefreshed);
            }
        });

        if(!homeViewModel.isPreviousStart(context)) {
            homeViewModel.previousStart(context);
            homeViewModel.dialogForFirstUsage(context).show();
        }

//        TODO: add info about non connection to internet and add possible to reconnect

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
//        textNextAlarmWillBe.setText(homeViewModel.getTimeOfNextAlarmLiveData().getValue());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homeViewModel.getTimeOfNextAlarmLiveData().removeObserver(observerNextAlarmText);
        homeViewModel.getIsDataRefreshLiveData().removeObserver(observerIsDataRefreshed);
        homeViewModel.getDataChanged().removeObserver(observerDataChangedAdapter);
    }
}