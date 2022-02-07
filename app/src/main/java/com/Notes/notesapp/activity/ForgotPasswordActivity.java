package com.Notes.notesapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout textEmail;
     Button btnForgotPassword;
    private ProgressBar progressBar;
     TextView checkEmail,emailNotSend,goBack;

    FirebaseAuth mAuth;
    private Animation bottomAnim,side;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        textEmail = findViewById(R.id.textEmail);
        btnForgotPassword = findViewById(R.id.btnLForgotPassword);
        progressBar = findViewById(R.id.progress);

        mAuth = FirebaseAuth.getInstance();
        checkEmail = findViewById(R.id.checkEmail);
        emailNotSend = findViewById(R.id.checkEmailFalse);

        goBack = findViewById(R.id.go_back);

        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.side_animation);
        side = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        checkEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ForgotPasswordActivity.this, "Gmail Opening", Toast.LENGTH_SHORT).show();
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                startActivity(intent);
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });



    }
   private void resetPassword(){
        String email = Objects.requireNonNull(textEmail.getEditText()).getText().toString();

        if (email.isEmpty()){
            textEmail.setError("Email is required");
            textEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            textEmail.setError("Please provide valid email");
            textEmail.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this,"Check your email to reset your password",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    checkEmail.setVisibility(View.VISIBLE);
                    checkEmail.setAnimation(side);
                    emailNotSend.setVisibility(View.GONE);
                }else {
                    Toast.makeText(ForgotPasswordActivity.this,"Try again! Something wrong happened",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    checkEmail.setVisibility(View.GONE);
                    emailNotSend.setVisibility(View.VISIBLE);
                    emailNotSend.setAnimation(bottomAnim);

                }
            }
        });

    }

}