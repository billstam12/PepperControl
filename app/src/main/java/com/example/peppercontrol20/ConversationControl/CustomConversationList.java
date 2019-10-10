package com.example.peppercontrol20.ConversationControl;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.example.peppercontrol20.Adapters.ListenAdapter;
import com.example.peppercontrol20.Adapters.PhotoAdapter;
import com.example.peppercontrol20.Adapters.SayAdapter;
import com.example.peppercontrol20.Adapters.VideoAdapter;
import com.example.peppercontrol20.AppActivities.PhotoObservable;
import com.example.peppercontrol20.AppActivities.editPhoto;
import com.example.peppercontrol20.R;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class CustomConversationList extends BaseAdapter implements Observer {
    private Activity context;
    public ArrayList<Conversation> conversations;
    public int event_id;
    private Dialog pwindo, pwindo2, pwindo_edit_text, pwindo_edit_video;

    SQLiteDatabaseHandler db;

    ArrayList<ListenConv> tmpListens;

    ArrayList<SayConv> tmpSays;

    Button btnAddListen;
    Button btnAddSay;
    Button xButton;

    MyListView listenView;
    MyListView sayView;
    EditText listenText;
    EditText sayText;
    Spinner spinner;

    Button addVideo;
    Button saveVideo;
    Button addPhoto;
    CheckBox btnMakeProactive;
    Button videoxButton;

    EditText videoURL;
    EditText videoDESC;
    EditText videoCAT;
    EditText videoNAME;
    LinearLayout videoLayout;
    LinearLayout photoLayout;

    ArrayList<VideoConv> videos;
    ArrayList<PhotoConv> photos;

    VideoAdapter adptVideo;
    ArrayAdapter<String> adapter;

    PhotoAdapter adptPhoto;
    ListView videoView;
    ListView photoView;
    EditText proactiveEngagement;
    public int listenIDPad = 1;
    public int sayIDPad = 1;
    public int photoIDPad = 1;
    public int videoIDPad = 1;

    public int isProactive = 0;


    public CustomConversationList(Activity context, ArrayList<Conversation> conversations, SQLiteDatabaseHandler db, int event_id) {
        this.context = context;
        this.conversations = conversations;
        this.event_id = event_id;
        this.db = db;
        PhotoObservable.getInstance().addObserver(this);

    }

    @Override
    public void update(Observable o, Object args) {
        if(o instanceof PhotoObservable){
            editPhoto c = (editPhoto) args;
            Log.d("Image", c.getUri());

            Uri selectedImage = Uri.parse(((editPhoto) args).getUri());
            PhotoConv photo = new PhotoConv(db.getPhotosMaxID() + photoIDPad, ((editPhoto) args).getId(), selectedImage);
            photoIDPad += 1;
            if(photos == null){
                photos = db.getPhotos(((editPhoto) args).getId());
                adptPhoto = new PhotoAdapter(context, android.R.layout.simple_list_item_1, photos);

            }
            for(int i = 0; i < photos.size(); i++){
                Log.d("Photo of conv", photos.get(i).uri.toString());
            }
            photos.add(photo);
            adptPhoto.notifyDataSetChanged();

        }
    }



    public static class ViewHolder
    {
        TextView textViewId;
        TextView textViewListen;
        TextView textViewSay;
        Button editButton;
        Button deleteButton;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            row = inflater.inflate(R.layout.row_item, null, true);

            vh.textViewId = (TextView) row.findViewById(R.id.textViewId);
            vh.textViewListen = (TextView) row.findViewById(R.id.textViewListen);
            vh.textViewSay = (TextView) row.findViewById(R.id.textViewSay);
            vh.editButton = (Button) row.findViewById(R.id.edit);
            vh.deleteButton = (Button) row.findViewById(R.id.delete);

            // store the holder with the view.
            row.setTag(vh);
        } else {

            vh = (ViewHolder) convertView.getTag();

        }
        String firstListen;
        String firstSay = conversations.get(position).getConversationSay().get(0).getSay();
        if(conversations.get(position).getConversationProactive() == 1){
            firstListen = conversations.get(position).getConversationProactiveEngagement();
            vh.textViewId.setText("Proactive" + conversations.get(position).getId());

        }
        else {
            firstListen = conversations.get(position).getConversationListen().get(0).getListen();
            vh.textViewId.setText("" + conversations.get(position).getId());

        }
        if(firstSay.length() > 10){
            firstSay = firstSay.substring(0,10) + "...";
        }
        if(firstListen.length() > 10){
            firstListen = firstListen.substring(0,10) + "...";
        }
        vh.textViewListen.setText(firstListen);
        vh.textViewSay.setText(firstSay);
        final int positionPopup = position;
        vh.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editPopup(positionPopup);

            }
        });
        vh.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Last Index", "" + positionPopup);
                //     Integer index = (Integer) view.getTag();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Are you sure about that?");
                builder.setMessage("This will delete the conversation, click Confirm to continue or cancel to go back.");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.deleteConversation(conversations.get(positionPopup));

                                //      countries.remove(index.intValue());
                                conversations = (ArrayList) db.getAllConversations(event_id);
                                Log.d("Conversations size", "" + conversations.size());
                                notifyDataSetChanged();
                            }

                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        return  row;
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return position;
    }

    public int getCount() {
        return conversations.size();
    }

    public void editPopup(final int positionPopup)
    {
       /*
        LayoutInflater inflater = context.getLayoutInflater();

        View layout = inflater.inflate(R.layout.edit_popup,
                (ViewGroup) context.findViewById(R.id.popup_element));
        */

        pwindo = new Dialog(context);
        pwindo.setContentView(R.layout.edit_popup);
        pwindo.show();

        LinearLayout listenLayout = pwindo.findViewById(R.id.editLayout);
        LinearLayout proactiveLayout = pwindo.findViewById(R.id.proactiveEngagementLayout);
        proactiveEngagement = pwindo.findViewById(R.id.proactiveEngagement);
        listenText = (EditText) pwindo.findViewById(R.id.editTextListen);
        sayText = (EditText) pwindo.findViewById(R.id.editTextSay);
        listenView = (MyListView) pwindo.findViewById(R.id.listViewListen);
        sayView = (MyListView) pwindo.findViewById(R.id.listViewSay);
        btnAddListen = pwindo.findViewById(R.id.listenButton);
        btnAddSay = pwindo.findViewById(R.id.sayButton);
        xButton = pwindo.findViewById(R.id.x_button);
        addVideo = pwindo.findViewById(R.id.addVideoButton);
        addPhoto = pwindo.findViewById(R.id.addPhotoButton);
        spinner = pwindo.findViewById(R.id.spinnerActivity);
        btnMakeProactive = pwindo.findViewById(R.id.proactiveButton);

        ArrayList<Integer> listenIdsForDel = new ArrayList<>();
        ArrayList<Integer> sayIdsForDel = new ArrayList<>();
        ArrayList<Integer> videoIdsForDel = new ArrayList<>();
        final ArrayList<ListenConv> listens = (db.getListens(conversations.get(positionPopup).getId()));
        final ArrayList<SayConv> says = (db.getSays(conversations.get(positionPopup).getId())) ;
        videos = (db.getVideos(conversations.get(positionPopup).getId()));
        photos = (db.getPhotos(conversations.get(positionPopup).getId()));
        Log.d("Conversation:" + Integer.toString(positionPopup), Integer.toString(videos.size()));
        final ArrayList<String> listensText = new ArrayList<>();
        final ArrayList<String> saysText = new ArrayList<>();
        tmpListens = new ArrayList<>();
        tmpSays = new ArrayList<>();

        Log.d("Listen size", Integer.toString(listens.size()));
        Log.d("Say size", Integer.toString(says.size()));
        final String spinnerText = (db.getActivity(conversations.get(positionPopup).getId()));
        //Get texts from listens and says
        for(int i = 0; i < listens.size(); i++){
            Log.d("Listen", listens.get(i).getListen());
            listensText.add(listens.get(i).getListen());
        }
        for(int i = 0; i < says.size(); i++){
            Log.d("Say", says.get(i).getSay());

            saysText.add(says.get(i).getSay());
        }
        for(int i = 0; i < videos.size(); i++){
            Log.d("Video", videos.get(i).getName());

        }
        for(int i = 0; i < photos.size(); i++){
            Log.d("Photo", photos.get(i).getUri().toString());
        }


        //New sol
        final ListenAdapter adptListen = new ListenAdapter(context, android.R.layout.simple_list_item_1, listens);
        final SayAdapter adptSay = new SayAdapter(context, android.R.layout.simple_list_item_1, says);
        adptPhoto = new PhotoAdapter(context, android.R.layout.simple_list_item_1, photos);
        adptVideo = new VideoAdapter(context, android.R.layout.simple_list_item_1, videos);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, context.getResources().getStringArray(R.array.activities));

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listenView.setAdapter(adptListen);

        // Check if proactive
        isProactive = db.getProactive(conversations.get(positionPopup).getId());
        if(isProactive == 1){
            proactiveLayout.setVisibility(View.VISIBLE);
            btnMakeProactive.setChecked(true);
            isProactive = 1;
        }
        else {
            proactiveLayout.setVisibility(View.GONE);
            btnMakeProactive.setChecked(false);
            listenLayout.setVisibility(View.VISIBLE);
        }
        // Make proactive
        btnMakeProactive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    proactiveLayout.setVisibility(View.VISIBLE);
                    isProactive = 1;
                }
                else{
                    proactiveLayout.setVisibility(View.GONE);
                    listenLayout.setVisibility(View.VISIBLE);
                    isProactive = 0;

                }
            }
        });
        proactiveEngagement.setText(conversations.get(positionPopup).getConversationProactiveEngagement());
        /* Add longpress to Edit */
        listenView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // Open popup
                Log.v("long clicked","pos: " + pos);

                pwindo_edit_text = new Dialog(pwindo.getContext());
                pwindo_edit_text.setContentView(R.layout.edit_text_popup);
                EditText listenEditText = pwindo_edit_text.findViewById(R.id.text);
                ListenConv ls = (ListenConv)arg0.getAdapter().getItem(pos);
                listenEditText.setText(ls.listen);
                Integer listenEditID = ls.id;
                pwindo_edit_text.show();

                // Set clicks
                Button deleteEditListen = pwindo_edit_text.findViewById(R.id.delete_text_button);
                Button saveEditListen = pwindo_edit_text.findViewById(R.id.save_text_button);
                Button closeEditButton = pwindo_edit_text.findViewById(R.id.x_button);

                deleteEditListen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //db.deleteListen(listens);
                        // TODO: add yes or no
                        if(listenEditID <= db.getListenMaxID()){
                            listenIdsForDel.add(listenEditID);
                        }
                        for(int i = 0; i < listens.size(); i++){
                            if(listens.get(i).id == ls.id){
                                listens.remove(i);
                            }
                        }
                        adptListen.setNotifyOnChange(true);
                        listenView.setAdapter(adptListen);
                        pwindo_edit_text.dismiss();
                    }
                });


                saveEditListen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        listens.set(pos, new ListenConv(listens.get(pos).id, listens.get(pos).conv_id,  listenEditText.getText().toString()));
                        adptListen.setNotifyOnChange(true);
                        listenView.setAdapter(adptListen);
                        pwindo_edit_text.dismiss();
                    }
                });
                closeEditButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pwindo_edit_text.dismiss();
                    }
                });
                return true;
            }
        });
        spinner.setAdapter(adapter);
        videoLayout = pwindo.findViewById(R.id.videoLayout);
        photoLayout = pwindo.findViewById(R.id.photoLayout);

        videoView = pwindo.findViewById(R.id.listViewVideo);
        photoView = pwindo.findViewById(R.id.listViewPhoto);

        videoView.setAdapter(adptVideo);
        photoView.setAdapter(adptPhoto);
        spinner.setSelection(adapter.getPosition(spinnerText));
        spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                String selCat = spinner.getItemAtPosition(arg2).toString();


                if(selCat.equals("Video")){
                    videoLayout.setVisibility(View.VISIBLE);
                }
                else {
                    videoLayout.setVisibility(View.GONE);
                }
                if(selCat.equals("Photo")){
                    photoLayout.setVisibility(View.VISIBLE);
                }
                else{
                    photoLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        sayView.setAdapter(adptSay);
        sayView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                Log.v("long clicked","pos: " + pos);
                pwindo_edit_text = new Dialog(pwindo.getContext());
                pwindo_edit_text.setContentView(R.layout.edit_text_popup);
                EditText sayEditText = pwindo_edit_text.findViewById(R.id.text);
                SayConv s = (SayConv) arg0.getAdapter().getItem(pos);
                sayEditText.setText(s.say);
                Integer sayEditID = s.id;
                pwindo_edit_text.show();

                // Set clicks
                Button deleteEditListen = pwindo_edit_text.findViewById(R.id.delete_text_button);
                Button saveEditListen = pwindo_edit_text.findViewById(R.id.save_text_button);
                Button closeEditButton = pwindo_edit_text.findViewById(R.id.x_button);
                deleteEditListen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //db.deleteListen(listens);
                        // TODO: add yes or no
                        if(sayEditID <= db.getSaysMaxID()){
                            sayIdsForDel.add(sayEditID);
                        }
                        for(int i = 0; i < says.size(); i++){
                            if(says.get(i).id == s.id){
                                says.remove(i);
                            }
                        }
                        adptSay.setNotifyOnChange(true);
                        sayView.setAdapter(adptSay);
                        pwindo_edit_text.dismiss();
                    }
                });


                saveEditListen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        says.set(pos, new SayConv(says.get(pos).id, says.get(pos).conv_id,  sayEditText.getText().toString()));
                        adptSay.setNotifyOnChange(true);
                        sayView.setAdapter(adptSay);
                        pwindo_edit_text.dismiss();
                    }
                });
                closeEditButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pwindo_edit_text.dismiss();
                    }
                });
                return true;
            }
        });

        btnAddListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listen = listenText.getText().toString();
                if(listen!= ""){
                    String log = "ID: " + Integer.toString(db.getListenMaxID()+ listenIDPad) + " Conversation: " + Integer.toString(conversations.get(positionPopup).getId()) + " Listen: " + listen;
                    Log.d("Adding Listen:", log);

                    ListenConv tmpListen = new ListenConv(db.getListenMaxID() + listenIDPad, conversations.get(positionPopup).getId(), listen);
                    listenIDPad +=1;
                    listens.add(tmpListen);
                    listensText.add(listen);
                    adptListen.setNotifyOnChange(true);
                    listenView.setAdapter(adptListen);
                }
                listenText.setText("");
                btnAddListen.setEnabled(true);
            }
        });

        btnAddSay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String say = sayText.getText().toString();
                if(say!= ""){
                    String log = "ID: " + Integer.toString(db.getSaysMaxID()+ sayIDPad) + " Conversation: " + Integer.toString(conversations.get(positionPopup).getId()) + " Say: " + say;
                    Log.d("Adding Say:", log);

                    SayConv tmpSay = new SayConv(db.getSaysMaxID() + sayIDPad, conversations.get(positionPopup).getId(), say);
                    sayIDPad += 1;
                    says.add(tmpSay);
                    saysText.add(say);

                    adptSay.setNotifyOnChange(true);
                    sayView.setAdapter(adptSay);
                }
                sayText.setText("");
                btnAddSay.setEnabled(true);
            }
        });

        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videos = addVideoPopUp(videos, positionPopup);
            }
        });
        videoView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                Log.v("long clicked","pos: " + pos);
                pwindo_edit_video = new Dialog(pwindo.getContext());
                pwindo_edit_video.setContentView(R.layout.edit_video_new);
                EditText videoName = pwindo_edit_video.findViewById(R.id.video_name);
                EditText videoURL = pwindo_edit_video.findViewById(R.id.video_url);
                EditText videoCat = pwindo_edit_video.findViewById(R.id.video_cat);
                EditText videoDesc = pwindo_edit_video.findViewById(R.id.video_desc);

                VideoConv video = (VideoConv)arg0.getAdapter().getItem(pos);
                videoName.setText(video.name);
                videoURL.setText(video.url);
                videoCat.setText(video.category);
                videoDesc.setText(video.description);

                pwindo_edit_video.show();
                Integer videoEditID = video.id;

                // Set clicks
                Button deleteEditVideo = pwindo_edit_video.findViewById(R.id.delete_video_popup);
                Button saveEditVideo = pwindo_edit_video.findViewById(R.id.save_video_popup);
                Button closeEditButton = pwindo_edit_video.findViewById(R.id.x_button_video);
                deleteEditVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //db.deleteListen(listens);
                        // TODO: add yes or no
                        if(videoEditID <= db.getVideosMaxID()){
                            videoIdsForDel.add(videoEditID);
                        }
                        for(int i = 0; i < videos.size(); i++){
                            if(videos.get(i).id == video.id){
                                videos.remove(i);
                            }
                        }
                        adptVideo.setNotifyOnChange(true);
                        videoView.setAdapter(adptVideo);
                        pwindo_edit_video.dismiss();
                    }
                });


                saveEditVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videos.set(pos, new VideoConv(videos.get(pos).id, videos.get(pos).conv_id, videoURL.getText().toString().replace("dl=0", "raw=1"), videoCat.getText().toString(), videoDesc.getText().toString(), videoName.getText().toString()));
                        adptVideo.setNotifyOnChange(true);
                        videoView.setAdapter(adptVideo);
                        pwindo_edit_video.dismiss();
                    }
                });
                closeEditButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pwindo_edit_video.dismiss();
                    }
                });
                return true;
            }
        });
        //addPhoto.setVisibility(View.INVISIBLE);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                final SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putInt("Conversation ID PHOTO", conversations.get(positionPopup).getId()).apply();

                context.startActivityForResult(pickPhoto , 0);//one can be replaced with any action code
            }
        });

        Button save = (Button) pwindo.findViewById(R.id.save_popup);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                db.deleteConversationListens(conversations.get(positionPopup).getId());
                db.deleteConversationSays(conversations.get(positionPopup).getId());

                Conversation conversation = conversations.get(positionPopup);

                conversation.setConversationListen(listens);
                conversation.setConversationSay(says);
                conversation.setConversationActivity(spinner.getSelectedItem().toString());
                conversation.setConversationEventID(event_id);
                conversation.setConversationProactive(isProactive);

                if(spinner.getSelectedItem().toString().equals("Video")){
                    db.deleteConversationVideos(conversations.get(positionPopup).getId());
                    conversation.setConversationVideo(videos);
                }
                else{
                    db.deleteConversationVideos(conversations.get(positionPopup).getId());
                }

                if(spinner.getSelectedItem().toString().equals("Photo")){
                    db.deleteConversationPhotos(conversations.get(positionPopup).getId());
                    conversation.setConversationPhoto(photos);

                }
                else{
                    db.deleteConversationPhotos(conversations.get(positionPopup).getId());
                }
                Log.d("Updating: ", "" + positionPopup);
                Log.d("Proactive Engagement", proactiveEngagement.getText().toString());

                if(isProactive == 1){
                    Log.d("Got in", "1");
                    if(!proactiveEngagement.getText().toString().equals("")){
                        conversation.setConversationProactiveEngagement(proactiveEngagement.getText().toString());
                        db.updateConversation(conversation);
                        pwindo.dismiss();
                    }
                }
                else{
                    Log.d("Got in", "2");
                    Log.d("Listens size", Integer.toString(listens.size()));
                    Log.d("Says size", Integer.toString(says.size()));
                    if(listens.size()!=0 && says.size()!=0){
                        db.updateConversation(conversation);
                        pwindo.dismiss();
                    }
                }
                notifyDataSetChanged();

            }
        });

        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwindo.dismiss();
            }
        });
    }

    public ArrayList<VideoConv> addVideoPopUp(ArrayList<VideoConv> videos, final int positionPopup) {
        pwindo2 = new Dialog(context);
        pwindo2.setContentView(R.layout.edit_video);
        pwindo2.show();

        videoNAME = pwindo2.findViewById(R.id.video_name);
        videoCAT = pwindo2.findViewById(R.id.video_cat);
        videoDESC = pwindo2.findViewById(R.id.video_desc);
        videoURL = pwindo2.findViewById(R.id.video_url);

        videoxButton = pwindo2.findViewById(R.id.x_button_video);
        saveVideo = pwindo2.findViewById(R.id.save_video_popup);


        saveVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<ListenConv> listens = new ArrayList<ListenConv>();
                final ArrayList<SayConv> says = new ArrayList<SayConv>();

                int maxVideoId = db.getVideosMaxID();

                Log.d("Convo_id", Integer.toString(conversations.get(positionPopup).getId()));
                if (false == videoNAME.getText().toString().equals("") && false == videoURL.getText().toString().equals("")) {
                    Log.d("Video", videoNAME.getText().toString());
                    videos.add(new VideoConv(maxVideoId + videoIDPad, conversations.get(positionPopup).getId(), videoURL.getText().toString(), videoCAT.getText().toString(), videoDESC.getText().toString(), videoNAME.getText().toString()));
                    videoIDPad += 1;
                    adptVideo.setNotifyOnChange(true);
                    videoView.setAdapter(adptVideo);
                }
                pwindo2.dismiss();
            }
        });


        videoxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwindo2.dismiss();
            }
        });
        return videos;

    }


}

