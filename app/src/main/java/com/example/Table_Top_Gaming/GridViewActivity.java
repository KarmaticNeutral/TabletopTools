package com.example.Table_Top_Gaming;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

import static com.example.Table_Top_Gaming.R.color.buttonText;
import static com.example.Table_Top_Gaming.R.color.colorAccent;
import static com.example.Table_Top_Gaming.R.color.colorBackground;
import static com.example.Table_Top_Gaming.R.color.defaultText;

/**
 * An activity designed to show the players and resources of the players lined up in a grid
 * to allow them to be seen and read more easily.
 */
public class GridViewActivity extends AppCompatActivity {
    private static final String TAG = "GridViewActivity";
    public static final String EXTRA_MESSAGE_GRID = "com.example.load.GRIDMESSAGE";
    private Game game;
    private Gson gson = new Gson();
    private int numPlayers;

    /**
     * Receive the current game state and set the default values for the activity.
     * @param savedInstanceState - The received instance state from the previous activity.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        setTheme(R.style.DialogTheme);

        // Get the intent for this Activity
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.d(TAG, "onCreate: Extras is empty!");
        } else {
            Log.d(TAG, "onCreate: Extras has content!");
        }

        String message = Objects.requireNonNull(extras).getString(GameActivity.EXTRA_MESSAGE);
        if (message == null) {
            message = extras.getString(CardGameActivity.EXTRA_MESSAGE_CARD);
        }
        game = gson.fromJson(message, Game.class);

        numPlayers = game.getPlayers().size();

        buildTable();
        initBotNav();
    }

    /**
     * Set up the bottom bar that allows app navigation.
     */
    private void initBotNav() {
        final BottomNavigationView bottomNavigationView = findViewById(R.id.navigationMenu);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setSelectedItemId(R.id.navigation_grid);

        View navDice = bottomNavigationView.findViewById(R.id.navigation_dice);
        View navGrid = bottomNavigationView.findViewById(R.id.navigation_grid);
        View navHome = bottomNavigationView.findViewById(R.id.navigation_home);
        View navCard = bottomNavigationView.findViewById(R.id.navigation_cards);
        View navSave = bottomNavigationView.findViewById(R.id.navigation_save);

        navDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diceClicked();
                bottomNavigationView.setSelectedItemId(R.id.navigation_grid);
            }
        });

        navGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set this to do nothing to clear out the old OnClick.
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
                cardClicked();
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
     *
     */
    public void buildTable() {
        TableLayout tableLayout = findViewById(R.id.table_main);
        //Start at -1 so that You get a row showing player names first
        //End at size +1 to get an extra row with player names
        for (int i = -1; i < game.getPlayers().get(0).getResources().size() + 1; i++) {
            TableRow row = new TableRow(this);
            row.setBackgroundColor(getResources().getColor(colorBackground));
            //Start at -1 so that you get a column of resource names first
            for (int j = -1; j < numPlayers; j++) {
                Log.d(TAG, "onCreate: I: " + i + ", J: " + j);
                if (i == -1 || i == game.getPlayers().get(0).getResources().size()) {
                    Log.d(TAG, "onCreate: Player Name Row.");
                    TextView textView = new TextView(this);
                    textView.setBackgroundColor(getResources().getColor(colorBackground));
                    textView.setTextColor(getResources().getColor(defaultText));
                    textView.setWidth(100);
                    textView.setHeight(50);
                    if (j == -1) {
                        textView.setText("");
                    } else {
                        textView.setText(game.getPlayers().get(j).getName());
                    }
                    row.addView(textView);
                } else if (j == -1) {
                    Log.d(TAG, "onCreate: Resource Name #" + i);
                    TextView textView = new TextView(this);
                    textView.setBackgroundColor(getResources().getColor(colorBackground));
                    textView.setWidth(100);
                    textView.setHeight(50);
                    textView.setText(game.getPlayers().get(0).getResources().get(i).getName());
                    textView.setTextColor(getResources().getColor(defaultText));
                    row.addView(textView);
                } else {
                    if (game.getPlayers().get(j).getResources() != null) {
                        Button button = new Button(this);
                        button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(colorAccent)));
                        button.setTextColor(getResources().getColor(buttonText));
                        button.setWidth(100);
                        button.setHeight(50);
                        button.setText(MessageFormat.format("{0}", game.getPlayers().get(j).getResources().get(i).getAmount()));
                        row.addView(button);
                        final int currentPlayer = j;
                        final int resource = i;
                        final int newButtonId = Button.generateViewId();
                        button.setId(newButtonId);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editPlayerScore((Button) GridViewActivity.this.findViewById(newButtonId), currentPlayer, resource);
                            }
                        });
                    }
                }
                if(j == game.getPlayers().size() - 1) {
                    tableLayout.addView(row, i + 1);
                }
            }
        }
    }

    /**
     * Move to the Home activity (GameActivity)
     */
    public void homeClicked() {
        assert game != null;
        String gameInformation = gson.toJson(game);
        Log.d(TAG, "returnToScore: Game Info:" + gameInformation);
        Intent intent = new Intent(GridViewActivity.this, GameActivity.class);
        intent.putExtra(EXTRA_MESSAGE_GRID, gameInformation);
        Log.d(TAG, "returnToScore: ExtraMessageInIntent: " + intent.getStringExtra(EXTRA_MESSAGE_GRID));
        startActivity(intent);
    }

    /**
     * This method is defined so that if the user clicks the button the app won't crash.
     * It does nothing.
     */
    public void gridClicked() {

    }

    /**
     * Move to the Card activity
     */
    public void cardClicked() {
        assert game != null;
        String gameInformation = gson.toJson(game);
        Log.d(TAG, "cardsClicked: Game String: " + gameInformation);
        Intent intent = new Intent(GridViewActivity.this, CardGameActivity.class);
        intent.putExtra(EXTRA_MESSAGE_GRID, gameInformation);
        startActivity(intent);
    }

    /**
     * Move to the Save activity
     */
    public void saveClicked() {
        assert game != null;
        String gameInformation = gson.toJson(game);
        Intent intent = new Intent(this, SaveGameActivity.class);
        intent.putExtra(EXTRA_MESSAGE_GRID, gameInformation);
        startActivity(intent);
    }

    /**
     * This function calls a dialog box that lets a user roll different kinds of dice and displays
     * the results on the screen
     */
    public void diceClicked() {
       DiceDialog diceDialog = new DiceDialog(GridViewActivity.this);
    }

    public void editPlayerScore(final Button callingButton, final int currentPlayer, final int resourceIndex) {

        // Define a new AlertDialog box that will be called from this activity
        AlertDialog.Builder builder = new AlertDialog.Builder(GridViewActivity.this);

        // Using a custom Layout from the EditPlayerScoreDialogActivity so we have to get that view
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.activity_edit_player_score_dialog, null);

        // Get the text field where the user will input a change in score
        final EditText input = view.findViewById(R.id.difference_in_score);
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                input.getText().clear();
            }
        });

        // Get the text view that displays the players score on the new activity window
        final TextView difference = view.findViewById(R.id.player_score);

        // Set the score display on the new window equal to Player 1's score
        String resourceInfo = game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getName();
        resourceInfo += ": " + Integer.toString(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount());
        difference.setText(resourceInfo);

        // There is a plus and a minus button on this custom layout so we have to grab them
        // and define them in this activity
        Button plus = view.findViewById(R.id.plus_button);
        Button minus = view.findViewById(R.id.minus_button);

        // When the user clicks the plus button add the input to the player's score
        plus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                if (input.getText().toString().equals("")) {
                    return;
                }

                // Set this integer to the amount the user wants to change the score by
                int diff = Integer.parseInt(input.getText().toString());

                // Add this amount to the current player score
                game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).setAmount(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount() + diff);

                // Change the display on the new window to the new amount
                String resourceInfo = game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getName();
                resourceInfo += ": " + Integer.toString(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount());
                difference.setText(resourceInfo);

                // Change the display on the GameActivity window score button to the new amount
                callingButton.setText(String.format(" %d", game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount()));
            }
        });

        // When the user clicks the minus button subtract the input from the player's score
        minus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                if (input.getText().toString().equals("")) {
                    return;
                }

                // Set this integer to the amount the user wants to change the score by
                int diff = Integer.parseInt(input.getText().toString());

                // Subtract this amount from the current player score
                game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).setAmount(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount() - diff);

                // Change the display on the new window to the new amount
                String resourceInfo = game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getName();
                resourceInfo += ": " + Integer.toString(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount());
                difference.setText(resourceInfo);

                // Change the display on the GameActivity window score button to the new amount
                callingButton.setText(String.format(" %d", game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount()));
            }
        });

        builder.setView(view)
                .setPositiveButton(R.string.player_score_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user hit "OK" do nothing they are done
                    }
                }).setNeutralButton(R.string.change_score_to, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().equals("")) {
                            return;
                        }

                        // Change the score to the number the user input instead of adding or subtracting it
                        game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).setAmount(Integer.parseInt(input.getText().toString()));
                        callingButton.setText(input.getText().toString());
                    }
                })
                .setNegativeButton(R.string.player_score_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user hit "CANCEL" do nothing they are done
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
