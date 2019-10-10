package com.example.peppercontrol20.AppActivities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.peppercontrol20.R;

public class Settings extends AppCompatActivity {

    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinner = findViewById(R.id.proactiveSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.proactive_time));
        // Create an ArrayAdapter using the string array and a default spinner layout
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);


        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        int proactiveMinutes = sharedPreferences.getInt("Proactive", 10);
        spinner.setSelection(adapter.getPosition(Integer.toString(proactiveMinutes)));


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt("Proactive", Integer.parseInt(spinner.getItemAtPosition(position).toString())).apply();
                ((TextView)view).setTextColor(getResources().getColor(R.color.white));
                ((TextView)view).setTextSize(20);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editor.putInt("Proactive", 10).apply();
            }
        });
    }
}
