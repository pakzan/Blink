package com.example.dell.blink;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

public class Blink {

    private static Blink blink = null;
    Handler periodHandler, durationHandler;
    View view;
    Ringtone ringtone;
    boolean enableSound;
    boolean isBlocking = false;

    private Blink(Context context, View view){
        this.view = view;
        periodHandler = new Handler();
        durationHandler = new Handler();

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(context, notification);
    }

    public static Blink getInstance(){
        assert blink != null;
        return blink;
    }

    public static void initialise(Context context, View view){
        if (blink == null)
            blink = new Blink(context, view);
    }

    void setSound(boolean enableSound){
        this.enableSound = enableSound;
    }

    void blockView(){
        if (!isBlocking){
            if (enableSound)
                ringtone.play();
            view.blockView();
            isBlocking = true;
        }
    }

    void showView(){
        if (isBlocking) {
            if (enableSound)
                ringtone.stop();
            view.showView();
            isBlocking = false;
        }
    }

    void startBlink(final int duration, final int period){
        blockView();
        periodHandler.postDelayed(new Runnable(){
            public void run(){
                showView();
                durationHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        blockView();
                    }
                }, duration);

                periodHandler.postDelayed(this, period);
            }
        }, period);
        Notification.getInstance().show();
    }

    void stopBlink(){
        durationHandler.removeMessages(0);
        periodHandler.removeMessages(0);
        showView();
        Notification.getInstance().hide();
    }

    interface View {
        void showView();
        void blockView();
    }
}
