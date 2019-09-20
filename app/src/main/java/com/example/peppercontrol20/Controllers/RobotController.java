package com.example.peppercontrol20.Controllers;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peppercontrol20.Controllers.ChatController;
import com.example.peppercontrol20.R;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class RobotController extends AppCompatActivity {

    private TextView mTextViewAngleLeft;
    private TextView mTextViewStrengthLeft;

    private TextView mTextViewAngleRight;
    private TextView mTextViewStrengthRight;
    private TextView mTextViewCoordinateRight;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "Robot";
    ChatController chatController;
    private BluetoothDevice connectingDevice;

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_controller);
        status = (TextView) findViewById(R.id.status);
        status.setText(getIntent().getExtras().getString("connectionTest"));
        //TODO get state
        /*
        mTextViewAngleLeft = (TextView) findViewById(R.id.textView_angle_left);
        mTextViewStrengthLeft = (TextView) findViewById(R.id.textView_strength_left);

        JoystickView joystickLeft = (JoystickView) findViewById(R.id.joystickView_left);
        joystickLeft.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                mTextViewAngleLeft.setText(angle + "°");
                mTextViewStrengthLeft.setText(strength + "%");
            }
        });

        */


        final Handler handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case ChatController.STATE_CONNECTED:
                                setStatus("Connected to: " + connectingDevice.getName());

                                break;
                            case ChatController.STATE_CONNECTING:
                                setStatus("Connecting...");
                                break;
                            case ChatController.STATE_LISTEN:
                            case ChatController.STATE_NONE:
                                setStatus("Not connected");
                                break;
                        }
                        break;
                    case MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;

                        String writeMessage = new String(writeBuf);

                        break;
                    case MESSAGE_READ:
                        /*
                        byte[] readBuf = (byte[]) msg.obj;

                        String readMessage = new String(readBuf, 0, msg.arg1);
                        */
                        break;
                    case MESSAGE_TOAST:
                        Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        ChatController chatController =  ChatController.getInstance(handler);

        mTextViewAngleRight = (TextView) findViewById(R.id.textView_angle_right);
        mTextViewStrengthRight = (TextView) findViewById(R.id.textView_strength_right);
        mTextViewCoordinateRight = findViewById(R.id.textView_coordinate_right);

        final JoystickView joystickRight = (JoystickView) findViewById(R.id.joystickView_right);
        joystickRight.setOnMoveListener(new JoystickView.OnMoveListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onMove(int angle, int strength) {
                mTextViewAngleRight.setText(angle + "°");
                mTextViewStrengthRight.setText(strength + "%");
                mTextViewCoordinateRight.setText(
                        String.format("X%03d:Y%03d",
                                joystickRight.getNormalizedX(),
                                joystickRight.getNormalizedY())
                );

                //chatController = ChatController.getInstance(handler);
                sendMessage("X:" + joystickRight.getNormalizedX() + "Y:" + joystickRight.getNormalizedY(), chatController);
            }
        });
    }

    private void sendMessage(String message, ChatController chatController) {

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            chatController.write(send);
        }
    }

    private void setStatus(String s) {
        status.setText(s);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
        //outState.putString("CONNECT", status.getText().toString());
        //outState.putSerializable("LIST", chatMessages);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onRestoreInstanceState(savedInstanceState);
        status.setText(savedInstanceState.getString("CONNECT"));
        //chatMessages = (ArrayList<String>) savedInstanceState.getSerializable("LIST");
        //Update UI
        //chatAdapter.notifyDataSetChanged();

    }
}
