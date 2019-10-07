package com.example.peppercontrol20.AppActivities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.BaseQiChatExecutor;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.QiChatExecutor;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.example.peppercontrol20.Controllers.ChatController;
import com.example.peppercontrol20.ConversationControl.Conversation;
import com.example.peppercontrol20.ConversationControl.ListenConv;
import com.example.peppercontrol20.ConversationControl.SQLiteDatabaseHandler;
import com.example.peppercontrol20.ConversationControl.SayConv;

import com.example.peppercontrol20.ConversationControl.VideoConv;
import com.example.peppercontrol20.MediaPlayers.SingleMedia;
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


    private static final String TAG = "Chatbot" ;

    private String introduction;
    private Say sayIntro, sayIntro2;
    private Chat chat;
    private ArrayList<ListenConv> listenConv;
    private ArrayList<SayConv> sayConv;
    private String activity;
    private int isProactive;
    private String proactiveEngagement;

    ArrayList<Conversation> conversations;
    ArrayList<Conversation> proactiveConversations;

    private ListView listView;
    private ImageView wallpaper;
    public TextView txtView ;
    int event_id;
    SQLiteDatabaseHandler db; //Database
    Handler handler;
    Runnable r;
    Thread thread;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "Robot";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private ChatController chatController;
    private BluetoothDevice connectingDevice;
    private ArrayAdapter<String> discoveredDevicesAdapter;
    private BluetoothAdapter bluetoothAdapter;

    private Handler chatHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Log.d("Message", Integer.toString(msg.what));
            switch (msg.what) {

                case MESSAGE_STATE_CHANGE:
                    Log.d("Message2", Integer.toString(msg.arg1));


                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);

                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);


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
        // REQUIRE BLUETOOTH
        //check device support bluetooth or not
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            finish();
        }

        handler = new Handler();

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        Intent startIntent = getIntent();
        event_id = startIntent.getIntExtra("OpenEventID", -1);
        Log.d("Opened Event", Integer.toString(event_id));
        db = new SQLiteDatabaseHandler(this);
        wallpaper = findViewById(R.id.eventWallpaper);


        if(db.getEventImageByID(event_id)!=null) {
            wallpaper.setImageURI(Uri.parse(db.getEventImageByID(event_id)));
        }
        txtView = findViewById(R.id.textView_strength_right);
        status = findViewById(R.id.status);
        mediaPlayerGuitar = MediaPlayer.create(this, R.raw.wholelottalove);
        mediaPlayerDisco = MediaPlayer.create(this, R.raw.disco);
        chatController =  ChatController.getInstance(chatHandler);
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // TODO: OPEN NEW ACTIVITY?
                    stopHandler();
                    Intent i = new Intent(qiContext, Proactive.class);
                    i.putExtra("OpenEventID", event_id);
                    startActivity(i);

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        QiSDK.register(this, this);

    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        stopHandler();//stop first and then start

        startHandler();
    }
    public void stopHandler() {
        Log.d("Handler","Stopped");
        handler.removeCallbacks(thread);
    }
    public void startHandler() {
        Log.d("Handler","Started");
        handler.postDelayed(thread, 60000); //for 1 minute
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        //Get Intro
        this.qiContext = qiContext;
        Log.d("I m activity", "1");


        startHandler();
        //Init Introduction
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        introduction = sharedPreferences.getString("Intro","Γεια σου Άνθρωπε!");
        int flag = sharedPreferences.getInt("Flag",0);
        if(flag == 0) {
            editor.putInt("Flag", 1).apply();
            Phrase introPhrase = new Phrase(introduction);
            sayIntro = SayBuilder.with(qiContext)
                    .withPhrase(introPhrase)
                    .build();
            sayIntro.run();
        }
        //Init Conversations
        conversations = (ArrayList<Conversation>) db.getAllConversations(event_id);
        proactiveConversations =  new ArrayList<Conversation>();

        String chatBot = "topic: ~chatbot()\n";
        int i = 0;

        for (Conversation conversation : conversations) {
            listenConv = conversation.getConversationListen();
            sayConv = conversation.getConversationSay();
            activity = conversation.getConversationActivity();
            isProactive = conversation.getConversationProactive();

            if(isProactive == 0) {
                //listen text
                chatBot += "concept: (c_" + i + ") [";
                for (int j = 0; j < listenConv.size(); j++) {
                    chatBot += "\"" + listenConv.get(j).listen + "\" ";
                }
                chatBot += "]\n";
                //reply text
                chatBot += "u: (~c_" + i + ") [";
                for (int j = 0; j < sayConv.size(); j++) {
                    chatBot += "\"" + sayConv.get(j).say + "\" ";
                }
                chatBot += "] ";
                //activity execute
                String conversationID = Integer.toString(conversation.getId());
                if (activity.equals("Video") || activity.equals("Photo")) {
                    chatBot += "^execute(pepperActions, " + activity + ", " + conversationID + ")";

                } else {
                    chatBot += "^execute(pepperActions, " + activity + ", " + Integer.toString(-1) + ")";
                }
                chatBot += "\n";
            }
            else{

            }
            // Create a qiChatbot
            i++;
        }
        Log.d("Chat",chatBot);

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
        Future<Void> fchat = chat.async().run();

        // Stop the chat when the qichatbot is done
        qiChatbot.addOnEndedListener(endReason -> {
            Log.d(TAG, "Qichatbot end reason = " + endReason);
            fchat.requestCancellation();
        });
    }

    @Override
    public void onRobotFocusLost() {
        // Remove the listeners from the Chat action.
        //QiSDK.unregister(this, this);
        stopHandler();
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // Nothing here.
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    chatController =  ChatController.getInstance(chatHandler);
                    Toast.makeText(this, "Bluetooth still disabled, turn off application!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister the RobotLifecycleCallbacks for this Activity.
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Flag", 0).apply();
        QiSDK.unregister(this, this);
        stopHandler();

        if (chatController != null) {
            chatController.stop();
            chatHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected  void onPause(){
        super.onPause();
        Log.d("Paused", "StartPepper");
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Flag", 0).apply();
        QiSDK.unregister(this, this);
        stopHandler();

        if (chatController != null) {
            chatController.stop();
            //chatHandler.removeCallbacksAndMessages(null);
            chatHandler.removeCallbacks(null);

        }
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
        //chatAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {

        super.onStart();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            chatController =  ChatController.getInstance(chatHandler);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (chatController != null) {
            if (chatController.getState() == ChatController.STATE_NONE) {
                //chatController = ChatController.getInstance(chatHandler);
                chatController.start();
                chatController.setHandler(chatHandler);
            }
            else{
                chatController = ChatController.getInstance(chatHandler);
                chatController.start();
                chatController.setHandler(chatHandler);

            }
        }
        else{
            //chatController = new ChatController(chatHandler);
        }
    }


    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveredDevicesAdapter.getCount() == 0) {
                    discoveredDevicesAdapter.add(getString(R.string.none_found));
                }
            }
        }
    };


    public void doAction(QiContext qiContext, String action, String conversationID) {
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
        ArrayList<VideoConv> videos = db.getVideos(conversationID);
        if(videos.size() == 1){
            VideoConv singleVideo = videos.get(0);
            Intent i = new Intent(qiContext, SingleMedia.class);


            i.putExtra("Video Name", singleVideo.name);
            i.putExtra("Video Description", singleVideo.description);
            i.putExtra("Video Category", singleVideo.category);
            i.putExtra("Video URL", singleVideo.url);
            qiContext.startActivity(i);
        }
        else {
            Intent i = new Intent(qiContext, VideoPlayers.class);
            // sending data process
            i.putExtra("Video ID", conversationID);
            try {
                qiContext.startActivity(i);
            } catch (Exception e) {
                Log.d("ERROR", e.toString());
            }
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
        Animate animate = AnimateBuilder.with(qiContext) // Create the builder with the context.
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
        Animate animate = AnimateBuilder.with(qiContext) // Create the builder with the context.
                .withAnimation(animation) // Set the animation.
                .build(); // Build the animate action.

        // Add an on started listener to the animate action.
        animate.addOnStartedListener(() -> mediaPlayerDisco.start());

        animate.run();
    }


}
