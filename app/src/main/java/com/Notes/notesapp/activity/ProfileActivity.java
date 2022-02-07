package com.Notes.notesapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {



     ImageView backImage;
     ProgressBar progressBar;
     Button editButton;
     BottomSheetDialog updateBottomSheetDialog;
     String fullName;
     String phoneNumber;
    FirebaseUser user;
    DatabaseReference reference;
    String userId;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


//        Button btnLogout =  findViewById(R.id.btnLogout);

        editButton = (Button) findViewById(R.id.editBtn);
        updateBottomSheetDialog = new BottomSheetDialog(ProfileActivity.this,R.style.updateBottomSheet);
        progressDialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);




        backImage = findViewById(R.id.imageBack);
        progressBar = findViewById(R.id.pProgress);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        editButton.setOnClickListener( v ->{
            ShowEditBottomSheet();

        });





//
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                Toast.makeText(ProfileActivity.this, "Logout...", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("user");
        assert user != null;
        userId = user.getUid();

        final  TextView userName =  findViewById(R.id.userName);

        final  TextView textEmail = findViewById(R.id.textEmail);

        final  TextView textPhone =  findViewById(R.id.textPhone);

        progressBar.setVisibility(View.VISIBLE);
        progressDialog.show();
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userProfile = snapshot.getValue(UserHelperClass.class);

                if (userProfile != null){
                    fullName = userProfile.fullname;
                    String email = userProfile.email;
                    phoneNumber = userProfile.phone;
                    userName.setText(fullName);
                    textEmail.setText(email);
                    textPhone.setText(phoneNumber);
                    progressBar.setVisibility(View.GONE);
                    progressDialog.dismiss();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something wrong happened!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();

            }
        });

    }

    private void ShowEditBottomSheet() {
        updateBottomSheetDialog.setContentView(R.layout.update_profile_bottom_sheet);
        TextInputLayout updateName = updateBottomSheetDialog.findViewById(R.id.userUpdateName);
        TextInputLayout updatePhone = updateBottomSheetDialog.findViewById(R.id.userUpdatePhone);
        assert updateName != null;
        updateName.getEditText().setText(String.valueOf(fullName));
        updatePhone.getEditText().setText(String.valueOf(phoneNumber));


        Button updateUserProfile = (Button)updateBottomSheetDialog.findViewById(R.id.userUpdateProfile);
        updateUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = updateName.getEditText().getText().toString().trim();
                String number = updatePhone.getEditText().getText().toString().trim();



                if (!name.isEmpty() && !number.isEmpty()) {

                    if(!name.equals(fullName) || !number.equals(phoneNumber)) {
                        reference.child(userId).child("fullname").setValue(name);
                        reference.child(userId).child("phone").setValue(number);
                        Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        updateBottomSheetDialog.dismiss();
                    }else{
                        Toast.makeText(getApplicationContext(),"Nothing for update",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Successfully updated user", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Unable to update user", Toast.LENGTH_SHORT).show();


        }
        });




        updateBottomSheetDialog.show();

    }

}