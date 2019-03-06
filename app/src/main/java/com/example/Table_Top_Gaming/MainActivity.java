package com.example.Table_Top_Gaming;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getPreferences(Context.MODE_PRIVATE);
    }

    /*
        Start a new Activity, or open the "Load a GameActivity" window
    */
    public void loadGame(View view) {
        Intent intent = new Intent(this, LoadGameActivity.class);
        startActivity(intent);
    }

    public void newGame(View view) {
        Intent intent = new Intent (this, NewGameActivity.class);
        startActivity(intent);
    }

    public void onTakePhoto(View view) {
        Intent intent = new Intent (MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }

    public void signIn(View view){
        String cloudUsername = prefs.getString("cloudUsername", " ");
        String cloudPassword = prefs.getString("cloudPassword", " ");

        if (cloudUsername.equals(" ")) {
            //go to get info activity
        }
        else {
            // Connect to FireBase
        }
    }
}
