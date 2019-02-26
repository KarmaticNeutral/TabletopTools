package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

public class GameActivity extends AppCompatActivity {
    private Game game;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        // Create a text view object to display the received information
        TextView textView = findViewById(R.id.textView2);

        // Get the intent for this Activity
        Intent intent = getIntent();

        // Grab the information from LoadGameActivity passed to this Activity and place it in the
        // string message
        String message = intent.getStringExtra(LoadGameActivity.EXTRA_MESSAGE);

        // If the GameActivity was reached by way of SaveGameActivity or NewGameActivity message
        // will be null
        if (message == null) {

            // Check to see if there is information to grab from the SaveGameActivity
            message = intent.getStringExtra(SaveGameActivity.EXTRA_MESSAGE_SAVE);

            // Check to see if there is information to grab from the NewGameActivity
            if (message == null) {
                message = intent.getExtras().getString("Game");

            }
        }
        // Set the text view to the recovered information to see if the desired information was
        // acquired
        textView.setText(message);

        // Parse the information saved in the String "message" and place it in the Game object
        game = gson.fromJson(message, Game.class);

    }

    /*
    This function converts the information saved in the game object to a string and passes that to
    the SaveGameActivity
     */
    public void saveGame(View view) {
        String gameInformation = gson.toJson(game);
        Intent intent = new Intent(this, SaveGameActivity.class);
        intent.putExtra("Game", gameInformation);
        startActivity(intent);
    }
}
