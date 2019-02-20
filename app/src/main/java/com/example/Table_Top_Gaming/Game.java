package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Game extends AppCompatActivity {

    // This might make a merge conflict! Uh oh!
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        // Get the intent for this Activity
        Intent intent = getIntent();

        // Grab the information passed to this Activity and place it in the string "message"
        String message = intent.getStringExtra(LoadGameActivity.EXTRA_MESSAGE);

        // Replace the Text on this Activity with the information of "message" and display it to the
        // screen
        TextView textView = findViewById(R.id.textView2);
        textView.setText(message);
    }
}
