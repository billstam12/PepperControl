package com.example.peppercontrol20.AppActivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.peppercontrol20.ConversationControl.PhotoConv;
import com.example.peppercontrol20.ConversationControl.SQLiteDatabaseHandler;
import com.example.peppercontrol20.R;

import java.util.ArrayList;


public class PhotoPresentation extends FragmentActivity {

    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    SQLiteDatabaseHandler db;
    Integer convo_id;
    private static ArrayList<PhotoConv> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        convo_id = i.getIntExtra("Photo ID", 0);
        db = new SQLiteDatabaseHandler(this);
        photos = db.getPhotos(convo_id);
        setContentView(R.layout.activity_photo_presentation);
        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);

    }

    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();
            Log.d("Position", Integer.toString(position));
            return SwipeFragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View swipeView = inflater.inflate(R.layout.image_slider_layout_item, container, false);
            ImageView imageView = (ImageView) swipeView.findViewById(R.id.imageView);
            Bundle bundle = getArguments();

            int position = bundle.getInt("position");
            Log.d("Position", Integer.toString(position));

            Uri imageURI = photos.get(position).getUri();
            Log.d("URI", imageURI.toString());
            imageView.setImageURI(imageURI);
            return swipeView;
        }

        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Log.d("Position", Integer.toString(position));

            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }
    }
}