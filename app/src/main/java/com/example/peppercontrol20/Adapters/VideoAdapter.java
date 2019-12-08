package com.example.peppercontrol20.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.peppercontrol20.ConversationControl.VideoConv;
import com.example.peppercontrol20.R;

import java.util.ArrayList;

public class VideoAdapter extends ArrayAdapter<VideoConv> {
    private ArrayList<VideoConv> videos;
    private Context context;

    public VideoAdapter(Context context, int textViewResourceId, ArrayList<VideoConv> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.videos = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        VideoConv video = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.text_layout, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.textViewId);
        // Populate the data into the template view using the data object
        String endText = video.name;
        if (endText.length() > 20) {
            endText = endText.substring(0, 20) + "...";
        }
        name.setText(endText);
        // Return the completed view to render on screen


        return convertView;
    }
}
