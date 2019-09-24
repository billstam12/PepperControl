package com.example.peppercontrol20.AppActivities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.example.peppercontrol20.Adapters.ListenAdapter;
import com.example.peppercontrol20.Adapters.PhotoAdapter;
import com.example.peppercontrol20.Adapters.SayAdapter;
import com.example.peppercontrol20.Adapters.VideoAdapter;
import com.example.peppercontrol20.ConversationControl.Conversation;
import com.example.peppercontrol20.ConversationControl.CustomConversationList;
import com.example.peppercontrol20.ConversationControl.ListenConv;
import com.example.peppercontrol20.ConversationControl.PhotoConv;
import com.example.peppercontrol20.ConversationControl.SQLiteDatabaseHandler;
import com.example.peppercontrol20.ConversationControl.SayConv;
import com.example.peppercontrol20.ConversationControl.VideoConv;
import com.example.peppercontrol20.Models.Video;
import com.example.peppercontrol20.R;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SetupRobot extends AppCompatActivity  {

    ArrayList<Conversation> conversations;
    ArrayList<ListenConv> tmpListens;
    Integer tmpListenId = 0;


    ArrayList<SayConv> tmpSays;
    Integer tmpSayId = 0;
    Integer photoId = 0;
    SQLiteDatabaseHandler db;
    Button btnSubmit;
    Button btnAddIntro;
    Button btnViewIntro;
    Button btnAddListen;
    Button btnAddSay;
    Button btnSoundSay;
    Button xButton;
    Button addVideo;
    Button addPhoto;
    Button saveVideo;
    Button videoxButton;
    EditText videoURL;
    EditText videoDESC;
    EditText videoCAT;
    EditText videoNAME;
    int event_id;
    int conversation_id;
    //PopupWindow pwindo;
    Dialog pwindo, pwindo2, pwindo_edit_text, pwindo_edit_video;
    Activity activity;
    ListView listView;
    ListView listenView;
    ListView sayView;
    ListView videoView;
    ListView photoView;
    EditText listenText;
    EditText sayText;
    LinearLayout videoLayout;
    LinearLayout photoLayout;
    ArrayList<VideoConv> videos;
    ArrayList<PhotoConv> photos;
    CustomConversationList customConversationList;
    VideoAdapter adptVideo;
    ImageView imageView;
    PhotoAdapter adptPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get event
        Intent startIntent = getIntent();
        setContentView(R.layout.activity_setup_robot);
        activity=this;

        pwindo = new Dialog(this);

        db = new SQLiteDatabaseHandler(this);
        event_id = startIntent.getIntExtra("Event", db.getEventsMaxID());

        listView = (ListView) findViewById(android.R.id.list);

        //final LinearLayout et2 =(LinearLayout) newView.getChildAt(newView.getChildCount());
        final EditText addIntro = (EditText) findViewById(R.id.addIntro);
        final TextView giveMeText = findViewById(R.id.giveMeSomething);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnAddIntro = (Button) findViewById(R.id.btnSubmitIntro);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPopUp();
            }
        });
        //Introduction Button
        //Shared Pref

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        //If add intro not there add
        giveMeText.setText("When I start I will say: " + sharedPreferences.getString("Intro", "Hello"));
        btnAddIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String introduction = addIntro.getText().toString();
                editor.putString("Intro", introduction).apply();
                giveMeText.setText("When I start I will say: " + introduction);
                addIntro.setText("");
            }
        });



        conversations = (ArrayList<Conversation>) db.getAllConversations(event_id);
        conversation_id = db.getConvoMaxID();
        if(conversation_id != 0 ){
            conversation_id +=1;
        }
        for (Conversation conversation : conversations) {
            String log = "Id: " + conversation.getId() + " ,Listen: " + conversation.getConversationListen() + " ,Say: " + conversation.getConversationSay();
            // Writing Conversations to log
            //Log.d("Name: ", log);
        }

        CustomConversationList customConversationList = new CustomConversationList(this, conversations, db, event_id);
        listView.setAdapter(customConversationList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getApplicationContext(), "You Selected " + conversations.get(position).getConversationListen() + " as Country", Toast.LENGTH_SHORT).show();
            }
        });
        adptPhoto = new PhotoAdapter(this, android.R.layout.simple_list_item_1, photos);
    }

    public void addPopUp() {
        /*
        LayoutInflater inflater = context.getLayoutInflater();

        View layout = inflater.inflate(R.layout.edit_popup,
                (ViewGroup) context.findViewById(R.id.popup_element));
        */
        pwindo = new Dialog(this);
        pwindo.setContentView(R.layout.edit_popup);
        pwindo.show();

        listenText = (EditText) pwindo.findViewById(R.id.editTextListen);
        sayText = (EditText) pwindo.findViewById(R.id.editTextSay);
        listenView = (ListView) pwindo.findViewById(R.id.listViewListen);
        sayView = (ListView) pwindo.findViewById(R.id.listViewSay);
        photoView = pwindo.findViewById(R.id.listViewPhoto);
        btnAddListen = pwindo.findViewById(R.id.listenButton);
        btnAddSay = pwindo.findViewById(R.id.sayButton);
        btnSoundSay = pwindo.findViewById(R.id.saySound);
        xButton = pwindo.findViewById(R.id.x_button);

        photos = new ArrayList<>();
        tmpListens = new ArrayList<ListenConv>();
        tmpSays = new ArrayList<SayConv>();
        addVideo = pwindo.findViewById(R.id.addVideoButton);
        addPhoto = pwindo.findViewById(R.id.addPhotoButton);
        videos  = new ArrayList<>();
        adptVideo = new VideoAdapter(this,
                android.R.layout.simple_list_item_1, videos);
        videoView = pwindo.findViewById(R.id.listViewVideo);
        adptPhoto = new PhotoAdapter(this, android.R.layout.simple_list_item_1, photos);

        photoView.setAdapter(adptPhoto);
        videoView.setAdapter(adptVideo);
        //final ArrayList<String> listensText = new ArrayList<String>();
        //final ArrayList<String> saysText = new ArrayList<String>();

        final ListenAdapter adptListen = new ListenAdapter(this,
                android.R.layout.simple_list_item_1, tmpListens);
        listenView.setAdapter(adptListen);

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
                        tmpListens.remove(pos);
                        //listensText.remove(pos);
                        adptListen.setNotifyOnChange(true);
                        listenView.setAdapter(adptListen);

                        pwindo_edit_text.dismiss();
                    }
                });


                saveEditListen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tmpListens.set(pos, new ListenConv(tmpListens.get(pos).id, tmpListens.get(pos).conv_id,  listenEditText.getText().toString()));
                        //listensText.set(pos, listenEditText.getText().toString());
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

        final SayAdapter adptSay = new SayAdapter(this,
                android.R.layout.simple_list_item_1, tmpSays);
        sayView.setAdapter(adptSay);
        /* Add longpress to Edit */
        sayView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                Log.v("long clicked","pos: " + pos);
                pwindo_edit_text = new Dialog(pwindo.getContext());
                pwindo_edit_text.setContentView(R.layout.edit_text_popup);
                EditText sayEditText = pwindo_edit_text.findViewById(R.id.text);
                SayConv s = (SayConv)arg0.getAdapter().getItem(pos);
                sayEditText.setText(s.say);
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
                        tmpSays.remove(pos);
                        //saysText.remove(pos);
                        adptSay.setNotifyOnChange(true);
                        sayView.setAdapter(adptSay);

                        pwindo_edit_text.dismiss();
                    }
                });


                saveEditListen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tmpSays.set(pos, new SayConv(tmpSays.get(pos).id, tmpSays.get(pos).conv_id,  sayEditText.getText().toString()));
                        //saysText.set(pos, sayEditText.getText().toString());
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
        Context context = this;

        btnAddListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listen = listenText.getText().toString();

                if(listen!= ""){
                    ListenConv tmpListen = new ListenConv();
                    //saysText.add(say);
                    tmpListenId = db.getListenMaxID() + 1;
                    Log.d("Max listen ID", Integer.toString(db.getListenMaxID()));
                    tmpListen = new ListenConv(tmpListenId, conversation_id, listen);
                    tmpListens.add(tmpListen);
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
                    SayConv tmpSay = new SayConv();
                    //saysText.add(say);
                    tmpSayId = db.getSaysMaxID() + 1;
                    Log.d("Max Say ID", Integer.toString(db.getListenMaxID()));
                    tmpSay = new SayConv(tmpSayId, conversation_id, say);
                    tmpSays.add(tmpSay);
                    adptSay.setNotifyOnChange(true);
                    sayView.setAdapter(adptSay);
                }
                sayText.setText("");
                btnAddSay.setEnabled(true);
            }
        });

        btnSoundSay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String say = sayText.getText().toString();
                if(say!= ""){
                    Intent intent = new Intent(SetupRobot.this, ListenSay.class);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.putExtra("Say", say);
                    startActivity(intent);
                }
            }
        });
        Button save = (Button) pwindo.findViewById(R.id.save_popup);

        final Spinner spinner = (Spinner) pwindo.findViewById(R.id.spinnerActivity);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.activities));

        // Create an ArrayAdapter using the string array and a default spinner layout

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        // Hide and show video layout and photo layout
        //Get permission and check if already granted

        videoLayout = pwindo.findViewById(R.id.videoLayout);
        photoLayout = pwindo.findViewById(R.id.photoLayout);
        spinner.setAdapter(adapter);
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
                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Permission is not granted
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(SetupRobot.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                        } else {
                            // No explanation needed; request the permission
                            ActivityCompat.requestPermissions(SetupRobot.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {
                        // Permission has already been granted

                    }
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
        /* Add longpress to Edit */
        videoView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // Open popup
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

                // Set clicks
                Button deleteEditVideo = pwindo_edit_video.findViewById(R.id.delete_video_popup);
                Button saveEditVideo = pwindo_edit_video.findViewById(R.id.save_video_popup);
                Button closeEditButton = pwindo_edit_video.findViewById(R.id.x_button_video);
                deleteEditVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //db.deleteListen(listens);
                        // TODO: add yes or no
                        videos.remove(pos);
                        //listensText.remove(pos);
                        adptVideo.setNotifyOnChange(true);
                        videoView.setAdapter(adptVideo);

                        pwindo_edit_video.dismiss();
                    }
                });


                saveEditVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videos.set(pos, new VideoConv(videos.get(pos).id, videos.get(pos).conv_id, videoURL.getText().toString().replace("dl=0", "raw=1"), videoCat.getText().toString(), videoDesc.getText().toString(), videoName.getText().toString()));
                        //listensText.set(pos, listenEditText.getText().toString());
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
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videos = addVideoPopUp();
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
            }
        });


        //Photo View buttons
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<ListenConv> listens = new ArrayList<ListenConv>();
                final ArrayList<SayConv> says = new ArrayList<SayConv>();

                final String conversationActivity = spinner.getSelectedItem().toString(); // Small, Medium, Large

                int maxListenId = db.getListenMaxID();
                int maxSayId = db.getSaysMaxID();
                //New sol
                for(int i = 0; i < tmpListens.size(); i++){
                    Log.d("Listen ", tmpListens.get(i).listen);
                    listens.add(tmpListens.get(i));
                }

                for(int i = 0; i < tmpSays.size(); i++){
                    Log.d("Say ", tmpSays.get(i).say);
                    says.add(tmpSays.get(i));
                }


                Conversation conversation = new Conversation(conversation_id, event_id, listens, says, videos, photos, conversationActivity);
                if(listens.size()!=0 && says.size()!=0) {
                    db.addConversation(conversation);
                    conversation_id++;
                }
                if(customConversationList==null)
                {
                    customConversationList = new CustomConversationList(activity, conversations, db, event_id);
                    listView.setAdapter(customConversationList);
                }

                customConversationList.conversations = (ArrayList) db.getAllConversations(event_id);
                ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                for (Conversation conversation1 : conversations) {
                    String log = "Id: " + conversation1.getId() + " ,Listen: " + conversation1.getConversationListen() + " ,Say: " + conversation1.getConversationSay();
                    // Writing Countries to log
                    Log.d("Name: ", log);
                }
                try {
                    pwindo.dismiss();
                }catch (Exception e){
                    Log.d("Exception", e.toString());
                }finally {
                    pwindo.dismiss();
                }
            }
        });

        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwindo.dismiss();
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
                    editPhoto args = new editPhoto(conv_id, imageReturnedIntent.getDataString());

                    PhotoObservable.getInstance().sendData(args);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    photoId = db.getPhotosMaxID() + 1;
                    PhotoConv photo = new PhotoConv(photoId, conversation_id, selectedImage);
                    photos.add(photo);
                    adptPhoto.notifyDataSetChanged();
                    
                }
                break;
        }
    }

    public ArrayList<VideoConv> addVideoPopUp() {
        pwindo2 = new Dialog(this);
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
                /*
                // Create video and add to db
                VideoConv video = new VideoConv(maxVideoId + 1, conversation_id, videoURL.getText().toString(), videoNAME.getText().toString(), videoDESC.getText().toString(), videoCAT.getText().toString());

                if(false != videoURL.getText().toString().equals("")){
                    db.addVideo(video);
                }
                */
                Log.d("Convo_id", Integer.toString(conversation_id));
                Log.d("Video URL", videoURL.getText().toString().replace("dl=0", "raw=1"));
                if(false == videoNAME.getText().toString().equals("") && false == videoURL.getText().toString().equals("")) {
                    Log.d("Video", videoNAME.getText().toString()) ;
                    videos.add( new VideoConv(maxVideoId + 1, conversation_id, videoURL.getText().toString().replace("dl=0", "raw=1"), videoCAT.getText().toString(), videoDESC.getText().toString(), videoNAME.getText().toString()));
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
