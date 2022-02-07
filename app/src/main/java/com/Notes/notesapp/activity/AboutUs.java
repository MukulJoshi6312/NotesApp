package com.Notes.notesapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUs extends AppCompatActivity {

    ImageView backimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        backimage = findViewById(R.id.backarow);

        backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutUs.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }
}