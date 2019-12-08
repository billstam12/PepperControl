package com.example.peppercontrol20.AppActivities;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peppercontrol20.Controllers.ChatController;
import com.example.peppercontrol20.R;

import java.util.ArrayList;
import java.util.Set;

public class TestBluetooth extends AppCompatActivity {
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "Robot";
    // Make sure to declare as ArrayList so it's Serializable
    static final String STATE_USER = "user";
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final String STATE_COUNTER = "Counter";
    private static final String STATE_ITEMS = "Chat";
    static TextView status;
    static Button btnConnect;
    static TextView percent;
    static TextView angle;
    static TextView coordinates;
    static ImageView pepperIcon;
    private Button btnSetup;
    private Button btnStart;
    private TextView eventName;
    private Dialog dialog;
    private TextInputLayout inputLayout;
    private BluetoothAdapter bluetoothAdapter;
    private ChatController chatController;
    private BluetoothDevice connectingDevice;
    private ArrayAdapter<String> discoveredDevicesAdapter;
    //The BroadcastReceiver uses the Bluetooth service we set up
    //To make a connection
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
    private int mCounter;
    private String mUser;
    private Handler handler;
    // Make sure to declare as ArrayList so it's Serializable
    private ArrayList<ChatController> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_bluetooth);
        findViewsByIds();
        // REQUIRE BLUETOOTH
        //check device support bluetooth or not
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            status.setText(savedInstanceState.getString("CONNECT"));

        } else {
            // Probably initialize members with default values for a new instance

        }
        //The messages are handled in the Secondary Thread by this Handle
        handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {

                    case MESSAGE_STATE_CHANGE:

                        switch (msg.arg1) {
                            case ChatController.STATE_CONNECTED:
                                setStatus("Connected to: " + connectingDevice.getName());
                                btnConnect.setEnabled(false);
                                break;
                            case ChatController.STATE_CONNECTING:
                                setStatus("Connecting...");
                                btnConnect.setEnabled(false);
                                break;
                            case ChatController.STATE_LISTEN:
                            case ChatController.STATE_NONE:
                                setStatus("Not connected");
                                btnConnect.setEnabled(true);
                                break;
                        }
                        break;
                    case MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        String writeMessage = new String(writeBuf);

                        break;
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;

                        String readMessage = new String(readBuf, 0, msg.arg1);
                        String[] readOutput = readMessage.split("\\.");
                        angle.setText(readOutput[0] + " °");
                        percent.setText(readOutput[1] + " %");
                        String coordinatesText = "X: " + readOutput[2] + " Y: " + readOutput[3];
                        coordinates.setText(coordinatesText);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) pepperIcon.getLayoutParams();
                        //Change position
                        int left, top;
                        int newLeft, newTop;

                        int x = Integer.parseInt(readOutput[2]);
                        int y = Integer.parseInt(readOutput[3]);
                        int power = Integer.parseInt(readOutput[1]);
                        left = params.leftMargin;
                        top = params.topMargin;
                        double offset = 0.2;
                        if (y > 40 && y < 60) {
                            if (x < 50) {
                                newLeft = (int) (left - (offset * power));
                            } else {
                                newLeft = (int) (left + (offset * power));
                            }
                        } else {
                            newLeft = left;
                        }
                        if (x > 40 && x < 60) {
                            if (y < 50) {
                                newTop = (int) (top - (offset * power));
                            } else {
                                newTop = (int) (top + (offset * power));

                            }
                        } else {
                            newTop = top;
                        }

                        params.setMargins(newLeft, newTop, 0, 0); //substitute parameters for left, top, right, bottom
                        pepperIcon.setLayoutParams(params);

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

        chatController = ChatController.getInstance(handler);

        resetIcon();

        //show bluetooth devices dialog when click connect button
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrinterPickDialog();
            }
        });
    }

    private void resetIcon() {
        /* This functions resets the Icons place on Start */
        int top = pepperIcon.getTop();
        int bottom = pepperIcon.getBottom();
        int left = pepperIcon.getLeft();
        int right = pepperIcon.getRight();
        Log.d("Top", Integer.toString(top));

        pepperIcon.setTop(50);
        Log.d("Top", Integer.toString(top));

        pepperIcon.setBottom(bottom);
        pepperIcon.setLeft(left);
        pepperIcon.setRight(right);
    }

    private void showPrinterPickDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_bluetooth);
        dialog.setTitle("Bluetooth Devices");

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

        //Initializing bluetooth adapters
        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        //locate listviews and attatch the adapters
        ListView listView = (ListView) dialog.findViewById(R.id.pairedDeviceList);
        ListView listView2 = (ListView) dialog.findViewById(R.id.discoveredDeviceList);
        listView.setAdapter(pairedDevicesAdapter);
        listView2.setAdapter(discoveredDevicesAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedDevicesAdapter.add(getString(R.string.none_paired));
        }

        //Handling listview item click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }

        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void setStatus(String s) {
        Log.d("Setting", s);

        status.setText(s);
    }

    private void connectToDevice(String deviceAddress) {
        bluetoothAdapter.cancelDiscovery();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        chatController.connect(device);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
        outState.putString("CONNECT", status.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onRestoreInstanceState(savedInstanceState);
        status.setText(savedInstanceState.getString("CONNECT"));


    }

    private void findViewsByIds() {
        status = (TextView) findViewById(R.id.status);
        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnStart = findViewById(R.id.btn_start);
        btnSetup = findViewById(R.id.btn_setup);
        angle = findViewById(R.id.textView_angle_right);
        percent = findViewById(R.id.textView_strength_right);
        coordinates = findViewById(R.id.textView_coordinate_right);
        pepperIcon = findViewById(R.id.pepper_icon);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    chatController = ChatController.getInstance(handler);
                    Toast.makeText(this, "Bluetooth still disabled, turn off application!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public void onStart() {

        super.onStart();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            chatController = ChatController.getInstance(handler);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (chatController != null) {
            if (chatController.getState() == ChatController.STATE_NONE) {
                chatController = ChatController.getInstance(handler);
                chatController.start();
                chatController.setHandler(handler);

            } else {
                chatController = ChatController.getInstance(handler);
                chatController.start();
                chatController.setHandler(handler);

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatController != null) {
            chatController.stop();
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Paused", "TestBluetooth");

        if (chatController != null) {
            chatController.stop();

            //handler.removeCallbacksAndMessages(null);
            handler.removeCallbacks(null);
        }
    }


}
