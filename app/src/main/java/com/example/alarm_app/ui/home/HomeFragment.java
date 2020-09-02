package com.example.alarm_app.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alarm_app.R;
import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.AlarmStaticService;
import com.example.alarm_app.alarmserver.model.AlarmDto;
import com.example.alarm_app.alarmserver.model.AlarmFor14Days;
import com.example.alarm_app.alarmserver.model.RingType;
import com.example.alarm_app.alarmserver.model.Snooze;
import com.example.alarm_app.alarmserver.model.Time;
import com.example.alarm_app.alarmserver.model.TurnOffType;
import com.example.alarm_app.ui.account.LoginActivity;
import com.example.alarm_app.ui.account.SingUpActivity;
import com.example.alarm_app.ui.alarmmodify.ModifyAlarmActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.example.alarm_app.alarmserver.ConnectionToAlarmServer.isNetworkConnected;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.FRIDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.MONDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.SATURDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.SUNDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.THURSDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.TUESDAY;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.WEDNESDAY;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton btnAddNewAlarm;
    private TextView textNextAlarmWillBe;
    private SwipeRefreshLayout swipeRefresh;
    private String noUpcomingAlarms;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        noUpcomingAlarms = getContext().getString(R.string.text_view_next_alarm_be);

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
                Intent intent = new Intent(getContext(), ModifyAlarmActivity.class);
                startActivity(intent);
            }
        });

        textNextAlarmWillBe = root.findViewById(R.id.text_view_next_alarm_be);
        AlarmService.getInstance().addListener(new AlarmService.OnDataSetChanged() {
            @Override
            public void dataChanged() {
                if (getContext() != null){
                    addNextAlarmTime();
                }
            }
        });

        swipeRefresh = root.findViewById(R.id.swipe_to_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AlarmService.getInstance().addListener(new AlarmStaticService.OnDataSetChanged() {
                    @Override
                    public void dataChanged() {
                        swipeRefresh.setRefreshing(false);
                    }
                });
                if (isNetworkConnected(requireContext())) {
                    AlarmService.getInstance().updateAlarmsFromServer(getContext());
                } else {
                    Toast.makeText(getContext(), R.string.error_con_to_service, Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                }

            }
        });

//        TODO: add info about non connection to internet and add possible to reconnect

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        boolean previouslyStarted = prefs.getBoolean(root.getContext().getString(R.string.pref_previously_started), false);
        final Context context = root.getContext();
        if(!previouslyStarted) {
            AlarmService.getInstance().creteAlarm(root.getContext(),
                    new AlarmDto(
                            "",
                            "Time to school",
                            "Wake up to school",
                            "",
                            new Time(7,0,0),
                            System.currentTimeMillis(),
                            RingType.ALARM_CLASSIC,
                            null,
                            Arrays.asList(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY),
                            false,
                            new ArrayList<com.example.alarm_app.alarmserver.model.Date>(),
                            TurnOffType.NORMAL,
                            Snooze.MIN_5
                    ));

            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
            edit.apply();
            new MaterialAlertDialogBuilder(root.getContext())
                    .setTitle("Sync all alarms")
                    .setMessage("Create or log to your account will keep all of yours alarms in cloud. It's highly recommended to create or log in now.")
                    .setPositiveButton("Create account now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(new Intent(context, SingUpActivity.class));
                        }
                    })
                    .setNeutralButton("Log to your account", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(new Intent(context, LoginActivity.class));

                        }
                    })
                    .setNegativeButton("Do it later via settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create()
                    .show();
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        addNextAlarmTime();
    }

    private void addNextAlarmTime() {
        String strNextAlar;
        List<AlarmFor14Days> sortedActiveAlarms = AlarmService.getInstance().getSortedActiveAlarmsFor14Days();

        if (sortedActiveAlarms.size() == 0) {
            strNextAlar = noUpcomingAlarms;
        } else {
            strNextAlar = getTextWhenNextAlarmWillBe(sortedActiveAlarms.get(0));
        }
        textNextAlarmWillBe.setText(strNextAlar);
    }

    public String getTextWhenNextAlarmWillBe(AlarmFor14Days alarm) {
        if (alarm == null){
            return getString(R.string.notify_no_upcoming_alarms);
        }
        else if (alarm.getAlarmBe().before(new Date())){
            return getString(R.string.notify_no_upcoming_alarms);
        }

        StringBuilder strTimeNextAlarm = new StringBuilder();
        long dayInMillis = 86400000L;

//        End today
        Calendar endOfCurrentDay = Calendar.getInstance();
        endOfCurrentDay.setTime(new Date());
        endOfCurrentDay.set(HOUR_OF_DAY, 23);
        endOfCurrentDay.set(MINUTE, 59);
        endOfCurrentDay.set(SECOND, 59);
//        End tomorrow
        Calendar endOfTomorrowDay = Calendar.getInstance();
        endOfTomorrowDay.setTime(endOfCurrentDay.getTime());
        endOfTomorrowDay.setTimeInMillis(endOfCurrentDay.getTimeInMillis() + dayInMillis);
//        End Week
        Calendar endOfWeek = Calendar.getInstance();
        endOfWeek.setTime(endOfTomorrowDay.getTime());
        endOfWeek.setTimeInMillis(endOfTomorrowDay.getTimeInMillis() + dayInMillis * 5);


        if (alarm.getAlarmBe().before(endOfCurrentDay.getTime())) {
            strTimeNextAlarm.append(getContext().getString(R.string.today));
        }
        else if (alarm.getAlarmBe().before(endOfTomorrowDay.getTime())) {
            strTimeNextAlarm.append(getContext().getString(R.string.tomorrow));
        }
        else if (alarm.getAlarmBe().before(endOfWeek.getTime())) {
            endOfCurrentDay.setTime(alarm.getAlarmBe());
            strTimeNextAlarm.append(
                    getContext().getResources().getStringArray(
                            R.array.week_days)[endOfCurrentDay.get(DAY_OF_WEEK)]
            );
        }
        else {
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            strTimeNextAlarm.append(df.format(alarm.getAlarmBe()));
        }

        strTimeNextAlarm.append(", ");
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("HH:mm:ss");
        strTimeNextAlarm.append(df.format(alarm.getAlarmBe()));

        return strTimeNextAlarm.toString();
    }
}