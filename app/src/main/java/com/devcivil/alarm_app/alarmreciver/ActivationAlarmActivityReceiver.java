package com.devcivil.alarm_app.alarmreciver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.devcivil.alarm_app.ui.ringing.AlarmRingingActivity;

public class ActivationAlarmActivityReceiver extends BroadcastReceiver {

    public static final String EXTRA_CURRENT_ALARM_RINGING = "alarm_current_ringing";

    @SuppressLint("Receiver for start alarm on time set")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent dialogIntent = new Intent(context, AlarmRingingActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(EXTRA_CURRENT_ALARM_RINGING, intent.getStringExtra(EXTRA_CURRENT_ALARM_RINGING));

        context.startActivity(dialogIntent);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            if (!Settings.canDrawOverlays(context)){
//                Toast.makeText(context, "No permission to write on the screen", Toast.LENGTH_LONG).show();
//                Log.e("Permit", "write on the screen: " + Settings.canDrawOverlays(context));
//            }
//
//            WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//            WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
//                    PixelFormat.TRANSLUCENT);
//            mLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
//            View view = LayoutInflater.from(context).inflate(R.layout.activity_alarm_ringing, null, false);
//            mWindowManager.addView(view, mLayoutParams);
//
//        } else {
//            context.startActivity(dialogIntent);
//        }
    }
}
