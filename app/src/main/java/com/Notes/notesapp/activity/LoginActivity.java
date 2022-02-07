package com.Notes.notesapp.activity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin,googleBtn;

    TextView registration, passwordReset,VerifyEmail;
    private TextInputLayout textEmail, password;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private GoogleSignInClient googleSignInClient;
    ProgressDialog progressDialog;
    private Animation bottomAnim,side;

    SharedPreferences sharedPreferences;
    private final static int RC_SIGN_IN = 90;



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseAuth = mAuth.getCurrentUser();
        if (firebaseAuth!= null){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.btnLogin);
        textEmail = findViewById(R.id.textEmail);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress);
        VerifyEmail = findViewById(R.id.verify_email);

        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.side_animation);
        side = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);


        VerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Gmail Opening", Toast.LENGTH_SHORT).show();
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                startActivity(intent);
            }
        });


        registration = findViewById(R.id.registration);
        passwordReset = findViewById(R.id.passwordReset);

        googleBtn = findViewById(R.id.googleBtn);


        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Please wait...");



        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);


        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
                }

        });







        sharedPreferences = getSharedPreferences("email", Context.MODE_PRIVATE);



      /*  checkConnection();*/

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
//                if (!isConnected(LoginActivity.this)){
//                    showCustomDialog();
//                }


                String email = textEmail.getEditText().getText().toString().trim();
                String userpassword = password.getEditText().getText().toString().trim();
                if (email.isEmpty()) {
                    textEmail.setError("Email is required");
                    textEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    textEmail.setError("please enter a valid email");
                    textEmail.requestFocus();
                    return;
                }

                if (userpassword.isEmpty()) {
                    password.setError("password is required");
                    password.requestFocus();
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);
                progressDialog.show();
                mAuth.signInWithEmailAndPassword(email, userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            assert firebaseUser != null;
                            if (firebaseUser.isEmailVerified()) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(LoginActivity.this, "Successfully Login...", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                progressDialog.dismiss();
                                finish();

                            } else {

                                firebaseUser.sendEmailVerification();
                                progressBar.setVisibility(View.GONE);
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Check you email to verify you account!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                VerifyEmail.setVisibility(View.VISIBLE);
                                VerifyEmail.setAnimation(side);



                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "ERROR!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }


        });


        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
//                startActivity(intent);

                Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
                Pair[] pairs = new Pair[1];

                pairs[0] = new Pair<View,String>(findViewById(R.id.registration),"transition_SignUp");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,pairs);
                    startActivity(intent,options.toBundle());
                }
                else {

                    startActivity(intent);

                }


            }
        });

        passwordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
//                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(getApplicationContext(),ForgotPasswordActivity.class);

                Pair[] pairs = new Pair[1];

                pairs[0] = new Pair<View,String>(findViewById(R.id.passwordReset), "transition_Forgot");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,pairs);
                    startActivity(intent,options.toBundle());
                }
                else {

                    startActivity(intent);

                }


            }
        });


    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        progressDialog.setTitle("Sign in with Google");
        progressDialog.setMessage("Please wait..");


        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Sing in with Google",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            progressDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Sorry authentication filed",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

}

