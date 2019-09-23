package com.example.peppercontrol20.AppActivities;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.aldebaran.qi.sdk.Qi;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.BaseQiChatExecutor;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.QiChatExecutor;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.example.peppercontrol20.Controllers.ChatController;
import com.example.peppercontrol20.ConversationControl.Conversation;
import com.example.peppercontrol20.ConversationControl.ListenConv;
import com.example.peppercontrol20.ConversationControl.SQLiteDatabaseHandler;
import com.example.peppercontrol20.ConversationControl.SayConv;

import com.example.peppercontrol20.MediaPlayers.VideoPlayers;
import com.example.peppercontrol20.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartPepper  extends Activity implements RobotLifecycleCallbacks {
    private static final String Tag = "StartPepper";
    private QiContext qiContext;
    private TextView status;

    private Animate animate;
    private MediaPlayer mediaPlayerGuitar;
    private MediaPlayer mediaPlayerDisco;
    private Dialog mVideoDialog ;
    private VideoView videoView;
    private MediaController controller;
    private PopupWindow pwindo;

    private static final String TAG = "Chatbot" ;

    private String introduction;
    private Say sayIntro;
    private Chat chat;
    private ArrayList<ListenConv> listenConv;
    private ArrayList<SayConv> sayConv;
    private String activity;
    private ArrayList<PhraseSet> pepperPhrases;
    private ArrayList<String> pepperResponses;
    private Listen pepperListen;
    ArrayList<Conversation> conversations;
    private ArrayAdapter<String> chatAdapter;
    private ArrayList<String> chatMessages;
    private BluetoothAdapter bluetoothAdapter;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "Robot";
    private BluetoothDevice connectingDevice;
    private ListView listView;
    private ImageView wallpaper;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private ChatController chatController;
    public TextView txtView ;
    int event_id;
    SQLiteDatabaseHandler db; //Database
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Log.d("Message,",msg.toString());

            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case ChatController.STATE_CONNECTED:
                            setStatus("Connected to: " + connectingDevice.getName());
                            //btnConnect.setEnabled(false);
                            break;
                        case ChatController.STATE_CONNECTING:
                            setStatus("Connecting...");
                            //btnConnect.setEnabled(false);
                            break;
                        case ChatController.STATE_LISTEN:
                        case ChatController.STATE_NONE:
                            setStatus("Not connected");
                            //btnConnect.setEnabled(true);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    Log.d("Where,","WRITE");

                    String writeMessage = new String(writeBuf);
                    chatMessages.add("Me: " + writeMessage);
                    chatAdapter.notifyDataSetChanged();
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    Log.d("Where,","READ");

                    String readMessage = new String(readBuf, 0, msg.arg1);
                    chatMessages.add(connectingDevice.getName() + ":  " + readMessage);
                    chatAdapter.notifyDataSetChanged();

                    break;
                case MESSAGE_DEVICE_OBJECT:

                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    Toast.makeText(getApplicationContext(), "Connected to " + connectingDevice.getName(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_pepper);
        //Intent intent = getIntent();
        //chatController = (ChatController) intent.getSerializableExtra("chatController");
        listView = (ListView) findViewById(R.id.list);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



        Intent startIntent = getIntent();
        db = new SQLiteDatabaseHandler(this);
        wallpaper = findViewById(R.id.eventWallpaper);
        event_id = startIntent.getIntExtra("Event", db.getEventsMaxID());
        if(db.getEventImageByID(event_id)!=null) {
            wallpaper.setImageURI(Uri.parse(db.getEventImageByID(event_id)));
        }
        txtView = findViewById(R.id.textView_strength_right);
        status = findViewById(R.id.status);
        mediaPlayerGuitar = MediaPlayer.create(this, R.raw.wholelottalove);
        mediaPlayerDisco = MediaPlayer.create(this, R.raw.disco);
        /*
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            status.setText(savedInstanceState.getString("CONNECT"));
            chatMessages = (ArrayList<String>) savedInstanceState.getSerializable("LIST");
            //Update UI
            //chatAdapter.notifyDataSetChanged();
        }
        else {
            // Probably initialize members with default values for a new instance
            status.setText("Not connected!");
            chatMessages = new ArrayList<>();
            chatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatMessages);
            //set chat adapter
            listView.setAdapter(chatAdapter);
        }
        */

        //status.setText(getIntent().getStringExtra("connectionTest"));
        //chatController = ChatController.getInstance(handler);
        //Log.d("State of controller", Integer.toString(chatController.getState()));
        QiSDK.register(this, this);



    }



    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        //Get Intro
        this.qiContext = qiContext;
        chatController = ChatController.getInstance(handler);
        Boolean holdFlag = false;


        //Init Introduction
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        introduction = sharedPreferences.getString("Intro","Γεια σου Άνθρωπε!");
        Phrase introPhrase = new Phrase(introduction);
        sayIntro = SayBuilder.with(qiContext)
                .withPhrase(introPhrase)
                //.withLocale(locale)
                .build();
        sayIntro.run();

        //Init Conversations
        conversations = (ArrayList<Conversation>) db.getAllConversations(event_id);


        String chatBot = "topic: ~chatbot()\n";
        int i = 0;

        for (Conversation conversation : conversations) {
            listenConv = conversation.getConversationListen();
            sayConv = conversation.getConversationSay();
            activity = conversation.getConversationActivity();
            Log.d("Activity", activity);
            //listen text
            chatBot += "concept: (c_" + i + ") [";
            for(int j = 0; j < listenConv.size(); j++) {
                chatBot += "\"" + listenConv.get(j).listen + "\" ";
            }
            chatBot += "]\n";
            //reply text
            chatBot += "u: (~c_" + i + ") [";
            for( int j = 0; j < sayConv.size(); j++) {
                chatBot += "\"" + sayConv.get(j).say + "\" ";
            }
            chatBot += "] ";
            //activity execute
            String conversationID = Integer.toString(conversation.getId());
            if(activity.equals("Video") || activity.equals("Photo")){
                chatBot += "^execute(pepperActions, " + activity + ", "  + conversationID +")";

            }
            else{
                chatBot += "^execute(pepperActions, " + activity +  ", " + Integer.toString(-1) + ")";
            }
            chatBot += "\n";


            Log.d("Chat",chatBot);


            // Create a qiChatbot
            i++;
        }
        final Topic topic = TopicBuilder.with(qiContext)
                .withText(chatBot)
                .build();
        Map<String, QiChatExecutor> executors = new HashMap<>();

        // Map the executor name from the topic to our qiChatExecutor
        executors.put("pepperActions", new MyQiChatExecutor(qiContext));

        // Set the executors to the qiChatbot
        QiChatbot qiChatbot = QiChatbotBuilder.with(qiContext).withTopic(topic).build();
        chat = ChatBuilder.with(qiContext).withChatbot(qiChatbot).build();
        qiChatbot.setExecutors(executors);

        chat.async().run();


    }

    @Override
    public void onRobotFocusLost() {
        // Remove the listeners from the Chat action.
        if (chat != null) {
            chat.removeAllOnStartedListeners();
        }
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // Nothing here.
    }



    @Override
    public  void onDestroy(){
        QiSDK.unregister(this, this);
        super.onDestroy();

    }

    class MyQiChatExecutor extends BaseQiChatExecutor {
        private final QiContext qiContext;
        MyQiChatExecutor(QiContext context) {
            super(context);
            this.qiContext = context;
        }

        @Override
        public void runWith(List<String> params) {
            // This is called when execute is reached in the topic
            doAction(qiContext, params.get(0), params.get(1));
        }

        @Override
        public void stop() {
            // This is called when chat is canceled or stopped
            Log.i(TAG, "QiChatExecutor stopped");
        }

    }
    private void doAction(QiContext qiContext, String action, String conversationID) {
        // Create an animation.
        Log.d("Action", action);
        if (action.equals("Disco")){
            dance(qiContext);
        }
        else if(action.equals("Guitar")){
            playGuitar(qiContext);
        }
        else if(action.equals("Video")){
            playVideo(qiContext, Integer.parseInt(conversationID));
        }
        else if(action.equals("Photo")){
            playPhotos(qiContext, Integer.parseInt(conversationID));
        }



    }
    public  void playVideo(QiContext qiContext, int conversationID){
        Log.d("Window","Opening Window: " + Integer.toString(conversationID));
        Intent i = new Intent(qiContext, VideoPlayers.class);
        // sending data process
        i.putExtra("Video ID", conversationID);
        try {
            qiContext.startActivity(i);
        }catch (Exception e){
            Log.d("ERROR", e.toString());
        }
        Log.d("Window","Closed Window");

    }

    public  void playPhotos(QiContext qiContext, int conversationID){
        Log.d("Window","Opening Window: " + Integer.toString(conversationID));
        Intent i = new Intent(qiContext, PhotoPresentation.class);
        // sending data process
        i.putExtra("Photo ID", conversationID);
        try {
            qiContext.startActivity(i);
        }catch (Exception e){
            Log.d("ERROR", e.toString());
        }
        Log.d("Window","Closed Window");

    }
    public void playGuitar(QiContext qiContext ) {


        // Create an animation.
        Animation animation = AnimationBuilder.with(qiContext) // Create the builder with the context.
                .withResources(R.raw.guitar_a001) // Set the animation resource.
                .build(); // Build the animation.

        // Create an animate action.
        animate = AnimateBuilder.with(qiContext) // Create the builder with the context.
                .withAnimation(animation) // Set the animation.
                .build(); // Build the animate action.

        // Add an on started listener to the animate action.
        animate.addOnStartedListener(() -> mediaPlayerGuitar.start());

        animate.run();

    }

    public void dance(QiContext qiContext){
        // Create an animation.
        Log.d("Dance", "Now I am Dancing");
        Animation animation = AnimationBuilder.with(qiContext) // Create the builder with the context.
                .withResources(R.raw.disco_a001) // Set the animation resource.
                .build(); // Build the animation.

        // Create an animate action.
        animate = AnimateBuilder.with(qiContext) // Create the builder with the context.
                .withAnimation(animation) // Set the animation.
                .build(); // Build the animate action.

        // Add an on started listener to the animate action.
        animate.addOnStartedListener(() -> mediaPlayerDisco.start());

        animate.run();
    }

    private void setStatus(String s) {
        status.setText(s);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onRestoreInstanceState(savedInstanceState);
        status.setText(savedInstanceState.getString("CONNECT"));
        chatMessages = (ArrayList<String>) savedInstanceState.getSerializable("LIST");
        //chatAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {

        super.onStart();


    }

    @Override
    public void onResume() {
        super.onResume();


    }


}
