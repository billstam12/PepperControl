package com.example.peppercontrol20.MediaPlayers;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.request.RequestOptions;
import com.example.peppercontrol20.R;

public class SingleMedia extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_media);

        // hide the default actionbar
        //getSupportActionBar().hide();
        MediaController mediaController = new MediaController(this);

        // Recieve data

        String name  = getIntent().getExtras().getString("Video Name");
        String description = getIntent().getExtras().getString("Video Description");
        String category = getIntent().getExtras().getString("Video Category");
        String thumbnail = getIntent().getExtras().getString("Video Thumbnail") ;
        String url = getIntent().getExtras().getString("Video URL");




        RequestOptions requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.loading).error(R.drawable.loading);

        // Set video

        VideoView videoView = (VideoView)findViewById(R.id.aa_video_url);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(url));

        videoView.start();

    }
}
