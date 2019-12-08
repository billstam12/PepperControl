package com.example.peppercontrol20;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.peppercontrol20.AppActivities.AdminPanel;
import com.example.peppercontrol20.AppActivities.MainActivity;
import com.example.peppercontrol20.AppActivities.Settings;
import com.example.peppercontrol20.AppActivities.StartPepper;
import com.example.peppercontrol20.ConversationControl.Event;
import com.example.peppercontrol20.ConversationControl.SQLiteDatabaseHandler;

import java.util.ArrayList;

public class IntroChoise extends AppCompatActivity {
    Button pepperButton;
    Button serverButton;
    Button adminButton;
    Button settingsButton;
    Integer locked, eventID;
    ArrayList<Event> events;
    SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(this);
    private Dialog pwindo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Instansiate the view and the buttons
        setContentView(R.layout.activity_intro_choise);
        pepperButton = findViewById(R.id.choosePepper);
        serverButton = findViewById(R.id.chooseServer);
        settingsButton = findViewById(R.id.settingsButton);

        //System Preferences are used to Check if the App is not or not and in which Event
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        locked = sharedPreferences.getInt("Admin lock", 1);
        eventID = sharedPreferences.getInt("Check", -1);
        adminButton = findViewById(R.id.lockButton);

        //If is unlocked we set the corresponding icons
        if (locked == 0) {
            adminButton.setBackgroundResource(R.drawable.unlock);
            ViewGroup.LayoutParams params = adminButton.getLayoutParams();
            params.width = 43;
            adminButton.setLayoutParams(params);
        }
        //Button onclick listeners
        pepperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                String eventName = db.getEventNameByID(eventID);
                if (locked == 1) {
                    intent = new Intent(IntroChoise.this, StartPepper.class);
                    events = db.getAllEvents();
                    if (events.size() != 0) {
                        intent.putExtra("OpenEventID", eventID);    //start on the saved eventID
                        startActivity(intent);
                    }
                } else {
                    intent = new Intent(IntroChoise.this, AdminPanel.class);
                    startActivity(intent);

                }
            }
        });
        serverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (locked == 1) {
                    intent = new Intent(IntroChoise.this, StartPepper.class);
                    events = db.getAllEvents();
                    if (events.size() != 0) {
                        intent.putExtra("OpenEventID", eventID);    //start on the saved eventID
                        startActivity(intent);
                    }

                } else {
                    intent = new Intent(IntroChoise.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroChoise.this, Settings.class);
                startActivity(intent);
            }
        });
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locked == 1) {
                    pwindo = new Dialog(IntroChoise.this);
                    pwindo.setContentView(R.layout.login);
                    pwindo.show();

                    EditText email = pwindo.findViewById(R.id.editTextEmail);
                    EditText password = pwindo.findViewById(R.id.editTextPassword);
                    Button loginButton = pwindo.findViewById(R.id.cirLoginButton);

                    TextView wrongPassword = pwindo.findViewById(R.id.wrongPassword);
                    wrongPassword.setVisibility(View.GONE);

                    loginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String emailText = email.getText().toString();
                            String passwordText = password.getText().toString();

                            if (emailText.equals("admin") && passwordText.equals("password")) {

                                adminButton.setBackgroundResource(R.drawable.unlock);
                                ViewGroup.LayoutParams params = adminButton.getLayoutParams();
                                params.width = 43;
                                adminButton.setLayoutParams(params);
                                locked = 0;
                                editor.putInt("Admin lock", 0).apply();
                                pwindo.dismiss();

                            } else {
                                wrongPassword.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {
                    adminButton.setBackgroundResource(R.drawable.lock);
                    locked = 1;
                    editor.putInt("Admin lock", 1).apply();

                }
            }
        });
    }
}
