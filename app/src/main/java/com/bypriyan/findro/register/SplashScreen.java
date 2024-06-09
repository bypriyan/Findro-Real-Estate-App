package com.bypriyan.findro.register;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bypriyan.findro.R;
import com.bypriyan.findro.activity.MainActivity;

public class SplashScreen extends AppCompatActivity {

    private static int splashScreen = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler( ).postDelayed(new Runnable() {
            @Override
            public void run() {
                    startActivity(new Intent(getApplicationContext(), SelectActivity.class));
                    finish();
            }
        },splashScreen);
    }

}