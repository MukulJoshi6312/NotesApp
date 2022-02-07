package com.Notes.notesapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    TextView tv,powered_by_line;
    ImageView iv;
    Animation sideAnim,bottomAnim;
    private static int SPLASH_TIME = 3000;
    private FirebaseAuth firebaseAuth;

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        tv =  findViewById(R.id.tv);
        iv =  findViewById(R.id.iv);
        powered_by_line = findViewById(R.id.powered_by_line);

        // Animation
        sideAnim = AnimationUtils.loadAnimation(this,R.anim.side_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);
        iv.setAnimation(sideAnim);
        tv.setAnimation(bottomAnim);
        powered_by_line.setAnimation(sideAnim);

        // firebase variable
       firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser == null) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    finish();
                }else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    finish();
                }

            }
        },SPLASH_TIME);

    }
}