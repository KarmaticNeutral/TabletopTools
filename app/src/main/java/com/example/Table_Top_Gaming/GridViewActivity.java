package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

public class GridViewActivity extends AppCompatActivity {
    private static final String TAG = "GridViewActivity";
    public static final String EXTRA_MESSAGE_GRID = "com.example.load.GRIDMESSAGE";
    private Game game;
    private Gson gson = new Gson();
    private int numPlayers;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);

        // Get the intent for this Activity
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.d(TAG, "onCreate: Extras is empty!");
        } else {
            Log.d(TAG, "onCreate: Extras has content!");
        }

        String message = extras.getString(GameActivity.EXTRA_MESSAGE);
        game = gson.fromJson(message, Game.class);

        numPlayers = game.getPlayers().size();
    }
}
