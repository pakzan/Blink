package com.example.dell.blink;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class Notification {

    private static Notification notificationManager = null;
    static final String STOP_BLINK_ACTION = "blink_action";
    static final int NOTIFICATION_ID = 0;

    NotificationCompat.Builder builder;
    NotificationManager manager;

    private Notification(Context context){
        Intent intent = new Intent(context, BlinkActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Intent stopBlinkIntent = new Intent(context, NotificationActionService.class).setAction(STOP_BLINK_ACTION);
        PendingIntent stopBlinkPendingIntent = PendingIntent.getService(context, 0, stopBlinkIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // create notification
         builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Blink is active")
                .setContentTitle("Blink")
                .setContentIntent(pendingIntent)
                .addAction(R.mipmap.ic_launcher, "Stop", stopBlinkPendingIntent)
                .setOngoing(true);
         manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "blink_id";
            NotificationChannel channel = new NotificationChannel(channelId, "Blink", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
    }

    public static Notification getInstance(){
        assert notificationManager != null;
        return notificationManager;
    }

    public static void initialise(Context context){
        if (notificationManager == null)
            notificationManager = new Notification(context);
    }

    void show(){
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    void hide(){
        manager.cancel(NOTIFICATION_ID);
    }


    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            if (action.equals(STOP_BLINK_ACTION)) {
                Blink.getInstance().stopBlink();
            }
        }
    }

}
