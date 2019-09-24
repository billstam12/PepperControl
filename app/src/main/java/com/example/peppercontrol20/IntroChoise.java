package com.example.peppercontrol20;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.peppercontrol20.AppActivities.MainActivity;
import com.example.peppercontrol20.AppActivities.PepperActivity;
import com.example.peppercontrol20.AppActivities.StartPepper;
import com.example.peppercontrol20.ConversationControl.AdminPanel;

public class IntroChoise extends AppCompatActivity {
    Button pepperButton;
    Button serverButton;
    Button adminButton;
    private Dialog pwindo;
    Integer locked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_choise);
        pepperButton = findViewById(R.id.choosePepper);
        serverButton = findViewById(R.id.chooseServer);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        locked = sharedPreferences.getInt("Admin lock", 1);
        adminButton = findViewById(R.id.lockButton);

        if(locked == 0){
            //adminButton.setBackgroundResource(R.drawable.unlock);
            ViewGroup.LayoutParams params = adminButton.getLayoutParams();
            params.width = 40;
            adminButton.setLayoutParams(params);
        }
        pepperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(locked == 1){
                    intent = new Intent(IntroChoise.this, StartPepper.class);

                }
                else {
                    intent = new Intent(IntroChoise.this, AdminPanel.class);
                }
                startActivity(intent);
            }
        });
        serverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(locked == 1){
                    intent = new Intent(IntroChoise.this, StartPepper.class);

                }
                else {
                    intent = new Intent(IntroChoise.this, MainActivity.class);
                }
                startActivity(intent);
            }
        });
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locked == 1){
                    pwindo = new Dialog(IntroChoise.this);
                    pwindo.setContentView(R.layout.login);
                    pwindo.show();

                    EditText email = pwindo.findViewById(R.id.editTextEmail);
                    EditText password = pwindo.findViewById(R.id.editTextPassword);
                    Button loginButton = pwindo.findViewById(R.id.cirLoginButton);

                    TextView wrongPassword = pwindo.findViewById(R.id.wrongPassword);
                    wrongPassword.setVisibility(View.GONE);

                    loginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String emailText = email.getText().toString();
                            String passwordText = password.getText().toString();

                            if (emailText.equals("admin") && passwordText.equals("password")) {

                                //adminButton.setBackgroundResource(R.drawable.unlock);
                                ViewGroup.LayoutParams params = adminButton.getLayoutParams();
                                params.width = 40;
                                adminButton.setLayoutParams(params);
                                locked = 0;
                                editor.putInt("Admin lock", 0).apply();
                                pwindo.dismiss();

                            }
                            else{
                                wrongPassword.setVisibility(View.VISIBLE);
                            }
                        }
                    } );
                }
                else{
                    //adminButton.setBackgroundResource(R.drawable.lock);
                    locked = 1;
                    editor.putInt("Admin lock", 1).apply();

                }
            }
        });
    }
}
