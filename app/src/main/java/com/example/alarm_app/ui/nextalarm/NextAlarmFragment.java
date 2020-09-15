package com.example.alarm_app.ui.nextalarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.alarm_app.R;
import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.AlarmStaticService;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.example.alarm_app.alarmserver.ConnectionToAlarmServer.isNetworkConnected;

public class NextAlarmFragment extends Fragment {

    private NextAlarmViewModel nextAlarmViewModel;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private AlarmStaticService.OnDataSetChanged listenerRefresh = new AlarmStaticService.OnDataSetChanged() {
        @Override
        public void dataChanged() {
            swipeRefresh.setRefreshing(false);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        nextAlarmViewModel =
                ViewModelProviders.of(this).get(NextAlarmViewModel.class);
        View root = inflater.inflate(R.layout.fragment_next_alarm, container, false);

        //        Recycle view all alarms
        recyclerView = root.findViewById(R.id.recycle_view_all_alarms);
        AlarmNextRecyclerViewAdapter adapter = new AlarmNextRecyclerViewAdapter(
                inflater.getContext(), getParentFragmentManager());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        swipeRefresh = root.findViewById(R.id.swipe_to_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AlarmService.getInstance().addListener(listenerRefresh);
                if (isNetworkConnected(requireContext())){
                    AlarmService.getInstance().updateAlarmsFromServer(getContext());
                } else {
                    Toast.makeText(getContext(), R.string.error_con_to_service, Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                }

            }
        });

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AlarmService.getInstance().removeListener(listenerRefresh);
    }
}