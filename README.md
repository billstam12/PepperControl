# Pepper Control Application Documentation

## Table of Contents
1. Introduction
2. Required Tools
3. Code File Description
    3.1 App Activities (### 3.1 AppActivities)
        * Files
4. Build and Compilation
## 1. Introduction

This is the Developer's Manual for the PepperControl Android Application for Headline.
The application is hosted on [this link]( https://github.com/billstam12/PepperControl/).

## 2. Required Tools

The tools required by aldebaran and google to built this application are the following:
1. Latest Version of Android Studio
2. Android Studio Pepper SDK Plugin
3. Android SDK API 19 to 28
4. Android SDK Built-Tools 22,23 and 28
5. QISDK API 4 and above

More information about the required tools can be found [here](https://qisdk.softbankrobotics.com/sdk/doc/pepper-sdk/index.html).
    
## 3. Code File Description

The Code for this package is located inside the **com.example.peppercontrol20** package and contains the following folders/files:
1. **Adapters**
2. **AppActivities**
3. **Controllers**
4. **ConversationControl**
5. **MediaPlayers**
6. **Models**
7. **Services**
8. **IntroChoice** (Activity for User to Choose what app to launch. Its the first screen of the application).

In the following Chapters we will be explaining what each of the above folders and their respective files contain and do. The order of explanation is the one best for understanding the tree of the code.
### 3.1 AppActivities
This folder contains the Activities code for the app. Each of the screens that are displayed on the applicaton derive their function from here.
* #####  AdminPanel
    
    In this file exists the code for the HEADLINE Admin Panel, in which the user can add the Events of the Application. The activity has a single view and some buttons, and when the **ADD EVENT** button is clicked, a popup opens.
    Inside the popup the user gets the edit_event.xml view. In this view he can give a name and a wallaper to save an event. After that in the eventView that is created, he can click an element to open the corresponding event in the **PepperActivity** app, by passing the event_id as Intent.
* ##### PepperActivity
    A simple activity that has Three Buttons. **btnSetup** which calls SetupRobot, **btnTest** which calls TestBluetooth and **btnStart** which calls StartPepper activity. In each of these calls the event_id is again passed as an Intent.
* ##### TestBluetooth
    In this Activity we take advantage of the bluetooth Service explained in a following paragraph, to send and receive messages between the apps.
    With the Joystick app in the Control activity a user with a tablet can connect to Pepper and move the Icon in her Screen.
    The bluetooth functions are standard, and with each message in the Thread we update the coordinates of the Icon accordingly.
* ##### SetupRobot
    ***THIS IS THE MAIN ACTIVITY OF THE APPLICATION***
    With this activity we input Conversation data into the application.
    To do this we have created a relational database, with the following Tables:

    * Event (ID, Νame, Photo, (Icon)), Icon is Deprecated
    * Conversation (ID, Event_ID,Activity, Proactive, Proactive_Engagement, )
    * Listen (ID, Conversation_ID, Listen_Text)
    * Say (ID, Conversation_ID, Say_Text)
    * Video (ID, Conversation_ID, URL, CATEGORY, DESCRIPTION, NAME)
    * Photo (ID, Conversation_ID, URI)
    
    The Database Implementation as well as the Corresponding Java Classes reside within the **Conversation Control** folder and are self-explanatory.
    
    The SetupRobot file has two different actions. One for Creating new stuff and one for editing old stuff.
    The Creation of new elements happens by first getting the maximum number of listens, says, videos and photos from the database. So we can Increment the IDs each time we add a new instance of those in the database when the user clicks the SAVE button.
    When the user clicks the SAVE button he can then choose to Edit or Delete
    the Conversation he created, something that is implemented through the CustomConversationList Adapter inside ConversationControl which has the same view as the edit_popup of setup but instead of saving new Instances it edits the old ones based on ID.
    The functions that are executed in both Activities on button clicks, can be found in the **onClickListeners** of the following buttons:
    1. btnSubmit;
    2. btnAddIntro;
    3. btnViewIntro;
    4. btnAddListen;
    5. btnAddSay;
    6. btnSoundSay;
    7. xButton;
    8. addVideo;
    9. addPhoto;
    10. saveVideo;
    11. videoxButton;
    12. btnMakeProactive;
    
**IT'S IMPORTANT to keep in mind that most of the stuff in the Application, especially in the setup, is done after button clicks. So, to understand the code better, the onClickListeners of the buttons are your friend.**
    
* ##### StartPepper

    This activity is the one called after the setup is done and opens the PepperActivity. It sets as background the choosen Event WallPaper and does a neet trick to convert the conversation data into a pepper Chatbot.
    All the while, this Activity holds an instance of a Bluetooth Thread that is there to handle the Moving of Pepper via bluetooth that we want to add in the future.
* ##### Proactive

    This activity is launched from the StartPepper Activity only if there are 1 or more Proactive conversations in the setup. To do this we use a timer handler in the StartPepper activity that fires up when the Robot remains inactive. To count the inactivity we set the timer in the Settings App and store the value in the SharedPreferences.
* ##### MainActivity
    
    The "Control" Activity. For now only has the joyStick and the Bluetooth Setup.
    
### 3.2 Controllers

Only contains the **ChatController** Activity which is a ready made Activity for sending and receiving messages via Bluetooth.

### 3.3 ConversationControl

As mentioned in Section 3.1, here we have every Activity that handles the data storage and manipulation.

### 3.4 MediaPlayers

This contains two Activities that have to do with the way we handle the Videos.
* ##### SingleMedia 
    
    Simple model to display a videoView.
* ##### VideoPlayers

    The Event Handler for the video list. Uses the **activity_video_players** view.
    
### 3.5 Models
    
The data models for Photos and Videos.

### 3.6 Services

Contains the code for the Bluetooth Background Service

_____
**The rest of the activities are a bit self-explanatory and will be listed quickly**

**EventPhotoObservable** (handles the UI change in the event Photo)
**ListenSay** (calls the empty PepperActivity to test the Say sentence)
**PhotoObservable** (same as the other but for the photos list in conversations)
**Settings** (the setting activity that launches when you press the settings cog in the start screen)

### 3.7 Resources

On the res folder, we can find the different resources each Activity of the Application uses. This includes, views, strings, photos, sounds etc.
    
### 4. Build and Compilation

Just run on Android Studio's Pepper Emulator.
