package com.example.peppercontrol20.AppActivities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.BaseQiChatExecutor;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;
import com.aldebaran.qi.sdk.util.PhraseSetUtil;
import com.example.peppercontrol20.ConversationControl.Conversation;
import com.example.peppercontrol20.ConversationControl.ListenConv;
import com.example.peppercontrol20.ConversationControl.SQLiteDatabaseHandler;
import com.example.peppercontrol20.ConversationControl.SayConv;
import com.example.peppercontrol20.ConversationControl.VideoConv;
import com.example.peppercontrol20.MediaPlayers.SingleMedia;
import com.example.peppercontrol20.MediaPlayers.VideoPlayers;
import com.example.peppercontrol20.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Proactive extends Activity implements RobotLifecycleCallbacks {
    SQLiteDatabaseHandler db;
    QiContext qiContext;
    private ArrayList<ListenConv> listenConv;
    private ArrayList<SayConv> sayConv;
    private String activity;
    private int isProactive;
    private int event_id;
    ArrayList<Conversation> proactiveConversations, conversations;
    private HumanAwareness humanAwareness;
    String chatBot = "topic: ~chatbot()\n";
    private MediaPlayer mediaPlayerGuitar;
    private MediaPlayer mediaPlayerDisco;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.transparent);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        Intent startIntent = getIntent();
        event_id = startIntent.getIntExtra("OpenEventID", -1);
        mediaPlayerGuitar = MediaPlayer.create(this, R.raw.wholelottalove);
        mediaPlayerDisco = MediaPlayer.create(this, R.raw.disco);
        QiSDK.register(this, this);


    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        db = new SQLiteDatabaseHandler(qiContext);
        proactiveConversations =  new ArrayList<Conversation>();
        conversations = (ArrayList<Conversation>) db.getAllConversations(event_id);
        Log.d("I m activity", "2");
        int i = 0;
        for (Conversation conversation : conversations) {
            listenConv = conversation.getConversationListen();
            sayConv = conversation.getConversationSay();
            activity = conversation.getConversationActivity();
            isProactive = conversation.getConversationProactive();

            if (isProactive == 1) {
                proactiveConversations.add(conversation);
            }
        }
        // Pick rand and do a random action from all
        Random r = new Random();
        int low = 0;
        int high = proactiveConversations.size();
        int result = r.nextInt(high-low) + low;
        Conversation conversation = proactiveConversations.get(result);
        ArrayList<SayConv> says = conversation.getConversationSay();
        ArrayList<ListenConv> listens = conversation.getConversationListen();

        //TODO: Maybe add human engagement
        Phrase introPhrase = new Phrase(conversation.getConversationProactiveEngagement());

        Say say = SayBuilder.with(qiContext)
                .withText(conversation.getConversationProactiveEngagement())  //Give engagement text
                .build();
        say.run();

        ArrayList<Phrase> listenPhrases = new ArrayList<Phrase>();

        for(i = 0; i < listens.size(); i++){
            Log.d("Phrase", listens.get(i).listen);
            listenPhrases.add(new Phrase(listens.get(i).listen));
        }
        PhraseSet listenPhraseSet = PhraseSetBuilder.with(qiContext) // Create the builder using the QiContext.
                .withPhrases(listenPhrases)
                .build(); // Build the PhraseSet.

        // Create a new listen action.
        Listen listenTo = ListenBuilder.with(qiContext) // Create the builder with the QiContext.
                .withPhraseSets(listenPhraseSet) // Set the PhraseSets to listen to.
                .build(); // Build the listen action.

        ListenResult listenResult = listenTo.run();
        // Identify the matched phrase set.

        // Get Random Respons
        r = new Random();
        low = 0;
        high = says.size();
        result = r.nextInt(high-low) + low;
        PhraseSet matchedPhraseSet = listenResult.getMatchedPhraseSet();
        if(PhraseSetUtil.equals(matchedPhraseSet, listenPhraseSet)){
            Phrase sayPhrase = new Phrase(says.get(result).say);


            say = SayBuilder.with(qiContext)
                    .withPhrase(sayPhrase)
                    .build();

            say.run();
            finish();
        }

    }

    @Override
    public void onRobotFocusLost() {
        QiSDK.unregister(this, this);
        finish();
    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    @Override
    protected void onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
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
        }

    }

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

        //animate.run();

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

        //animate.run();
    }


}
