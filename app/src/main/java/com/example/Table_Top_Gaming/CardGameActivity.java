package com.example.Table_Top_Gaming;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

public class CardGameActivity extends AppCompatActivity {

    // Create a KEY for passing information to the next activity
    public static final String EXTRA_MESSAGE = "com.example.load.MESSAGE";
    private Game game;
    private Gson gson = new Gson();
    private int currentPlayer;
    private int numPlayers;
    private TextView playerNameHeader;

    /**
     * Initialize values that are needed when the game Activity starts.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_game);

        // Get the intent for this Activity
        Intent intent = getIntent();

        // Grab the information from GameActivity passed to this Activity and place it in the
        // string message
        String message = intent.getStringExtra(GameActivity.EXTRA_MESSAGE);
        Game game = gson.fromJson(message, Game.class);
        currentPlayer = 0;
        numPlayers = game.getPlayers().size();
        playerNameHeader = findViewById(R.id.playerNameHeader);
        playerNameHeader.setText(game.getPlayers().get(currentPlayer).getName());
        RecyclerView recyclerView = findViewById(R.id.handRecyclerView);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(game.getPlayers().get(currentPlayer).getHand());
    }

    public void returnToScore(View view) {
        String gameInformation = gson.toJson(game);
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Game", gameInformation);
        startActivity(intent);
    }
}
