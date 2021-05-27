package com.example.dell.blink;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;

public class BlinkActivity extends Activity implements Blink.View{

    int duration = 1000; // millisecond
    int period = 2000; // millisecond
    private static final int OVERLAY_PERMISSION = 227;
    View blankView;
    WindowManager windowManager;
    WindowManager.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blink);

        // check/request draw overlay permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(getApplicationContext(), "Please allow display over other app for " + getPackageName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION);
            }
        }

        setupView();
        setupOverlay();
        Blink.initialise(getApplicationContext(), this);
        Notification.initialise(getApplicationContext());
    }

    void setupView(){
        final EditText et_duration = findViewById(R.id.et_duration);
        final EditText et_period = findViewById(R.id.et_period);
        final ToggleButton btn_toggle = findViewById(R.id.btn_toggle);
        final Switch sw_sound = findViewById(R.id.sw_sound);

        et_duration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String duration_s = s.toString();
                if (duration_s.equals("")){
                    et_duration.setError("empty value");
                    btn_toggle.setEnabled(false);
                }
                else{
                    duration = Integer.parseInt(duration_s)*1000;
                    if (duration == 0){
                        et_duration.setError("duration has to be > 0");
                        btn_toggle.setEnabled(false);
                    } else if (duration >= period){
                        et_duration.setError("duration has to be < period");
                        btn_toggle.setEnabled(false);
                    } else{
                        et_period.setError(null);
                        btn_toggle.setEnabled(true);
                    }
                }
            }
        });

        et_period.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String period_s = s.toString();
                if (period_s.equals("")){
                    et_period.setError("empty value");
                    btn_toggle.setEnabled(false);
                }
                else{
                    period = Integer.parseInt(period_s)*1000;
                    if (period == 0){
                        et_period.setError("period has to be > 0");
                        btn_toggle.setEnabled(false);
                    } else if (duration >= period){
                        et_period.setError("period has to be > duration");
                        btn_toggle.setEnabled(false);
                    } else{
                        et_duration.setError(null);
                        btn_toggle.setEnabled(true);
                    }
                }
            }
        });

        btn_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean startBlink) {
                Blink blink = Blink.getInstance();
                if (startBlink){
                    blink.setSound(sw_sound.isChecked());
                    blink.startBlink(duration, period);
                } else{
                    blink.stopBlink();
                }
            }
        });
    }

    void setupOverlay(){
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        blankView = inflater.inflate(R.layout.blank, null);
        blankView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION) {
            if (!Settings.canDrawOverlays(this)) {
                finish();
            }
        }
    }

    @Override
    public void showView() {
        windowManager.removeView(blankView);
    }

    @Override
    public void blockView() {
        windowManager.addView(blankView, params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Blink.getInstance().stopBlink();
    }
}
