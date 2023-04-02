package com.nilscreation.healthfacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nilscreation.healthfacts.R;


public class splash extends AppCompatActivity {

    ImageView applogo;
    TextView appname, subtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        applogo = findViewById(R.id.applogo);
        appname = findViewById(R.id.appname);
        subtext = findViewById(R.id.subtext);

        applogo.startAnimation(AnimationUtils.loadAnimation(splash.this, R.anim.fade_in));
        appname.startAnimation(AnimationUtils.loadAnimation(splash.this, R.anim.fade_in));
        subtext.startAnimation(AnimationUtils.loadAnimation(splash.this, R.anim.fade_in));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(splash.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2500);
    }
}