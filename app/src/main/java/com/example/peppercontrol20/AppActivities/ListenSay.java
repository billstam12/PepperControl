package com.example.peppercontrol20.AppActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.example.peppercontrol20.R;

public class ListenSay extends Activity implements RobotLifecycleCallbacks {
    String say = "";
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.transparent);

        intent = getIntent();
        say = intent.getStringExtra("Say");
        QiSDK.register(this, this);

    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Phrase introPhrase = new Phrase(say);
        Say sayLoud = SayBuilder.with(qiContext)
                .withPhrase(introPhrase)
                .build();
        sayLoud.run();
        Log.d("Said", say);
        finish();

    }

    @Override
    public void onRobotFocusLost() {
        QiSDK.unregister(this);
    }

    @Override
    public void onRobotFocusRefused(String s) {

    }
}
