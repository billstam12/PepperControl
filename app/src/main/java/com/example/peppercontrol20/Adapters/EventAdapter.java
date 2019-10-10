package com.example.peppercontrol20.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.peppercontrol20.AppActivities.EventPhotoObservable;
import com.example.peppercontrol20.AppActivities.PepperActivity;
import com.example.peppercontrol20.AppActivities.AdminPanel;
import com.example.peppercontrol20.ConversationControl.Event;
import com.example.peppercontrol20.ConversationControl.SQLiteDatabaseHandler;
import com.example.peppercontrol20.Models.photoObject;
import com.example.peppercontrol20.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class EventAdapter extends ArrayAdapter<Event> implements Observer {
    Context mContext;
    Uri eventIconUri, eventWallpaperUri;
    ImageView eventIcon;
    ImageView eventWallpaper;
    static ArrayList<Event> events;
    private RadioButton mSelectedRB;
    private int mSelectedPosition = -1;



    @Override
    public void update(Observable o, Object args) {
        if(o instanceof EventPhotoObservable){

            photoObject p = (photoObject) args;
            Uri selectedImage = (((photoObject) args).getUri());
            int type = p.getType();
            if(type == 0){
                eventWallpaperUri = selectedImage;
                try {
                    Picasso.get()
                            .load(eventWallpaperUri)
                            .placeholder(R.drawable.headline)
                            .fit()
                            .centerCrop()
                            .into(eventWallpaper);
                }
                catch (Exception e){

                }

            }
            else if(type == 1){
                eventIconUri = selectedImage;
                Log.d("Updated:", "event icon");
                eventIcon.setImageURI(eventIconUri);
            }

        }
    }

    private static class ViewHolder {
        private TextView name;
        private Button editButton;
        private Button deleteButton;
        private RadioButton radioButton;
    }

    public EventAdapter(Context context, int textViewResourceId, ArrayList<Event> items) {
        super(context, textViewResourceId, items);
        this.mContext = context;
        EventPhotoObservable.getInstance().addObserver(this);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Event event = getItem(position);
        ViewHolder holder;
        // Get all events

        // Check if an existing view is being reused, otherwise inflate the view
        SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(getContext());
        events = db.getAllEvents();
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_event, parent, false);

        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.textViewId);
        Button edit = convertView.findViewById(R.id.edit);
        Button delete = convertView.findViewById(R.id.delete);
        RadioButton radioButton = convertView.findViewById(R.id.event_checkbox);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(((Activity) mContext));
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        // Get checked id
        //int check = sharedPreferences.getInt("Check", -1);
        mSelectedPosition = sharedPreferences.getInt("Check", - 1) - 1;
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
                eventIcon = pwindo.findViewById(R.id.eventIcon);
                eventWallpaper = pwindo.findViewById(R.id.eventWallpaper);
                eventWallpaperUri = null;
                eventIconUri = null;
                eventName.setText(event.getName());
                // Get Permission
                if (ContextCompat.checkSelfPermission((AdminPanel) mContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale((AdminPanel) mContext,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions((AdminPanel) mContext,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    if(event.getPhoto() != null){
                        eventWallpaper.setImageURI(event.getPhoto());
                        Picasso.get()
                                .load(event.getPhoto())
                                .placeholder(R.drawable.headline)
                                .fit()
                                .centerCrop()
                                .into(eventWallpaper);
                    }


                }

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nameString = eventName.getText().toString();
                        if (nameString == "") {
                            notify.setText("Please enter a name for the event!");
                            notify.setVisibility(View.VISIBLE);
                        }
                        else{
                            Log.d("Editing Event with id", Integer.toString(event.getId()));

                            if(eventIconUri != null){
                                event.setIcon(eventIconUri);

                            }
                            if(eventWallpaperUri != null){
                                event.setPhoto(eventWallpaperUri);
                            }
                            //eventAdptr.notifyDataSetChanged();
                            event.setName(eventName.getText().toString());
                            db.updateEvent(event);
                            pwindo.dismiss();

                        }
                        notifyDataSetChanged();

                    }

                });

                addWallpaper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        ((Activity) mContext).startActivityForResult(pickPhoto , 2);//one can be replaced with any action code
                    }
                });

                addIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        ((Activity) mContext).startActivityForResult(pickPhoto , 3);//one can be replaced with any action code
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
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(true);
                builder.setTitle("Are you sure about that?");
                builder.setMessage("This will delete the event, click Confirm to continue or cancel to go back.");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(events.size() == 0){
                                    editor.putInt("Check", -1).apply();
                                }
                                db.deleteEvent(event);
                                events = db.getAllEvents();

                            }

                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                notifyDataSetChanged();

            }

        });

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position != mSelectedPosition && mSelectedRB != null){
                    mSelectedRB.setChecked(false);

                }

                editor.putInt("Check", event.getId()).apply();
                Map<String, ?> allEntries = sharedPreferences.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                }                 mSelectedPosition = position;
                mSelectedRB = (RadioButton) v;


            }
        });
        if(mSelectedPosition != position){
            radioButton.setChecked(false);
        }
        else{
            radioButton.setChecked(true);
            if(mSelectedRB != null && radioButton != mSelectedRB){
                mSelectedRB = radioButton;
            }
        }



        // Return the completed view to render on screen
        return convertView;
    }
}
