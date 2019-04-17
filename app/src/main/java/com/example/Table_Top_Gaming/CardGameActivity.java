package com.example.Table_Top_Gaming;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 */
public class CardGameActivity extends AppCompatActivity {
    private static final String TAG = "CardGameActivity";
    // Create a KEY for passing information to the next activity
    public static final String EXTRA_MESSAGE_CARD = "com.example.load.MESSAGE3";
    private Game game;
    private Gson gson = new Gson();
    private int currentPlayer;
    private int numPlayers;
    private TextView playerNameHeader;
    private ImageButton discardPileButton;
    private ImageButton drawPileButton;
    private RecyclerViewAdapter recyclerViewAdapter;
    private boolean hideHand;

    /**
     * Initialize values that are needed when the game Activity starts.
     * @param savedInstanceState - as required by super(savedInstanceState)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_game);
        setTheme(R.style.DialogTheme);

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
        String message = Objects.requireNonNull(extras).getString(GameActivity.EXTRA_MESSAGE);
        if (message == null) {
            message = extras.getString(GridViewActivity.EXTRA_MESSAGE_GRID);
        }
        game = gson.fromJson(message, Game.class);
        currentPlayer = 0;
        Log.d(TAG, "onCreate: Game String: " + message);
        numPlayers = game.getPlayers().size();
        playerNameHeader = findViewById(R.id.playerNameHeader);
        playerNameHeader.setText(game.getPlayers().get(currentPlayer).getName());
        discardPileButton = findViewById(R.id.discardButton);
        if (game.getDiscardPile().size() == 0) {
            discardPileButton.setImageResource(R.drawable.gray_back);
        } else {
            String cardToDiplay = game.getDiscardPile().get(game.getDiscardPile().size() - 1).getSuit().toString() +
                    game.getDiscardPile().get(game.getDiscardPile().size() - 1).getNumber();
            int id = this.getResources().getIdentifier(cardToDiplay, "drawable", this.getPackageName());
            discardPileButton.setImageResource(id);
        }
        drawPileButton = findViewById(R.id.deckButton);

        hideHand = true;

        initRecyclerView();
        initBotNav();
    }

    /**
     * Initialize the bottom navigation bar.
     */
    public void initBotNav() {
        final BottomNavigationView bottomNavigationView = findViewById(R.id.navigationMenu);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setSelectedItemId(R.id.navigation_cards);

        View navDice = bottomNavigationView.findViewById(R.id.navigation_dice);
        View navGrid = bottomNavigationView.findViewById(R.id.navigation_grid);
        View navHome = bottomNavigationView.findViewById(R.id.navigation_home);
        View navCard = bottomNavigationView.findViewById(R.id.navigation_cards);
        View navSave = bottomNavigationView.findViewById(R.id.navigation_save);

        navDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diceClicked();
                bottomNavigationView.setSelectedItemId(R.id.navigation_cards);
            }
        });

        navGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridClicked();
            }
        });

        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeClicked();
            }
        });

        navCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do Nothing. Clear out the old OnClick.
            }
        });

        navSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClicked();
            }
        });
    }

    /**
     * Shuffle the Deck of cards
     */
    public void shuffleDeck() {
        game.getDeck().shuffle();
    }

    /**
     * Puts every card in the dscard pile into the main deck
     */
    public void putDiscardToDeck() {
        while (game.getDiscardPile().size() > 0) {
            game.getDeck().getDeck().add(game.getDiscardPile().get(0));
            game.getDiscardPile().remove(0);
        }
    }

    /**
     * But the cards in the discard pile into the draw pile then randomize it.
     * @param view - The button that called this function onClick
     */
    public void shuffleClicked(View view) {
        putDiscardToDeck();
        shuffleDeck();
        updateImagesForCardLocations();
    }

    /**
     * Update the screen to ensure that all of the correct images are shown
     */
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

    /**
     * Create the recycler view and get it ready to show the hand of the current player.
     */
    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: <- What that says.");
        RecyclerView recyclerView = findViewById(R.id.handRecyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(game.getPlayers().get(currentPlayer).getHand(), game.getDiscardPile(), hideHand, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    /**
     * Take the top card from the draw pile and add it to the end of the player's hand.
     * @param view - The button that called this function onClick
     */
    public void drawClicked(View view) {
        if (game.getDeck().getDeck().size() > 0) {
            game.getPlayers().get(currentPlayer).getHand().add(game.getDeck().drawCard());
            recyclerViewAdapter.notifyDataSetChangedWithExtras(hideHand, game.getPlayers().get(currentPlayer).getHand());
            updateImagesForCardLocations();
        } else {
            Toast.makeText(this, "The Draw Pile Is Empty.",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Take the top card from the discard pile and add it to the end of the player's hand.
     * @param view - The button that called this function onClick
     */
    public void discardClicked(View view) {
        if(game.getDiscardPile().size() == 0) {
            Toast.makeText(this, "The Discard Pile Is Empty.",
                    Toast.LENGTH_LONG).show();
            discardPileButton.setImageResource(R.drawable.gray_back);
        } else {
            game.getPlayers().get(currentPlayer).getHand().add(game.getDiscardPile().get(game.getDiscardPile().size() - 1));
            game.getDiscardPile().remove(game.getDiscardPile().size() - 1);
            recyclerViewAdapter.notifyDataSetChangedWithExtras(hideHand, game.getPlayers().get(currentPlayer).getHand());

            if (game.getDiscardPile().size() == 0) {
                discardPileButton.setImageResource(R.drawable.gray_back);
            } else {
                String cardToDiplay = game.getDiscardPile().get(game.getDiscardPile().size() - 1).getSuit().toString() +
                        game.getDiscardPile().get(game.getDiscardPile().size() - 1).getNumber();
                int id = this.getResources().getIdentifier(cardToDiplay, "drawable", this.getPackageName());
                discardPileButton.setImageResource(id);
            }
        }
    }

    /**
     * Toggle whether the current player's hand is shown or hidden.
     * @param view - The button that called this function onClick
     */
    public void hideClicked(View view) {
        hideHand = !hideHand;
        updateImagesForCardLocations();
    }

    /**
     * Change the current Player to the previous player and repopulate the views with their info.
     * @param view - The button that called this function onClick
     */
    public void prevPlayerClicked(View view) {
        if (currentPlayer > 0) {
            currentPlayer--;
        }
        else {
            currentPlayer = numPlayers - 1;
        }
        hideHand = true;
        CheckBox checkBox = findViewById(R.id.hideCheck);
        checkBox.setChecked(true);
        updateImagesForCardLocations();
    }

    /**
     * Change the current Player to the next player and repopulate the views with their info.
     * @param view - The button that called this function onClick
     */
    public void nextPlayerClicked(View view) {
        if (currentPlayer < numPlayers) {
            currentPlayer++;
        }
        if (currentPlayer >= numPlayers) {
            currentPlayer = 0;
        }
        hideHand = true;
        CheckBox checkBox = findViewById(R.id.hideCheck);
        checkBox.setChecked(true);
        updateImagesForCardLocations();
    }

    /**
     * This function calls a dialog box that lets a user roll different kinds of dice and displays
     * the results on the screen
     */
    public void diceClicked() {
        DiceDialog diceDialog = new DiceDialog(CardGameActivity.this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationMenu);
        bottomNavigationView.setSelectedItemId(R.id.navigation_cards);
    }

    /**
     * Move to the Grid activity.
     */
    public void gridClicked() {
        assert game != null;
        String gameInformation = gson.toJson(game);
        Intent intent = new Intent(this, GridViewActivity.class);
        intent.putExtra(EXTRA_MESSAGE_CARD, gameInformation);
        startActivity(intent);
    }

    /**
     * Move to the Save activity
     */
    public void saveClicked() {
        assert game != null;
        String gameInformation = gson.toJson(game);
        Intent intent = new Intent(this, SaveGameActivity.class);
        intent.putExtra(EXTRA_MESSAGE_CARD, gameInformation);
        startActivity(intent);
    }

    /**
     * Move to the Home activity (GameActivity)
     */
    public void homeClicked() {
        assert game != null;
        String gameInformation = gson.toJson(game);
        Log.d(TAG, "returnToScore: Game Info:" + gameInformation);
        Intent intent = new Intent(CardGameActivity.this, GameActivity.class);
        intent.putExtra(EXTRA_MESSAGE_CARD, gameInformation);
        Log.d(TAG, "returnToScore: ExtraMessageInIntent: " + intent.getStringExtra(EXTRA_MESSAGE_CARD));
        startActivity(intent);
    }

}
