package com.example.peppercontrol20.Adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.peppercontrol20.ConversationControl.VideoConv;
import com.example.peppercontrol20.MediaPlayers.SingleMedia;
import com.example.peppercontrol20.R;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {

    RequestOptions options ;
    private Context mContext ;
    private List<VideoConv> mData ;


    public RVAdapter(Context mContext, List<VideoConv> mData) {


        this.mContext = mContext;
        this.mData = mData;
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.loading)
                .error(R.drawable.loading);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        view = mInflater.inflate(R.layout.media_row ,parent,false);
        // click listener here
        final MyViewHolder viewHolder = new MyViewHolder(view) ;
        viewHolder.view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, SingleMedia.class);
                Log.d("Holder", mData.get(viewHolder.getAdapterPosition()).toString());
                // sending data process

                i.putExtra("Video Name",mData.get(viewHolder.getAdapterPosition()).getName());
                i.putExtra("Video Description",mData.get(viewHolder.getAdapterPosition()).getDescription());
                i.putExtra("Video URL",mData.get(viewHolder.getAdapterPosition()).getUrl());
                i.putExtra("Video Category",mData.get(viewHolder.getAdapterPosition()).getCategory());


                mContext.startActivity(i);

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.name.setText(mData.get(position).getName());
        holder.description.setText(mData.get(position).getDescription());
        Log.d("Description", mData.get(position).getDescription());
        Log.d("Category", mData.get(position).getCategory());
        String uri= (mData.get(position).getUrl());
        Log.d("Uri", uri);

        Glide.with(mContext)
                .asBitmap()
                .load(uri) // or URI/path
                .into(holder.thumbnail); //imageview to set thumbnail to

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, description, url, category;
        ImageView thumbnail;
        VideoView video;
        LinearLayout view_container;


        public MyViewHolder(View itemView) {
            super(itemView);
            view_container = itemView.findViewById(R.id.container);
            name = itemView.findViewById(R.id.rowname);
            description = itemView.findViewById(R.id.description);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            category  = itemView.findViewById(R.id.category);
            //video = itemView.findViewById(R.id.aa_video_url);

        }
    }


}