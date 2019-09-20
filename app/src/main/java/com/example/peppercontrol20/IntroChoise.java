package com.example.peppercontrol20;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.peppercontrol20.AppActivities.MainActivity;
import com.example.peppercontrol20.AppActivities.PepperActivity;

public class IntroChoise extends AppCompatActivity {
    Button pepperButton;
    Button serverButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_choise);
        pepperButton = findViewById(R.id.choosePepper);
        serverButton = findViewById(R.id.chooseServer);

        pepperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroChoise.this, PepperActivity.class);
                startActivity(intent);
            }
        });
        serverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroChoise.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
