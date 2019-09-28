package com.example.peppercontrol20.MediaPlayers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.peppercontrol20.Adapters.RVAdapter;
import com.example.peppercontrol20.ConversationControl.SQLiteDatabaseHandler;
import com.example.peppercontrol20.ConversationControl.VideoConv;
import com.example.peppercontrol20.R;

import java.util.ArrayList;
import java.util.List;

public class VideoPlayers extends AppCompatActivity {

    private JsonArrayRequest ArrayRequest;
    private RequestQueue requestQueue;
    private ArrayList<VideoConv> lstVideos = new ArrayList<>();
    private RecyclerView myrv;

    Integer URL_JSON;
    SQLiteDatabaseHandler db; //Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Opened: ", "VideoPlayers");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_players);
        myrv = findViewById(R.id.rv);
        db = new SQLiteDatabaseHandler(this);
        Integer id = getIntent().getExtras().getInt("Video ID");

        lstVideos = db.getVideos(id);
        setRvadapter(lstVideos);


    }

    public void setRvadapter (ArrayList<VideoConv> lst) {

        RVAdapter myAdapter = new RVAdapter(this, lst) ;
        myrv.setLayoutManager(new LinearLayoutManager(this));
        myrv.setAdapter(myAdapter);

    }
}
