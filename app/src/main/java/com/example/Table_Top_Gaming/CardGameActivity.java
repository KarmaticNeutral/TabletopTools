package com.example.Table_Top_Gaming;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

public class CardGameActivity extends AppCompatActivity {
    private static final String TAG = "CardGameActivity";
    // Create a KEY for passing information to the next activity
    public static final String EXTRA_MESSAGE_CARD = "com.example.load.MESSAGE3";
    private Game game;
    private Gson gson = new Gson();
    private int currentPlayer;
    private int numPlayers;
    private TextView playerNameHeader;
    private RecyclerView recyclerView;
    private ImageButton discardPileButton;
    private ImageButton drawPileButton;
    RecyclerViewAdapter recyclerViewAdapter;

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
//        String message = intent.getStringExtra(GameActivity.EXTRA_MESSAGE);
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.d(TAG, "onCreate: Extras is empty!");
        } else {
            Log.d(TAG, "onCreate: Extras has content!");
        }
        String message = extras.getString(GameActivity.EXTRA_MESSAGE);
        game = gson.fromJson(message, Game.class);
        currentPlayer = 0;
        Log.d(TAG, "onCreate: Game String: " + message);
        numPlayers = game.getPlayers().size();
        playerNameHeader = findViewById(R.id.playerNameHeader);
        playerNameHeader.setText(game.getPlayers().get(currentPlayer).getName());
        discardPileButton = findViewById(R.id.rightCard);
        drawPileButton = findViewById(R.id.leftCard);

        initRecyclerView();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: <- What that says.");
        recyclerView = findViewById(R.id.handRecyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(game.getPlayers().get(currentPlayer).getHand(), game.getDiscardPile(), this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void drawClicked(View view) {
        game.getPlayers().get(currentPlayer).getHand().add(game.getDeck().drawCard());
        recyclerViewAdapter.notifyDataSetChanged();

    }

    public void discardClicked(View view) {
        if(game.getDiscardPile().size() == 0) {
            Toast.makeText(this, "The Discard Pile Is Empty.",
                    Toast.LENGTH_LONG).show();
        } else {
            game.getPlayers().get(currentPlayer).getHand().add(game.getDiscardPile().get(game.getDiscardPile().size() - 1));
            game.getDiscardPile().remove(game.getDiscardPile().size() - 1);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public void returnToScore(View view) {
        assert game != null;
        String gameInformation = gson.toJson(game);
        Log.d(TAG, "returnToScore: Game Info:" + gameInformation);
        Intent intent = new Intent(CardGameActivity.this, GameActivity.class);
        intent.putExtra(this.EXTRA_MESSAGE_CARD, gameInformation);
        Log.d(TAG, "returnToScore: ExtraMessageInIntent: " + intent.getStringExtra(this.EXTRA_MESSAGE_CARD));
        startActivity(intent);
    }
}
