package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
    private RecyclerViewAdapter recyclerViewAdapter;
    private boolean hideHand;

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
        discardPileButton = findViewById(R.id.discardButton);
        if (game.getDiscardPile().size() == 0) {
            discardPileButton.setImageResource(R.drawable.gray_back);
            //TODO get an empty discard image that is better than this.
        } else {
            String cardToDiplay = game.getDiscardPile().get(game.getDiscardPile().size() - 1).getSuit().toString() +
                    game.getDiscardPile().get(game.getDiscardPile().size() - 1).getNumber();
            int id = this.getResources().getIdentifier(cardToDiplay, "drawable", this.getPackageName());
            discardPileButton.setImageResource(id);
        }
        drawPileButton = findViewById(R.id.deckButton);

        hideHand = true;

        initRecyclerView();
    }

    public void shuffleDeck() {
        game.getDeck().shuffle();
    }

    public void putDiscardToDeck() {
        while (game.getDiscardPile().size() > 0) {
            game.getDeck().getDeck().add(game.getDiscardPile().get(0));
            game.getDiscardPile().remove(0);
        }
    }

    public void shuffleClicked(View view) {
        putDiscardToDeck();
        shuffleDeck();
        updateImagesForCardLocations();
    }

    public void updateImagesForCardLocations() {
        if (game.getDiscardPile().size() > 0) {
            String cardToDiplay = game.getDiscardPile().get(game.getDiscardPile().size() - 1).getSuit().toString() +
                    game.getDiscardPile().get(game.getDiscardPile().size() - 1).getNumber();
            int id = this.getResources().getIdentifier(cardToDiplay, "drawable", this.getPackageName());
            discardPileButton.setImageResource(id);
        } else {
            discardPileButton.setImageResource(R.drawable.gray_back);
        }

        if (game.getDeck().getDeck().size() == 0) {
            drawPileButton.setImageResource(R.drawable.gray_back);
        } else {
            drawPileButton.setImageResource(R.drawable.red_back);
        }

        recyclerViewAdapter.notifyDataSetChangedWithExtras(hideHand, game.getPlayers().get(currentPlayer).getHand());

        playerNameHeader.setText(game.getPlayers().get(currentPlayer).getName());

    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: <- What that says.");
        recyclerView = findViewById(R.id.handRecyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(game.getPlayers().get(currentPlayer).getHand(), game.getDiscardPile(), hideHand, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void drawClicked(View view) {
        if (game.getDeck().getDeck().size() > 0) {
            game.getPlayers().get(currentPlayer).getHand().add(game.getDeck().drawCard());
            recyclerViewAdapter.notifyDataSetChanged();
            updateImagesForCardLocations();
        } else {
            Toast.makeText(this, "The Draw Pile Is Empty.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void discardClicked(View view) {
        if(game.getDiscardPile().size() == 0) {
            Toast.makeText(this, "The Discard Pile Is Empty.",
                    Toast.LENGTH_LONG).show();
            discardPileButton.setImageResource(R.drawable.gray_back);
            //TODO get an empty discard image that is better than this.
        } else {
            game.getPlayers().get(currentPlayer).getHand().add(game.getDiscardPile().get(game.getDiscardPile().size() - 1));
            game.getDiscardPile().remove(game.getDiscardPile().size() - 1);
            recyclerViewAdapter.notifyDataSetChanged();

            if (game.getDiscardPile().size() == 0) {
                discardPileButton.setImageResource(R.drawable.gray_back);
                //TODO get an empty discard image that is better than this.
            } else {
                String cardToDiplay = game.getDiscardPile().get(game.getDiscardPile().size() - 1).getSuit().toString() +
                        game.getDiscardPile().get(game.getDiscardPile().size() - 1).getNumber();
                int id = this.getResources().getIdentifier(cardToDiplay, "drawable", this.getPackageName());
                discardPileButton.setImageResource(id);
            }
        }
    }

    public void hideClicked(View view) {
        hideHand = !hideHand;
        updateImagesForCardLocations();
    }

    public void prevPlayerClicked(View view) {
        if (currentPlayer > 0) {
            currentPlayer--;
        }
        else {
            currentPlayer = numPlayers - 1;
        }
        hideHand = true;
        CheckBox checkBox = (CheckBox) findViewById(R.id.hideCheck);
        checkBox.setChecked(true);
        updateImagesForCardLocations();
    }

    public void nextPlayerClicked(View view) {
        if (currentPlayer < numPlayers) {
            currentPlayer++;
        }
        if (currentPlayer >= numPlayers) {
            currentPlayer = 0;
        }
        hideHand = true;
        CheckBox checkBox = (CheckBox) findViewById(R.id.hideCheck);
        checkBox.setChecked(true);
        updateImagesForCardLocations();
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
