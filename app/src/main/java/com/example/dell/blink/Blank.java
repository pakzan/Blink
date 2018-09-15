package com.example.dell.blink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Dell on 23/07/2015.
 */
public class Blank extends Activity {
    static int n = 0;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        Finish();
    }
    public void Finish(){
        setContentView(R.layout.blank);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BlinkReceiver.Delay();
                onDestroy();
            }
        }, 1000);
        /*if (n == 0) {
            setContentView(R.layout.blank);
            n++;
        }
        else {
            onDestroy();
            n = 0;
        }*/
    }

    @Override
    public void onDestroy()
    {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

}
