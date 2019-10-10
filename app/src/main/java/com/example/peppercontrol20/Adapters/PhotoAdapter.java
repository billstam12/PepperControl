package com.example.peppercontrol20.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.peppercontrol20.ConversationControl.ListenConv;
import com.example.peppercontrol20.ConversationControl.PhotoConv;
import com.example.peppercontrol20.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class PhotoAdapter extends ArrayAdapter<PhotoConv> {
    private ArrayList<PhotoConv> photos;
    private Context context;
    public PhotoAdapter(Context context, int textViewResourceId, ArrayList<PhotoConv> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.photos = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PhotoConv image = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_thumb, parent, false);
        }
        // Lookup view for data population
        ImageView imageView = (ImageView) convertView.findViewById(R.id.photosImage);
        // Populate the data into the template view using the data object
        Picasso.get()
                .load(image.uri)
                .fit()
                .centerCrop()
                .into(imageView);
        // Return the completed view to render on screen
        Button upButton = convertView.findViewById(R.id.upButton);
        Button downButton = convertView.findViewById(R.id.downButton);
        Button removeButton = convertView.findViewById(R.id.trashButton);

        upButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("Photo 1", photos.get(0).uri.toString());
                if(position!=0){
                    //Swap Ids first
                    int tempId = photos.get(position).id;
                    photos.get(position).id =  photos.get(position - 1).id;
                    photos.get(position - 1).id =  tempId;
                    Collections.swap(photos , position, position-1);


                }
                notifyDataSetChanged();
            }
        });

        downButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("Photo 1", photos.get(0).uri.toString());
                if(position != photos.size() - 1 ){
                    //Swap Ids first
                    int tempId = photos.get(position).id;
                    photos.get(position).id =  photos.get(position + 1).id;
                    photos.get(position + 1).id =  tempId;
                    Collections.swap(photos , position, position + 1);
                }
                notifyDataSetChanged();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photos.remove(position);
                notifyDataSetChanged();

            }
        });



        return convertView;
    }
}
