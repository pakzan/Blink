package com.example.dell.blink;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Dell on 23/07/2015.
 */
public class BlinkReceiver extends BroadcastReceiver {

    private static Intent i;
    private static Context ctx;
    private static Handler handler;

    @Override
    public void onReceive(Context ctx, Intent intent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Blink is activated")
                .setContentTitle("Blink");
        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

        //insert alarm
        i = new Intent(ctx, Blank.class);
        PendingIntent pi = PendingIntent.getActivity(ctx, 3333, i, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) ctx.getSystemService(ctx.ALARM_SERVICE);
        Calendar cal;
        cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);

        this.ctx = ctx;
        handler = new Handler();
    }

    public static void Delay(){
        i = new Intent(ctx, Blank.class);
        final PendingIntent pi = PendingIntent.getActivity(ctx, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        final AlarmManager am = (AlarmManager) ctx.getSystemService(ctx.ALARM_SERVICE);
        Calendar cal;
        cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);

    }
}
