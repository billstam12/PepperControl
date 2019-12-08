package com.example.peppercontrol20.AppActivities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.peppercontrol20.ConversationControl.SQLiteDatabaseHandler;
import com.example.peppercontrol20.R;

public class PepperActivity extends AppCompatActivity {

    // Make sure to declare as ArrayList so it's Serializable
    static final String STATE_USER = "user";
    int event_id;
    SQLiteDatabaseHandler db;
    private Button btnSetup;
    private Button btnStart;
    private Button btnTest;
    private ListView listView;
    private TextView eventName;
    private Dialog dialog;
    private String mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pepper);

        Intent startIntent = getIntent();
        db = new SQLiteDatabaseHandler(this);
        //Get the eventID Intent and call three differente acitivities on each button press
        //Passing to them the same eventID Intent.
        event_id = startIntent.getIntExtra("Event", db.getEventsMaxID());
        String name = db.getEventNameByID(event_id);
        eventName = findViewById(R.id.event_name);
        eventName.setText(name);
        findViewsByIds();

        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PepperActivity.this, SetupRobot.class);
                intent.putExtra("Event", event_id);
                startActivity(intent);
            }
        });

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PepperActivity.this, TestBluetooth.class);
                startActivity(intent);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PepperActivity.this, StartPepper.class);
                intent.putExtra("OpenEventID", event_id);
                //intent.putExtra("chatController", chatController);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onRestoreInstanceState(savedInstanceState);
    }


    private void findViewsByIds() {
        btnStart = findViewById(R.id.btn_start);
        btnSetup = findViewById(R.id.btn_setup);
        btnTest = findViewById(R.id.btn_test);
    }


    @Override
    public void onStart() {

        super.onStart();


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}