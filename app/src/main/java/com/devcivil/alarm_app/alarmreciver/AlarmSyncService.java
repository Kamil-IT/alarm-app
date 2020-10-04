package com.devcivil.alarm_app.alarmreciver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.devcivil.alarm_app.R;
import com.devcivil.alarm_app.alarmserver.updator.AlarmUpdateDataReceiver;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

public class AlarmSyncService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //        Create updater for data from server
        if (PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.auto_sync_key), false)){
            long interval = AlarmManager.INTERVAL_HALF_HOUR / 30
                    * PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .getInt(getString(R.string.sync_interval_key), 30);

            alarmMgr.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + interval,
                    interval,
                    PendingIntent.getBroadcast(this, 0, new Intent(getBaseContext(), AlarmUpdateDataReceiver.class), 0)
            );
        }
        else {
            stopSelf();
        }

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startService(Context context){
        Intent syncService = new Intent(context, AlarmSyncService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(syncService);
//        } else{
            context.startService(syncService);
//        }
    }

    public static void syncTimeUpdated(Context context){
        startService(context);
    }
}
