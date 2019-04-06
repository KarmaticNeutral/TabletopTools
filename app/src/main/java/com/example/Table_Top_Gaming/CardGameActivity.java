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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
        setTheme(R.style.AppTheme);

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
            recyclerViewAdapter.notifyDataSetChanged();
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
            recyclerViewAdapter.notifyDataSetChanged();

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
        // Create a new dieRoller that will keep track of all the dice
        final DieRoller dieRoller = new DieRoller();

        // Create a new Alert Dialog and set the view to the dice rolling custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(CardGameActivity.this);
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.activity_roll_dice, null);

        // Create variables for the different text fields on the dice rolling custom layout
        final TextView toBeRolled = view.findViewById(R.id.diceBeingRolled);
        final TextView total = view.findViewById(R.id.sumOfDice);

        // Create buttons for all the different buttons on the dice rolling custom layout
        Button zero = view.findViewById(R.id.zero);
        Button one = view.findViewById(R.id.one);
        Button two = view.findViewById(R.id.two);
        Button three = view.findViewById(R.id.three);
        Button four = view.findViewById(R.id.four);
        Button five = view.findViewById(R.id.five);
        Button six = view.findViewById(R.id.six);
        Button seven = view.findViewById(R.id.seven);
        Button eight = view.findViewById(R.id.eight);
        Button nine = view.findViewById(R.id.nine);
        Button delete = view.findViewById(R.id.delete);
        Button plus = view.findViewById(R.id.roll_plus);
        Button roll = view.findViewById(R.id.roll);
        Button d4 = view.findViewById(R.id.d4);
        Button d6 = view.findViewById(R.id.d6);
        Button d8 = view.findViewById(R.id.d8);
        Button d10 = view.findViewById(R.id.d10);
        Button d20 = view.findViewById(R.id.d20);

        // The zero button has been pressed check for foolish user input
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the to be rolled string is blank or the last character is a space you should not be able to use the zero button
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }

                // Check if the last entered string was that of a die, if it was you should not be able to use the zero button
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // Check if the last entered string was that of a die with 10 or 20 sides, if it is you should not be able to use the zero button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // Having cleared all the checks add the 0 to the string
                toBeRolled.setText(String.format("%s0", toBeRolled.getText().toString()));
            }
        });

        // The one button has been pressed check for foolish user input
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the string is empty you should be able to use the one button
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(String.format("%s1", toBeRolled.getText().toString()));
                    return;
                }

                // If the last button pressed was a die button you should not be able to use the one button
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // If the last button pressed was a die with 10 or 20 sides you should not be able to use the one button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // Having cleared all checks add the one button to the string
                toBeRolled.setText(String.format("%s1", toBeRolled.getText().toString()));
            }
        });

        // The two button has been pressed check for foolish user input
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the string is empty you should be able to use the two button
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(String.format("%s2", toBeRolled.getText().toString()));
                    return;
                }

                // If the last button pressed was a die button you should not be able to use the two button
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // If the last button pressed was a die with 10 or 20 sides you should not be able to use the two button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // Having cleared all checks add the two button to the string
                toBeRolled.setText(String.format("%s2", toBeRolled.getText().toString()));
            }
        });

        // The three button has been pressed check for foolish user input
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the string is empty you should be able to use the three button
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(String.format("%s3", toBeRolled.getText().toString()));
                    return;
                }

                // If the last button pressed was a die button you should not be able to use the three button
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // If the last button pressed was a die with 10 or 20 sides you should not be able to use the three button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // Having cleared all checks add the three button to the string
                toBeRolled.setText(String.format("%s3", toBeRolled.getText().toString()));
            }
        });

        // The four button has been pressed check for foolish user input
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the string is empty you should be able to use the four button
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(String.format("%s4", toBeRolled.getText().toString()));
                    return;
                }

                // If the last button pressed was a die button you should not be able to use the four button
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // If the last button pressed was a die with 10 or 20 sides you should not be able to use the four button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // Having cleared all checks add the four button to the string
                toBeRolled.setText(String.format("%s4", toBeRolled.getText().toString()));
            }
        });

        // The five button has been pressed check for foolish user input
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the string is empty you should be able to use the five button
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(String.format("%s5", toBeRolled.getText().toString()));
                    return;
                }

                // If the last button pressed was a die button you should not be able to use the five button
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // If the last button pressed was a die with 10 or 20 sides you should not be able to use the five button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // Having cleared all checks add the five button to the string
                toBeRolled.setText(String.format("%s5", toBeRolled.getText().toString()));
            }
        });

        // The six button has been pressed check for foolish user input
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the string is empty you should be able to use the six button
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(String.format("%s6", toBeRolled.getText().toString()));
                    return;
                }

                // If the last button pressed was a die button you should not be able to use the six button
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // If the last button pressed was a die with 10 or 20 sides you should not be able to use the six button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // Having cleared all checks add the six button to the string
                toBeRolled.setText(String.format("%s6", toBeRolled.getText().toString()));
            }
        });

        // The seven button has been pressed check for foolish user input
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the string is empty you should be able to use the seven button
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(String.format("%s7", toBeRolled.getText().toString()));
                    return;
                }

                // If the last button pressed was a die button you should not be able to use the seven button
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // If the last button pressed was a die with 10 or 20 sides you should not be able to use the seven button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // Having cleared all checks add the seven button to the string
                toBeRolled.setText(String.format("%s7", toBeRolled.getText().toString()));
            }
        });

        // The eight button has been pressed check for foolish user input
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the string is empty you should be able to use the eight button
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(String.format("%s8", toBeRolled.getText().toString()));
                    return;
                }

                // If the last button pressed was a die button you should not be able to use the eight button
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // If the last button pressed was a die with 10 or 20 sides you should not be able to use the eight button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // Having cleared all checks add the eight button to the string
                toBeRolled.setText(String.format("%s8", toBeRolled.getText().toString()));
            }
        });

        // The nine button has been pressed check for foolish user input
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the string is empty you should be able to use the nine button
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(String.format("%s9", toBeRolled.getText().toString()));
                    return;
                }

                // If the last button pressed was a die button you should not be able to use the nine button
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // If the last button pressed was a die with 10 or 20 sides you should not be able to use the nine button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // Having cleared all checks add the nine button to the string
                toBeRolled.setText(String.format("%s9", toBeRolled.getText().toString()));
            }
        });

        // The D4 button has been pressed check for foolish user input and a some D4 to the dieRoller
        d4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for a number before a die can be added, if there is no number return
                if (toBeRolled.getText().toString().isEmpty()) {
                    return;
                }

                // If the last button pressed was a another die or the plus button you should not be able to use this button yet
                if (toBeRolled.getText().toString().length() > 1) {

                    // Check if the last button pressed was the plus button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == ' ') {
                        return;
                    }

                    // Checks if the last button pressed was a die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // Check if the last button pressed was a die with 10 or 20 sides you should not be able to use this button yet
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // If the string is not empty and the last button pressed was not the plus button grab the last number entered
                if (!toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    int startIndex = 0;

                    // This loop grabs the last number entered if the plus button has been pressed
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    // Grab the correct number of dice
                    int numDice;

                    // If there is multiple dice being rolled grab just the last number of dice added to the string
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex));
                    }

                    // This would be the first amount of dice to be rolled, grab the amount of dice
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    // Add all the dice to the dieRoller, a list of dice.
                    toBeRolled.setText(String.format("%sd4", toBeRolled.getText().toString()));
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(4));
                    }
                }
            }
        });

        // The D6 button has been pressed check for foolish user input and a some D6 to the dieRoller
        d6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for a number before a die can be added, if there is no number return
                if (toBeRolled.getText().toString().isEmpty()) {
                    return;
                }

                // If the last button pressed was a another die or the plus button you should not be able to use this button yet
                if (toBeRolled.getText().toString().length() > 1) {

                    // Check if the last button pressed was the plus button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == ' ') {
                        return;
                    }

                    // Checks if the last button pressed was a die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // Check if the last button pressed was a die with 10 or 20 sides you should not be able to use this button yet
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // If the string is not empty and the last button pressed was not the plus button grab the last number entered
                if (!toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    int startIndex = 0;

                    // This loop grabs the last number entered if the plus button has been pressed
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    // Grab the correct number of dice
                    int numDice;

                    // If there is multiple dice being rolled grab just the last number of dice added to the string
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex));
                    }

                    // This would be the first amount of dice to be rolled, grab the amount of dice
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    // Add all the dice to the dieRoller, a list of dice.
                    toBeRolled.setText(String.format("%sd6", toBeRolled.getText().toString()));
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(6));
                    }
                }
            }
        });

        // The D8 button has been pressed check for foolish user input and a some D8 to the dieRoller
        d8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for a number before a die can be added, if there is no number return
                if (toBeRolled.getText().toString().isEmpty()) {
                    return;
                }

                // If the last button pressed was a another die or the plus button you should not be able to use this button yet
                if (toBeRolled.getText().toString().length() > 1) {

                    // Check if the last button pressed was the plus button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == ' ') {
                        return;
                    }

                    // Checks if the last button pressed was a die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // Check if the last button pressed was a die with 10 or 20 sides you should not be able to use this button yet
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // If the string is not empty and the last button pressed was not the plus button grab the last number entered
                if (!toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    int startIndex = 0;

                    // This loop grabs the last number entered if the plus button has been pressed
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    // Grab the correct number of dice
                    int numDice;

                    // If there is multiple dice being rolled grab just the last number of dice added to the string
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex));
                    }

                    // This would be the first amount of dice to be rolled, grab the amount of dice
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    // Add all the dice to the dieRoller, a list of dice.
                    toBeRolled.setText(String.format("%sd8", toBeRolled.getText().toString()));
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(8));
                    }
                }
            }
        });

        // The D10 button has been pressed check for foolish user input and a some D10 to the dieRoller
        d10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for a number before a die can be added, if there is no number return
                if (toBeRolled.getText().toString().isEmpty()) {
                    return;
                }

                // If the last button pressed was a another die or the plus button you should not be able to use this button yet
                if (toBeRolled.getText().toString().length() > 1) {

                    // Check if the last button pressed was the plus button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == ' ') {
                        return;
                    }

                    // Checks if the last button pressed was a die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // Check if the last button pressed was a die with 10 or 20 sides you should not be able to use this button yet
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // If the string is not empty and the last button pressed was not the plus button grab the last number entered
                if (!toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    int startIndex = 0;

                    // This loop grabs the last number entered if the plus button has been pressed
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    // Grab the correct number of dice
                    int numDice;

                    // If there is multiple dice being rolled grab just the last number of dice added to the string
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex));
                    }

                    // This would be the first amount of dice to be rolled, grab the amount of dice
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    // Add all the dice to the dieRoller, a list of dice.
                    toBeRolled.setText(String.format("%sd10", toBeRolled.getText().toString()));
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(10));
                    }
                }
            }
        });

        // The D20 button has been pressed check for foolish user input and a some D20 to the dieRoller
        d20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for a number before a die can be added, if there is no number return
                if (toBeRolled.getText().toString().isEmpty()) {
                    return;
                }

                // If the last button pressed was a another die or the plus button you should not be able to use this button yet
                if (toBeRolled.getText().toString().length() > 1) {

                    // Check if the last button pressed was the plus button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == ' ') {
                        return;
                    }

                    // Checks if the last button pressed was a die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // Check if the last button pressed was a die with 10 or 20 sides you should not be able to use this button yet
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // If the string is not empty and the last button pressed was not the plus button grab the last number entered
                if (!toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    int startIndex = 0;

                    // This loop grabs the last number entered if the plus button has been pressed
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    // Grab the correct number of dice
                    int numDice;

                    // If there is multiple dice being rolled grab just the last number of dice added to the string
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex));
                    }

                    // This would be the first amount of dice to be rolled, grab the amount of dice
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    // Add all the dice to the dieRoller, a list of dice.
                    toBeRolled.setText(String.format("%sd20", toBeRolled.getText().toString()));
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(20));
                    }
                }
            }
        });

        // The delete button has been pressed, delete the text on the screen and remove dice from the diceRoller if necessary
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check for more than one item in the string
                if (toBeRolled.getText().toString().length() > 1) {

                    // Check if the last entered item in the string was a d4 die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            && toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {

                        // Delete "d4" from the string and remove all the d4 die objects from the list of die
                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -2));
                        for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                            if (dieRoller.getDice().get(i).getNumSides() == 4) {
                                dieRoller.getDice().remove(i);
                                i = 0;
                                if (dieRoller.getDice().size() > 0) {
                                    // This if statement now compares the current die to a "d4".
                                    if (dieRoller.getDice().get(0).getNumSides() == 4 && dieRoller.getDice().size() == 1) {
                                        dieRoller.getDice().clear();
                                    }
                                }
                            }
                        }
                        return;
                    }

                    // Check if the last entered item in the string was a d6 die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            && toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {

                        // Delete "d6" from the string and remove all the d6 die objects from the list of die
                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -2));
                        for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                            if (dieRoller.getDice().get(i).getNumSides() == 6) {
                                dieRoller.getDice().remove(i);
                                i = 0;
                                if (dieRoller.getDice().size() > 0) {
                                    if (dieRoller.getDice().get(0).getNumSides() == 6 && dieRoller.getDice().size() == 1) {
                                        dieRoller.getDice().clear();
                                    }
                                }
                            }
                        }
                        return;
                    }

                    // Check if the last entered item in the string was a d8 die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8'
                            && toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {

                        // Delete "d8" from the string and remove all the d8 die objects from the list of die
                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -2));
                        for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                            if (dieRoller.getDice().get(i).getNumSides() == 8) {
                                dieRoller.getDice().remove(i);
                                i = 0;
                                if (dieRoller.getDice().size() > 0) {
                                    if (dieRoller.getDice().get(0).getNumSides() == 8 && dieRoller.getDice().size() == 1) {
                                        dieRoller.getDice().clear();
                                    }
                                }
                            }
                        }
                        return;
                    }

                    // Check if the last entered item in the string was a d10 die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == '1') {

                                // Delete "d10" from the string and remove all the d10 die objects from the list of die
                                toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -3));
                                for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                                    if (dieRoller.getDice().get(i).getNumSides() == 10) {
                                        dieRoller.getDice().remove(i);
                                        i = 0;
                                        if (dieRoller.getDice().size() > 0) {
                                            if (dieRoller.getDice().get(0).getNumSides() == 10 && dieRoller.getDice().size() == 1) {
                                                dieRoller.getDice().clear();
                                            }
                                        }
                                    }
                                }
                                return;
                            }

                            // Check if the last entered item in the string was a d20 die
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == '2') {

                                // Delete "d20" from the string and remove all the d20 die objects from the list of die
                                toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -3));
                                for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                                    if (dieRoller.getDice().get(i).getNumSides() == 20) {
                                        dieRoller.getDice().remove(i);
                                        i = 0;
                                        if (dieRoller.getDice().size() > 0) {
                                            if (dieRoller.getDice().get(0).getNumSides() == 20 && dieRoller.getDice().size() == 1) {
                                                dieRoller.getDice().clear();
                                            }
                                        }
                                    }
                                }
                                return;
                            }
                            toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -3));
                            return;
                        }
                    }

                    // Check if the last button press was the plus button, if true delete 3 characters at the end of the string
                    if (toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -3));
                        return;
                    }
                }

                // If all the other checks have been cleared delete a character at a time in the string
                if (toBeRolled.getText().toString().length() > 0) {
                    toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -1));
                }
            }
        });

        // The plus button has been press just display a plus on the screen and check for foolish user input
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the last button pressed was a plus or the string is empty to nothing
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }

                // If the last button pressed was a d4, d6, or a d8 add " + " to the string
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            toBeRolled.setText(String.format("%s + ", toBeRolled.getText().toString()));
                            return;
                        }
                    }

                    // If the last button pressed was a d10 or d20 add " + " to the string
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            toBeRolled.setText(String.format("%s + ", toBeRolled.getText().toString()));
                        }
                    }
                }
            }
        });

        // The roll button has been pressed roll all the dice and display the results to the screen and check for foolish user input
        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the string is empty the plus button should not be able to be used
                if (toBeRolled.getText().toString().isEmpty()) {
                    return;
                }

                // Check if the string is not empty and that the last button pressed was not a plus button
                if (!dieRoller.getDice().isEmpty() || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) != ' ') {

                    // If the last button pressed was a die, roll the dice
                    if (toBeRolled.getText().toString().length() > 1) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                                || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                                || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                                dieRoller.rollAllDice();

                                // Display the total sum of the dice rolled and clear the to be rolled text
                                total.setText(dieRoller.display());
                                dieRoller.getDice().clear();
                                toBeRolled.setText("");
                                return;
                            }
                        }

                        // Check if the last button pressed was a d10 or 20 die if so roll the dice
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                                && toBeRolled.getText().toString().length() > 2) {
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                                dieRoller.rollAllDice();

                                // Display the total sum of the dice rolled and clear the to be rolled text
                                total.setText(dieRoller.display());
                                dieRoller.getDice().clear();
                                toBeRolled.setText("");
                            }
                        }
                    }
                }
            }
        });

        builder.setView(view)
                // Add a OK button to the dialog
                .setPositiveButton(R.string.player_score_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user hit "OK" do nothing they are done
                    }
                }).setNegativeButton(R.string.player_score_cancel, new DialogInterface.OnClickListener() {
            // Add a CANCEL button to the dialog
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The user hit "CANCEL" do nothing they are done
            }
        });

        // Create and show the dialog to the screen
        AlertDialog dialog = builder.create();
        dialog.show();

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
