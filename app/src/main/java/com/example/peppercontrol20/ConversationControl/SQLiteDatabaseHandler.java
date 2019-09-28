package com.example.peppercontrol20.ConversationControl;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.peppercontrol20.ConversationControl.Conversation;
import com.example.peppercontrol20.ConversationControl.ListenConv;
import com.example.peppercontrol20.ConversationControl.SayConv;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version

    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "conversationData";

    // Conversation table name
    private static final String TABLE_CONVO = "Conversation";
    private static final String TABLE_LISTEN = "Listen";
    private static final String TABLE_SAY = "Say";
    private static final String TABLE_VIDEO = "Video";
    private static final String TABLE_PHOTO = "Photo";
    private static final String TABLE_EVENT = "Event";

    // Event Table Columns names
    private static final String EVENT_NAME = "Event_Name";
    private static final String EVENT_PHOTO = "Event_Photo";
    private static final String EVENT_ICON = "Event_Icon";


    // Conversation Table Columns names
    private static final String KEY_ID = "id";
    private static final String EVENT_ID = "Event_ID";
    private static final String ACTIVITY = "Activity";


    // Conversation Table Listen names
    private static final String LISTEN_ID = "Listen_ID";
    private static final String CONV_ID = "Conversation_ID";
    private static final String LISTEN = "Listen";

    // Conversation Table Say names
    private static final String SAY_ID = "Say_ID";
    private static final String SAY = "Say";

    // Conversation Table Video names
    private static final String VIDEO_ID = "Video_ID";
    private static final String VIDEO_URL = "Video_URL";
    private static final String VIDEO_NAME = "Video_NAME";
    private static final String VIDEO_DESCRIPTION = "Video_DESCRIPTION";
    private static final String VIDEO_CATEGORY = "Video_CATEGORY";

    // Conversation Table Photo names
    private static final String PHOTO_ID = "Photo_ID";
    private static final String PHOTO_URL = "Photo_URL";

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_EVENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + EVENT_NAME + " TEXT, " + EVENT_PHOTO + " TEXT, "
                + EVENT_ICON + " TEXT" + ")";
        Log.d("EVENT SQL", CREATE_EVENT_TABLE);
        String CREATE_CONVERSATION_TABLE = "CREATE TABLE " + TABLE_CONVO + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + ACTIVITY + " TEXT, " + EVENT_ID + " TEXT" + ")";

        String CREATE_LISTEN_TABLE = "CREATE TABLE " + TABLE_LISTEN +  "("
                + LISTEN_ID + " INTEGER PRIMARY KEY,"   + CONV_ID + " INTEGER, "  + LISTEN + "  TEXT " + ")";
        String CREATE_SAY_TABLE = "CREATE TABLE " + TABLE_SAY + "("
                + SAY_ID + " INTEGER PRIMARY KEY,"  + CONV_ID + " INTEGER, " + SAY + "  TEXT " + ")";
        String CREATE_VIDEO_TABLE = "CREATE TABLE " + TABLE_VIDEO + "("
                + VIDEO_ID + " INTEGER PRIMARY KEY,"  + CONV_ID + " INTEGER, " + VIDEO_URL + "  TEXT, " +
                VIDEO_CATEGORY + "  TEXT, " + VIDEO_DESCRIPTION + "  TEXT, " + VIDEO_NAME + "  TEXT " + ")";
        String CREATE_PHOTO_TABLE = "CREATE TABLE " + TABLE_PHOTO + "("
                + PHOTO_ID + " INTEGER PRIMARY KEY," + CONV_ID + " INTEGER, " + PHOTO_URL + " TEXT " + ")";
        db.execSQL(CREATE_EVENT_TABLE);
        db.execSQL(CREATE_CONVERSATION_TABLE);
        db.execSQL(CREATE_LISTEN_TABLE);
        db.execSQL(CREATE_SAY_TABLE);
        db.execSQL(CREATE_VIDEO_TABLE);
        db.execSQL(CREATE_PHOTO_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);


        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    public void addEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_ID, event.getId());
        values.put(EVENT_NAME, event.getName());
        if(event.getPhoto()!=null){     values.put(EVENT_PHOTO, event.getPhoto().toString());}
        if(event.getIcon()!=null){values.put(EVENT_ICON, event.getIcon().toString());}

        // Inserting Row
        db.insert(TABLE_EVENT, null, values);
        db.close(); // Closing database connection
    }
    // Adding new conversation
    public  void addConversation(Conversation conversation) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues listenValues = new ContentValues();
        ContentValues sayValues = new ContentValues();
        ContentValues videoValues = new ContentValues();
        ContentValues photoValues = new ContentValues();

        ContentValues values = new ContentValues();

        ArrayList<ListenConv> listens = conversation.getConversationListen();
        ArrayList<SayConv> says = conversation.getConversationSay();
        ArrayList<VideoConv> videos = conversation.getConversationVideo();
        ArrayList<PhotoConv> photos = conversation.getConversationPhoto();
        // Insert Listens to listen table

        for (int i = 0; i < listens.size(); i++){
            listenValues.put(LISTEN_ID, listens.get(i).getId());
            listenValues.put(CONV_ID, listens.get(i).getConvId());
            listenValues.put(LISTEN, listens.get(i).getListen());
            //Log.d("Listen", listens.get(i));
            db.insert(TABLE_LISTEN, null, listenValues);
        }

        // Insert Says to say table
        for (int i = 0; i < says.size(); i++){
            sayValues.put(SAY_ID, says.get(i).getId());
            sayValues.put(CONV_ID, says.get(i).getConvId());
            sayValues.put(SAY, says.get(i).getSay());
            //Log.d("Say", says.get(i));
            db.insert(TABLE_SAY, null, sayValues);

        }

        // Insert Videos to Video table
        for (int i = 0; i < videos.size(); i++){
            videoValues.put(VIDEO_ID, videos.get(i).getId());
            videoValues.put(CONV_ID, videos.get(i).getConv_id());
            videoValues.put(VIDEO_CATEGORY, videos.get(i).getCategory());
            videoValues.put(VIDEO_URL, videos.get(i).getUrl());
            videoValues.put(VIDEO_DESCRIPTION, videos.get(i).getDescription());
            videoValues.put(VIDEO_NAME, videos.get(i).getName());

            db.insert(TABLE_VIDEO, null, videoValues);

        }

        for(int i = 0; i < photos.size(); i++){
            photoValues.put(PHOTO_ID, photos.get(i).getId());
            photoValues.put(CONV_ID, photos.get(i).getConversationId());
            photoValues.put(PHOTO_URL, photos.get(i).getUri().toString());

            db.insert(TABLE_PHOTO, null, photoValues);
        }

        values.put(KEY_ID, conversation.getId()); // Conversation ID
        values.put(ACTIVITY, conversation.getConversationActivity()); // Conversation activity
        values.put(EVENT_ID, conversation.getConversationEventID());
        // Inserting Row
        db.insert(TABLE_CONVO, null, values);
        db.close(); // Closing database connection
    }

    public  void addVideo(VideoConv video) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues videoValues = new ContentValues();

        // Insert Listens to listen table

        videoValues.put(VIDEO_ID, video.getId());
        videoValues.put(CONV_ID, video.getConv_id());
        videoValues.put(VIDEO_NAME, video.getName());
        videoValues.put(VIDEO_DESCRIPTION, video.getDescription());
        videoValues.put(VIDEO_URL, video.getUrl());
        videoValues.put(VIDEO_CATEGORY, video.getCategory());
        //Log.d("Listen", listens.get(i));
        db.insert(TABLE_VIDEO, null, videoValues);

    }

    public void addPhoto(PhotoConv photo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues photoValues = new ContentValues();

        photoValues.put(PHOTO_ID, photo.getId());
        photoValues.put(CONV_ID, photo.getConversationId());
        photoValues.put(PHOTO_URL, photo.getUri().toString());

    }

    public ArrayList<ListenConv> getListens(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorListen = db.query(TABLE_LISTEN, null, CONV_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        ArrayList<ListenConv> listens = new ArrayList<>();
        for(cursorListen.moveToFirst(); !cursorListen.isAfterLast(); cursorListen.moveToNext()) {
            // The Cursor is now set to the right position
            listens.add(new ListenConv(cursorListen.getInt(0), cursorListen.getInt(1), cursorListen.getString(2)));
        }
        return listens;
    }


    public  ArrayList<SayConv> getSays(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorListen = db.query(TABLE_SAY, null, CONV_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        ArrayList<SayConv> says = new ArrayList<>();
        for(cursorListen.moveToFirst(); !cursorListen.isAfterLast(); cursorListen.moveToNext()) {
            // The Cursor is now set to the right position
            says.add(new SayConv(cursorListen.getInt(0), cursorListen.getInt(1), cursorListen.getString(2)));
        }
        return says;
    }

    public  ArrayList<VideoConv> getVideos(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorVideo = db.query(TABLE_VIDEO, null, CONV_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        ArrayList<VideoConv> videos = new ArrayList<>();
        for(cursorVideo.moveToFirst(); !cursorVideo.isAfterLast(); cursorVideo.moveToNext()) {
            // The Cursor is now set to the right position
            Log.d("Video_name", (cursorVideo.getString(5)));

            videos.add(new VideoConv(cursorVideo.getInt(0), cursorVideo.getInt(1), cursorVideo.getString(2),cursorVideo.getString(3),cursorVideo.getString(4),cursorVideo.getString(5)));
        }
        return videos;
    }

    public ArrayList<PhotoConv> getPhotos(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorPhoto = db.query(TABLE_PHOTO, null, CONV_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        ArrayList<PhotoConv> photos = new ArrayList<>();
        for(cursorPhoto.moveToFirst(); !cursorPhoto.isAfterLast(); cursorPhoto.moveToNext()) {
            // The Cursor is now set to the right position

            photos.add(new PhotoConv(cursorPhoto.getInt(0), cursorPhoto.getInt(1), Uri.parse(cursorPhoto.getString(2))));
        }
        return photos;
    }

    public String getActivity(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorSay = db.query(TABLE_CONVO, new String[] {ACTIVITY}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        String activity = new String();
        for(cursorSay.moveToFirst(); !cursorSay.isAfterLast(); cursorSay.moveToNext()) {
            // The Cursor is now set to the right position
            activity = (cursorSay.getString(0));
        }
        return activity;
    }

    // Getting single conversation
    /*
    public  Conversation getConversation(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorListen = db.query(TABLE_LISTEN, new String[] {LISTEN}, CONV_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        Cursor cursorSay = db.query(TABLE_LISTEN, new String[] {LISTEN}, CONV_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursorListen != null)
            cursorListen.moveToFirst();
        if (cursorSay != null)
            cursorSay.moveToFirst();

        ArrayList<ListenConv> listens = new ArrayList<ListenConv>();
        ArrayList<SayConv> says = new ArrayList<SayConv>();

        for(cursorListen.moveToFirst(); !cursorListen.isAfterLast(); cursorListen.moveToNext()) {
            // The Cursor is now set to the right position
            listens.add(new ListenConv(cursorListen.getInt(0), cursorListen.getInt(1), cursorListen.getString(2)));
        }
        for(cursorSay.moveToFirst(); !cursorSay.isAfterLast(); cursorSay.moveToNext()) {
            // The Cursor is now set to the right position
            says.add(new SayConv(cursorSay.getInt(0), cursorSay.getInt(1), cursorSay.getString(2)));
        }
        Conversation conversation = new Conversation(listens, says);
        // return conversation
        return conversation;
    }
    */

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    // Getting All Events
    public ArrayList<Event> getAllEvents() {
        ArrayList<Event> eventsList = new ArrayList();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_EVENT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);



        // cursorListen through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setId(Integer.parseInt(cursor.getString(0)));
                event.setName(cursor.getString(1));
                if(cursor.getString(2) != null) {
                    event.setPhoto(Uri.parse(cursor.getString(2)));
                }
                if(cursor.getString(3) !=null) {
                    event.setIcon(Uri.parse(cursor.getString(3)));
                }
                int event_id = Integer.parseInt(cursor.getString(0));

                ArrayList<Conversation> conversations = new ArrayList<Conversation>();

                conversations = (ArrayList<Conversation>) getAllConversations(event_id);
                event.setConversations(conversations);
                eventsList.add(event);

            } while (cursor.moveToNext());
        }

        // return conversation list
        return eventsList;
    }
    // Getting All Conversations
    public List getAllConversations(int event_id) {
        List conversationList = new ArrayList();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_CONVO + " WHERE " + EVENT_ID + "=" + event_id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);



        // cursorListen through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Conversation conversation = new Conversation();
                conversation.setId(Integer.parseInt(cursor.getString(0)));
                conversation.setConversationActivity(cursor.getString(1));
                int conv_id = Integer.parseInt(cursor.getString(0));

                String selectQuerySay = "SELECT  * FROM " + TABLE_SAY + " WHERE " + CONV_ID +"=" + conv_id;

                String selectQueryListen = "SELECT  * FROM " + TABLE_LISTEN + " WHERE " + CONV_ID +"=" + conv_id;
                String selectQueryVideo = "SELECT  * FROM " + TABLE_VIDEO + " WHERE " + CONV_ID +"=" + conv_id;
                String selectQueryPhoto = "SELECT  * FROM " + TABLE_PHOTO + " WHERE " + CONV_ID +"=" + conv_id;

                Cursor cursorListen = db.rawQuery(selectQueryListen, null);
                Cursor cursorSay = db.rawQuery(selectQuerySay, null);
                Cursor cursorVideo = db.rawQuery(selectQueryVideo, null);
                Cursor cursorPhoto = db.rawQuery(selectQueryPhoto, null);

                // For listens and says
                ArrayList<Integer> listenIds = new ArrayList<Integer>();
                ArrayList<Integer> sayIds = new ArrayList<Integer>();
                ArrayList<Integer> listenConvIds = new ArrayList<Integer>();
                ArrayList<Integer> sayConvIds = new ArrayList<Integer>();
                ArrayList<String> listenStrings = new ArrayList<String>();
                ArrayList<String> sayStrings = new ArrayList<String>();

                ArrayList<ListenConv> listens = new ArrayList<ListenConv>();
                ArrayList<SayConv> says =  new ArrayList<SayConv>();

                // For Videos
                ArrayList<Integer> videoIds = new ArrayList<>();
                ArrayList<Integer> videoConvIds = new ArrayList<>();
                ArrayList<String> videoURL = new ArrayList<>();
                ArrayList<String> videoDesc = new ArrayList<>();
                ArrayList<String> videoCat = new ArrayList<>();
                ArrayList<String> videoName = new ArrayList<>();

                ArrayList<VideoConv> videos = new ArrayList<>();

                // For Photos
                ArrayList<PhotoConv> photos = new ArrayList<>();

                for(cursorListen.moveToFirst(); !cursorListen.isAfterLast(); cursorListen.moveToNext()) {
                    // The Cursor is now set to the right position
                    listenIds.add(cursorListen.getInt(0));
                    listenConvIds.add(cursorListen.getInt(1));
                    listenStrings.add(cursorListen.getString(2));
                    listens.add(new ListenConv(cursorListen.getInt(0),cursorListen.getInt(1),cursorListen.getString(2)));
                }
                for(cursorSay.moveToFirst(); !cursorSay.isAfterLast(); cursorSay.moveToNext()) {
                    // The Cursor is now set to the right position
                    sayIds.add(cursorSay.getInt(0));
                    sayConvIds.add(cursorSay.getInt(1));
                    sayStrings.add(cursorSay.getString(2));
                    says.add(new SayConv(cursorSay.getInt(0),cursorSay.getInt(1),cursorSay.getString(2)));
                }

                for(cursorVideo.moveToFirst(); !cursorVideo.isAfterLast(); cursorVideo.moveToNext()) {
                    // The Cursor is now set to the right position
                    videoIds.add(cursorVideo.getInt(0));
                    videoConvIds.add(cursorVideo.getInt(1));
                    videoURL.add(cursorVideo.getString(2));
                    videoCat.add(cursorVideo.getString(3));
                    videoDesc.add(cursorVideo.getString(4));
                    videoName.add(cursorVideo.getString(5));

                    videos.add(new VideoConv(cursorVideo.getInt(0),cursorVideo.getInt(1),cursorVideo.getString(2),cursorVideo.getString(3),cursorVideo.getString(4),cursorVideo.getString(5)));
                }
                for(cursorPhoto.moveToFirst(); !cursorPhoto.isAfterLast(); cursorPhoto.moveToNext()) {

                    photos.add(new PhotoConv(cursorPhoto.getInt(0), cursorPhoto.getInt(1), Uri.parse(cursorPhoto.getString(2))));
                }
                // Get Activity
                conversation.setConversationListen(listens);
                conversation.setConversationSay(says);
                conversation.setConversationVideo(videos);
                conversation.setConversationPhoto(photos);
                // Adding conversation to list
                conversationList.add(conversation);
            } while (cursor.moveToNext());
        }

        // return conversation list
        return conversationList;
    }

    // Updating single EVENT
    public void updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Conversation> conversations = event.getConversations();
        String name = event.getName();
        Uri photo = event.getPhoto();
        Uri icon = event.getIcon();


        ContentValues eventValues = new ContentValues();


        eventValues.put(KEY_ID, event.getId());
        eventValues.put(EVENT_NAME, event.getName());
        if(event.getPhoto()!=null){ eventValues.put(EVENT_PHOTO, event.getPhoto().toString()); }
        if(event.getIcon()!=null){ eventValues.put(EVENT_ICON, event.getIcon().toString());}

        db.replace(TABLE_EVENT, null, eventValues);

    }

    // Deleting single event
    public void deleteEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete each conversation
        List<Conversation> conversations = getAllConversations(event.getId());
        for(int i = 0; i < conversations.size(); i++){
            Conversation conversation = conversations.get(i);
            Log.d("Deleting Conversation", Integer.toString(conversation.id));

            db.delete(TABLE_CONVO, KEY_ID + " = ?",
                    new String[] { String.valueOf(conversation.getId()) });
            db.delete(TABLE_LISTEN, CONV_ID + " = ?",
                    new String[] { String.valueOf(conversation.getId()) });
            db.delete(TABLE_SAY, CONV_ID + " = ?",
                    new String[] { String.valueOf(conversation.getId()) });
            db.delete(TABLE_VIDEO, CONV_ID + " = ?",
                    new String[] { String.valueOf(conversation.getId()) });
            db.delete(TABLE_PHOTO, CONV_ID + " = ?",
                    new String[] { String.valueOf(conversation.getId()) });
        }
        db.delete(TABLE_EVENT, KEY_ID + " = ?",
                new String[] { String.valueOf(event.getId()) });
        db.close();
    }
    // Updating single conversation
    public void updateConversation(Conversation conversation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<ListenConv> listens = conversation.getConversationListen();
        ArrayList<SayConv> says = conversation.getConversationSay();
        ArrayList<VideoConv> videos = conversation.getConversationVideo();
        ArrayList<PhotoConv> photos = conversation.getConversationPhoto();

        String activity = conversation.getConversationActivity();

        ContentValues listenValues = new ContentValues();
        ContentValues sayValues = new ContentValues();
        ContentValues convoValues = new ContentValues();
        ContentValues videoValues = new ContentValues();
        ContentValues photoValues = new ContentValues();

        //Log.d("Listen size 2:", Integer.toString(listens.size()));
        for (int i = 0; i < listens.size(); i++){
            listenValues.put(LISTEN_ID, listens.get(i).getId());
            listenValues.put(CONV_ID, listens.get(i).getConvId());
            listenValues.put(LISTEN, listens.get(i).getListen());
            //Log.d("Listen", listens.get(i).getListen());
            db.replace(TABLE_LISTEN, null, listenValues);
        }

        // Insert Says to say table
        for (int i = 0; i < says.size(); i++){
            sayValues.put(SAY_ID, says.get(i).getId());
            sayValues.put(CONV_ID, says.get(i).getConvId());
            sayValues.put(SAY, says.get(i).getSay());
            //Log.d("Say", says.get(i));
            db.replace(TABLE_SAY, null, sayValues);

        }

        // Insert Videos to video table

        db.delete(TABLE_VIDEO, CONV_ID + " = ?",
                new String[] { String.valueOf(conversation.getId()) });
        for (int i = 0; i < videos.size(); i++){
            videoValues.put(VIDEO_ID, videos.get(i).getId());
            videoValues.put(CONV_ID, videos.get(i).getConv_id());
            videoValues.put(VIDEO_URL, videos.get(i).getUrl());
            videoValues.put(VIDEO_CATEGORY, videos.get(i).getCategory());
            videoValues.put(VIDEO_DESCRIPTION, videos.get(i).getDescription());
            videoValues.put(VIDEO_NAME, videos.get(i).getName());

            //Log.d("Say", says.get(i));
            db.replace(TABLE_VIDEO, null, videoValues);

        }
        db.delete(TABLE_PHOTO, CONV_ID + " = ?",
                new String[] { String.valueOf(conversation.getId()) });
        // Insert Photos to photo table
        for (int i = 0; i < photos.size(); i++){
            photoValues.put(PHOTO_ID, photos.get(i).getId());
            photoValues.put(CONV_ID, photos.get(i).getConversationId());
            photoValues.put(PHOTO_URL, photos.get(i).getUri().toString());
            Log.d("Photo of save conv", photos.get(i).uri.toString());

            db.replace(TABLE_PHOTO, null, photoValues);

        }

        convoValues.put(KEY_ID, conversation.getId());
        convoValues.put(EVENT_ID, conversation.getConversationEventID());

        convoValues.put(ACTIVITY, conversation.getConversationActivity());
        db.replace(TABLE_CONVO, null, convoValues);

    }


    // Deleting single conversation
    public void deleteConversation(Conversation conversation) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONVO, KEY_ID + " = ?",
                new String[] { String.valueOf(conversation.getId()) });
        db.delete(TABLE_LISTEN, CONV_ID + " = ?",
                new String[] { String.valueOf(conversation.getId()) });
        db.delete(TABLE_SAY, CONV_ID + " = ?",
                new String[] { String.valueOf(conversation.getId()) });
        db.delete(TABLE_VIDEO, CONV_ID + " = ?",
                new String[] { String.valueOf(conversation.getId()) });
        db.delete(TABLE_PHOTO, CONV_ID + " = ?",
                new String[] { String.valueOf(conversation.getId()) });
        db.close();
    }

    public void deleteConversationVideos(int convId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_VIDEO, CONV_ID + " = ?",
                new String[] { String.valueOf(convId) });
        db.close();
    }


    public void deleteConversationListens(int convId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_LISTEN, CONV_ID + " = ?",
                new String[] { String.valueOf(convId) });
        db.close();
    }


    public void deleteConversationSays(int convId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_SAY, CONV_ID + " = ?",
                new String[] { String.valueOf(convId) });
        db.close();
    }

    public void deleteConversationPhotos(int convId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PHOTO, CONV_ID + " = ?",
                new String[] { String.valueOf(convId) });
        db.close();
    }


    public void deleteListen(int listenId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LISTEN, LISTEN_ID + " = ?",
                new String[] {String.valueOf(listenId)});
    }

    public void deleteSay(int sayId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SAY, SAY_ID + " = ?",
                new String[] {String.valueOf(sayId)});
    }

    // Deleting all conversations
    public void deleteAllConversations() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONVO,null,null);
        db.close();
    }



    // Getting conversations Count
    public int getConversationsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONVO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Get Listens size
    public int getListenSize() {
        String countQuery = "SELECT  * FROM " + TABLE_LISTEN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Get Convo max ID
    public int getConvoMaxID() {
        String countQuery = "SELECT MAX(" +  KEY_ID +  ")  FROM " + TABLE_CONVO   ;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(countQuery, null);
            int maxid = (cursor.moveToFirst() ? cursor.getInt(0) : 0);
            return maxid;

        }catch (Exception e){
            return 0;
        }
    }
    // Get Listens max size
    public int getListenMaxID() {
        String countQuery = "SELECT MAX(" +  LISTEN_ID +  ")  FROM " + TABLE_LISTEN   ;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(countQuery, null);
            int maxid = (cursor.moveToFirst() ? cursor.getInt(0) : 0);
            return maxid;

        }catch (Exception e){
            return 0;
        }
    }

    // Get Says max size
    public int getSaysMaxID() {
        String countQuery = "SELECT MAX(" +  SAY_ID +  ")  FROM " + TABLE_SAY   ;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(countQuery, null);
            int maxid = (cursor.moveToFirst() ? cursor.getInt(0) : 0);
            return maxid;

        }catch (Exception e){
            return 0;
        }

        // return count
    }

    // Get Says max size
    public int getVideosMaxID() {
        String countQuery = "SELECT MAX(" +  VIDEO_ID +  ")  FROM " + TABLE_VIDEO   ;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(countQuery, null);
            int maxid = (cursor.moveToFirst() ? cursor.getInt(0) : 0);
            return maxid;

        }catch (Exception e){
            return 0;
        }

        // return count
    }
    // Get Says max size
    public int getPhotosMaxID() {
        String countQuery = "SELECT MAX(" +  PHOTO_ID +  ")  FROM " + TABLE_PHOTO   ;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(countQuery, null);
            int maxid = (cursor.moveToFirst() ? cursor.getInt(0) : 0);
            return maxid;

        }catch (Exception e){
            return 0;
        }

        // return count
    }

    // Get Says max size
    public int getEventsMaxID() {
        String countQuery = "SELECT MAX(" +  KEY_ID +  ")  FROM " + TABLE_EVENT   ;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(countQuery, null);
            int maxid = (cursor.moveToFirst() ? cursor.getInt(0) : 0);
            return maxid;

        }catch (Exception e){
            return 0;
        }

        // return count
    }

    // Get Listens size
    public int getSaySize() {
        String countQuery = "SELECT  * FROM " + TABLE_SAY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getInt(0);
    }

    public String getEventNameByID(int id){
        String countQuery = "SELECT " + EVENT_NAME + " FROM " + TABLE_EVENT + " WHERE " + KEY_ID + " = " + Integer.toString(id) ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();


        // return name
        // return name
        try {
            return cursor.getString(0);


        }catch (Exception e){
            return "";
        }

    }

    public String getEventImageByID(int id){
        String countQuery = "SELECT " + EVENT_PHOTO + " FROM " + TABLE_EVENT + " WHERE " + KEY_ID + " = " + Integer.toString(id) ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();


        // return name
        try {
            return cursor.getString(0);


        }catch (Exception e){
            return "";
        }

    }
}