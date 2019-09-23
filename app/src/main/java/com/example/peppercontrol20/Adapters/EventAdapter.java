package com.example.peppercontrol20.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.peppercontrol20.AppActivities.PepperActivity;
import com.example.peppercontrol20.ConversationControl.AdminPanel;
import com.example.peppercontrol20.ConversationControl.Event;
import com.example.peppercontrol20.ConversationControl.ListenConv;
import com.example.peppercontrol20.ConversationControl.SQLiteDatabaseHandler;
import com.example.peppercontrol20.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<Event> {
    Context mContext;
    private static class ViewHolder {
        private TextView name;
    }

    public EventAdapter(Context context, int textViewResourceId, ArrayList<Event> items) {
        super(context, textViewResourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Event event = getItem(position);
        mContext = parent.getContext();

        // Check if an existing view is being reused, otherwise inflate the view
        SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(getContext());

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_event, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.textViewId);
        Button edit = convertView.findViewById(R.id.edit);
        //Button delete = convertView.findViewById(R.id.delete);
        //CheckBox checkBox = convertView.findViewById(R.id.event_checkbox);

        // Populate the data into the template view using the data object
        String endText = event.getName();
        if(endText.length() > 20){
            endText = endText.substring(0,20) + "...";
        }
        name.setText(endText);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PepperActivity.class);
                intent.putExtra("Event", event.getId());
                ((Activity) mContext).startActivity(intent);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog pwindo = new Dialog(getContext());
                pwindo.setContentView(R.layout.edit_event);
                pwindo.show();
                Button addWallpaper = pwindo.findViewById(R.id.addPhotoButton);
                Button addIcon = pwindo.findViewById(R.id.addIconButton);
                Button closeButton = pwindo.findViewById(R.id.x_button);
                EditText eventName = pwindo.findViewById(R.id.editTextEvent);
                TextView notify = pwindo.findViewById(R.id.notify);
                Button saveButton = pwindo.findViewById(R.id.save_popup);
                ImageView eventIcon = pwindo.findViewById(R.id.eventIcon);
                ImageView eventWallpaper = pwindo.findViewById(R.id.eventWallpaper);
                Uri eventWallpaperUri = null, eventIconUri = null;
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
                            db.updateEvent(event);
                            ArrayList<Event> events = (ArrayList<Event>) db.getAllEvents();
                            //eventAdptr.notifyDataSetChanged();

                            pwindo.dismiss();
                        }
                    }
                });

                addWallpaper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        ((Activity) mContext).startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
                    }
                });

                addIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        ((Activity) mContext).startActivityForResult(pickPhoto , 0);//one can be replaced with any action code
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

        // Return the completed view to render on screen
        return convertView;
    }
}
