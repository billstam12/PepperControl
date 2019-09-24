package com.example.peppercontrol20.ConversationControl;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.peppercontrol20.Adapters.EventAdapter;
import com.example.peppercontrol20.AppActivities.PepperActivity;
import com.example.peppercontrol20.IntroChoise;
import com.example.peppercontrol20.R;

import java.util.ArrayList;

public class AdminPanel extends AppCompatActivity {

    Button addEventButton;
    ArrayList<Event> events;
    EventAdapter eventAdptr;
    ListView eventView;
    SQLiteDatabaseHandler db;
    Dialog pwindo;
    Uri eventWallpaperUri, eventIconUri;
    ImageView eventWallpaper, eventIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        db = new SQLiteDatabaseHandler(this);
        events = (ArrayList<Event>) db.getAllEvents();
        eventView = findViewById(R.id.event_list);
        eventAdptr = new EventAdapter(this,
                android.R.layout.simple_list_item_1, events);

        eventView.setAdapter(eventAdptr);

        addEventButton =  findViewById(R.id.addEventButton);

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwindo = new Dialog(AdminPanel.this);
                pwindo.setContentView(R.layout.edit_event);
                pwindo.show();
                Button addWallpaper = pwindo.findViewById(R.id.addPhotoButton);
                Button addIcon = pwindo.findViewById(R.id.addIconButton);
                Button closeButton = pwindo.findViewById(R.id.x_button);
                EditText eventName = pwindo.findViewById(R.id.editTextEvent);
                TextView notify = pwindo.findViewById(R.id.notify);
                Button saveButton = pwindo.findViewById(R.id.save_popup);
                eventIcon = pwindo.findViewById(R.id.eventIcon);
                eventWallpaper = pwindo.findViewById(R.id.eventWallpaper);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nameString = eventName.getText().toString();
                        if (nameString == "") {
                            notify.setText("Please enter a name for the event!");
                        }
                        else{
                            Log.d("MAX EVENT", Integer.toString(db.getEventsMaxID()));
                            Event event = new Event(db.getEventsMaxID() + 1, nameString);
                            if(eventIconUri != null){
                                event.setIcon(eventIconUri);
                            }
                            if(eventWallpaperUri != null){
                                event.setPhoto(eventWallpaperUri);
                            }
                            db.addEvent(event);
                            events = (ArrayList<Event>) db.getAllEvents();
                            eventAdptr.notifyDataSetChanged();
                            eventView.setAdapter(eventAdptr);

                            pwindo.dismiss();
                        }
                    }
                });

                addWallpaper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
                    }
                });

                addIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 0);//one can be replaced with any action code
                    }
                });

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pwindo.dismiss();
                    }
                });






            }
        });

        eventView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* Now we will begin the event, and transform the app Icon and Photo to the event's */
                // TODO Update Icon  here
                // Intent to SetupRobot with the Event Details
                Intent intent = new Intent(AdminPanel.this, PepperActivity.class);
                intent.putExtra("Event", events.get(position).id);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        Integer conv_id = sharedPreferences.getInt("Conversation ID PHOTO", 1);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    eventIconUri = selectedImage;
                    eventIcon.setImageURI(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    eventWallpaperUri = selectedImage;
                    eventWallpaper.setImageURI(selectedImage);

                }
                break;

        }
    }
}
