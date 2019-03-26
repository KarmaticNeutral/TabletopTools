package com.example.Table_Top_Gaming;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import static android.view.View.generateViewId;

public class GameActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private static final String TAG = "GameActivity";

    // Create a KEY for passing information to the next activity
    public static final String EXTRA_MESSAGE = "com.example.load.MESSAGE";

    private Game game;
    private Gson gson = new Gson();
    private int currentPlayer;
    private int numPlayers;
    private TextView playerNameHeader;
    private GameActivity.CustomAdapter customAdapter;
    private GestureDetectorCompat detector;
    private String newResourceName;
    private int defaultResourceValue;

    private ImageButton profilePicture;

    /**
     * Initialize values that are needed when the game Activity starts.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Set player to player number 1
        currentPlayer = 0;
        newResourceName = "";
        defaultResourceValue = 0;

        // Get the intent for this Activity
        Intent intent = getIntent();

        // Grab the information from LoadGameActivity passed to this Activity and place it in the
        // string message
        String message = intent.getStringExtra(LoadGameActivity.EXTRA_MESSAGE);
        Log.d(TAG, "onCreate: Extra From Load" + message);
        // If the GameActivity was reached by way of SaveGameActivity or NewGameActivity message
        // will be null
        if (message == null) {

            // Check to see if there is information to grab from the SaveGameActivity
            message = intent.getStringExtra(SaveGameActivity.EXTRA_MESSAGE_SAVE);
            Log.d(TAG, "onCreate: Extra From Save: " + message);
            // Check to see if there is information to grab from the NewGameActivity
            if (message == null) {

                message = intent.getStringExtra(CardGameActivity.EXTRA_MESSAGE_CARD);
                Log.d(TAG, "onCreate: Extra From Card: " + message);
                if (message == null) {
                    //Check to see if there is information to grab from CardGameActivity
                    //message = intent.getStringExtra(CardGameActivity.EXTRA_MESSAGE);
                    if (intent.getExtras() != null) {
                        message = intent.getExtras().getString("Game");
                        if (intent.getExtras().getString("CurrentPlayer") != null) {
                            currentPlayer = Integer.parseInt(intent.getExtras().getString("CurrentPlayer"));
                        }
                    }
                }
            }
        }

        detector = new GestureDetectorCompat(this,this);

        // Set player to player number 1
        currentPlayer = 0;
        Log.d(TAG, "onCreate: GameContentMessage: " + message);
        // Parse the information saved in the String "message" and place it in the Game object
        game = gson.fromJson(message, Game.class);

        numPlayers = game.getPlayers().size();

        //Create a custom adapter for this activity
        customAdapter = new CustomAdapter(this);
        // Predefine  the ListView
        ListView resourceListView = (ListView) findViewById(R.id.resourceListView);
        //Set up the custom adapter for the discovered ListView.
        resourceListView.setAdapter(customAdapter);

        playerNameHeader = (TextView) findViewById(R.id.playerNameHeader);

        profilePicture = (ImageButton) findViewById(R.id.playerProfileImageButton);

        setPlayerView();
    }

    /**
     * This function converts the information saved in the game object to a string and passes that
     * to the SaveGameActivity.
     * @param view
     */
    public void saveGame(View view) {
        String gameInformation = gson.toJson(game);
        Intent intent = new Intent(this, SaveGameActivity.class);
        intent.putExtra("Game", gameInformation);
        startActivity(intent);
    }

    /**
     * If it is appropriate for currentPlayer to draw a card, they do.
     * @param view - The View Object that called this function.
     */
    public void drawCard(View view) {

        if (!game.getDeck().getDeck().isEmpty()) {
            game.getPlayers().get(currentPlayer).addCardToHand(game.getDeck().drawCard());
        }

        else {
            Toast.makeText(this, "Cannot draw a card. There are no more cards in the deck",
                    Toast.LENGTH_LONG).show();
        }

        Log.i("GameActivity", game.getPlayers().get(currentPlayer).getName() + " has drawn a card");
    }

    /**
     * If it is appropriate for currentPlayer to draw a full hand of cards, they do.
     * @param view - The View Object that called this function.
     */
    public void drawHand(View view) {
        int numCards = 5;

        if (game.getDeck().getDeck().isEmpty()) {
            Toast.makeText(this, "Cannot draw a hand. There are no more cards in the deck",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!game.getPlayers().get(currentPlayer).canDraw()) {
            Toast.makeText(this, game.getPlayers().get(currentPlayer).getName() +
                            " has already drawn a hand", Toast.LENGTH_LONG).show();
            return;
        }
        game.getPlayers().get(currentPlayer).setHand(game.getDeck().drawHand(numCards));

        Log.i("GameActivity", game.getPlayers().get(currentPlayer).getName() + " has drawn a hand");
    }

    /**
     * This function Allows the editing of a players score when the score button is pressed.
     * @param callingButton - The button that called this function.
     * @param resourceIndex - The index in the resource list of the currentPlayer that is being modified.
     */
    public void editPlayerScore (final Button callingButton, final int resourceIndex) {
        // Define a new AlertDialog box that will be called from this activity
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

        // Using a custom Layout from the EditPlayerScoreDialogActivity so we have to get that view
        View view = getLayoutInflater().inflate(R.layout.activity_edit_player_score_dialog, null);

        // Get the text field where the user will input a change in score
        final EditText input = (EditText) view.findViewById(R.id.difference_in_score);

        // Get the text view that displays the players score on the new activity window
        final TextView difference = (TextView) view.findViewById(R.id.player_score);

        // Set the score display on the new window equal to Player 1's score
        String resourceInfo = game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getName();
        resourceInfo += ": " + Integer.toString(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount());
        difference.setText(resourceInfo);

        // There is a plus and a minus button on this custom layout so we have to grab them
        // and define them in this activity
        Button plus = (Button) view.findViewById(R.id.plus_button);
        Button minus = (Button) view.findViewById(R.id.minus_button);

        // When the user clicks the plus button add the input to the player's score
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set this integer to the amount the user wants to change the score by
                int diff = Integer.parseInt(input.getText().toString());

                // Add this amount to the current player score
                game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).setAmount(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount() + diff);

                // Change the display on the new window to the new amount
                String resourceInfo = game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getName();
                resourceInfo += ": " + Integer.toString(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount());
                difference.setText(resourceInfo);

                // Change the display on the GameActivity window score button to the new amount
                callingButton.setText(" " + game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount());
            }
        });

        // When the user clicks the minus button subtract the input from the player's score
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set this integer to the amount the user wants to change the score by
                int diff = Integer.parseInt(input.getText().toString());

                // Subtract this amount from the current player score
                game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).setAmount(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount() - diff);

                // Change the display on the new window to the new amount
                String resourceInfo = game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getName();
                resourceInfo += ": " + Integer.toString(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount());
                difference.setText(resourceInfo);

                // Change the display on the GameActivity window score button to the new amount
                callingButton.setText(" " + game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount());
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

                        // Change the score to the number the user input instead of adding or subtracting it
                        game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).setAmount(Integer.parseInt(input.getText().toString()));
                        setPlayerView();
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

    /**
     * Update the display to show that the currentPlayer has changed.
     */
    public void setPlayerView() {
        playerNameHeader.setText(game.getPlayers().get(currentPlayer).getName());
        String uriString = game.getPlayers().get(currentPlayer).getPathToImage();
        if (uriString != null) {
            profilePicture.setImageURI(Uri.parse(uriString));
        }
        else {
           profilePicture.setImageResource(R.drawable.ic_default_avatar_24dp);
        }

        customAdapter.notifyDataSetChanged();
    }

    /**
     * Shift the currentPlayer to be the next player in the List in the positive direction.
     */
    public void nextPlayerView() {
        if (currentPlayer < numPlayers) {
            currentPlayer++;
        }
        if (currentPlayer >= numPlayers) {
            currentPlayer = 0;
        }
        // Make the display reflect the change in the data structure.
        setPlayerView();
    }

    /**
     * Shift the currentPlayer to be the next player in the List in the negative direction.
     */
    public void previousPlayerView() {
        if (currentPlayer > 0) {
            currentPlayer--;
        }
        else {
            currentPlayer = numPlayers - 1;
        }
        // Make the display reflect the change in the data structure.
        setPlayerView();
    }

    /**
     * Add a blank resource to the resource List.
     * @param view2 - The View that called the function.
     */
    public void onClickAddResource(View view2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

        View view = getLayoutInflater().inflate(R.layout.activity_add_resource_dialog, null);

        final EditText resourceName = (EditText) view.findViewById(R.id.enterResourceName);


        final EditText resourceValue = (EditText) view.findViewById(R.id.enterResourceValue);

        builder.setView(view)
                .setPositiveButton(R.string.add_resource_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user hit "OK" add the new resource with the name and amount they are done
                        game.getPlayers().get(currentPlayer).getResources().add(new Resource(newResourceName, defaultResourceValue));
                        game.getPlayers().get(currentPlayer).getResources().get(game.getPlayers().get(currentPlayer).getResources().size() - 1).setName(resourceName.getText().toString());
                        game.getPlayers().get(currentPlayer).getResources().get(game.getPlayers().get(currentPlayer).getResources().size() - 1).setAmount(Integer.parseInt(resourceValue.getText().toString()));

                        // Make sure the Display reflects the change in the data structure.
                        customAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.add_resource_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user hit "CANCEL" do nothing they are done
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Launches a dialog to allow user to select camera or gallery.
     * @param view The button that called the function.
     */
    public void cameraDialog(View view) {
        String gameGson = gson.toJson(game);
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("Game", gameGson);
        intent.putExtra("CurrentPlayer", Integer.toString(currentPlayer));
        startActivity(intent);
    }

    public void diceClicked(View view2) {
        final DieRoller dieRoller = new DieRoller();
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        View view = getLayoutInflater().inflate(R.layout.activity_roll_dice, null);

        final TextView toBeRolled = (TextView) view.findViewById(R.id.diceBeingRolled);
        final TextView total = (TextView) view.findViewById(R.id.sumOfDice);

        Button zero = (Button) view.findViewById(R.id.zero);
        Button one = (Button) view.findViewById(R.id.one);
        Button two = (Button) view.findViewById(R.id.two);
        Button three = (Button) view.findViewById(R.id.three);
        Button four = (Button) view.findViewById(R.id.four);
        Button five = (Button) view.findViewById(R.id.five);
        Button six = (Button) view.findViewById(R.id.six);
        Button seven = (Button) view.findViewById(R.id.seven);
        Button eight = (Button) view.findViewById(R.id.eight);
        Button nine = (Button) view.findViewById(R.id.nine);
        Button delete = (Button) view.findViewById(R.id.delete);
        Button plus = (Button) view.findViewById(R.id.roll_plus);
        Button roll = (Button) view.findViewById(R.id.roll);
        Button d4 = (Button) view.findViewById(R.id.d4);
        Button d6 = (Button) view.findViewById(R.id.d6);
        Button d8 = (Button) view.findViewById(R.id.d8);
        Button d10 = (Button) view.findViewById(R.id.d10);
        Button d20 = (Button) view.findViewById(R.id.d20);

        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "0");
            }
        });

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "1");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "1");
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "2");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "2");
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "3");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "3");
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "4");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "4");
            }
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "5");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "5");
            }
        });

        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "6");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "6");
            }
        });

        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "7");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "7");
            }
        });

        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "8");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "8");
            }
        });

        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "9");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "9");
            }
        });

        d4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }
                else {
                    int startIndex = 0;
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    int numDice;
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex, toBeRolled.getText().toString().length()));
                    }
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    toBeRolled.setText(toBeRolled.getText().toString() + "d4");
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new D4());
                    }
                    Log.i("e", "DIEROLLER SIZE: " + dieRoller.getDice().size());
                }
            }
        });

        d6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }
                else {
                    int startIndex = 0;
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    int numDice;
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex, toBeRolled.getText().toString().length()));
                    }
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    toBeRolled.setText(toBeRolled.getText().toString() + "d6");
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new D6());
                    }
                    Log.i("e", "DIEROLLER SIZE: " + dieRoller.getDice().size());
                }
            }
        });

        d8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }
                else {
                    int startIndex = 0;
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    int numDice;
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex, toBeRolled.getText().toString().length()));
                    }
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    toBeRolled.setText(toBeRolled.getText().toString() + "d8");
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new D8());
                    }
                    Log.i("e", "DIEROLLER SIZE: " + dieRoller.getDice().size());
                }
            }
        });

        d10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }
                else {
                    int startIndex = 0;
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    int numDice;
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex, toBeRolled.getText().toString().length()));
                    }
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    toBeRolled.setText(toBeRolled.getText().toString() + "d10");
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new D10());
                    }
                    Log.i("e", "DIEROLLER SIZE: " + dieRoller.getDice().size());
                }
            }
        });

        d20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }
                else {
                    int startIndex = 0;
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    int numDice;
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex, toBeRolled.getText().toString().length()));
                    }
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    toBeRolled.setText(toBeRolled.getText().toString() + "d20");
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new D20());
                    }
                    Log.i("e", "DIEROLLER SIZE: " + dieRoller.getDice().size());
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            && toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {

                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -2));
                        for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                            if (dieRoller.getDice().get(i) instanceof D4) {
                                dieRoller.getDice().remove(i);
                                i = 0;
                                if (dieRoller.getDice().size() > 0) {
                                    if (dieRoller.getDice().get(0) instanceof D4 && dieRoller.getDice().size() == 1) {
                                        dieRoller.getDice().clear();
                                    }
                                }
                            }
                        }
                        return;
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            && toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {

                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -2));
                        for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                            if (dieRoller.getDice().get(i) instanceof D6) {
                                dieRoller.getDice().remove(i);
                                i = 0;
                                if (dieRoller.getDice().size() > 0) {
                                    if (dieRoller.getDice().get(0) instanceof D6 && dieRoller.getDice().size() == 1) {
                                        dieRoller.getDice().clear();
                                    }
                                }
                            }
                        }
                        return;
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8'
                            && toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -2));
                            for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                                if (dieRoller.getDice().get(i) instanceof D8) {
                                    dieRoller.getDice().remove(i);
                                    i = 0;
                                    if (dieRoller.getDice().size() > 0) {
                                        if (dieRoller.getDice().get(0) instanceof D8 && dieRoller.getDice().size() == 1) {
                                            dieRoller.getDice().clear();
                                        }
                                    }
                                }
                            }
                            return;
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == '1') {
                                toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -3));
                                for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                                    if (dieRoller.getDice().get(i) instanceof D10) {
                                        dieRoller.getDice().remove(i);
                                        i = 0;
                                        if (dieRoller.getDice().size() > 0) {
                                            if (dieRoller.getDice().get(0) instanceof D10 && dieRoller.getDice().size() == 1) {
                                                dieRoller.getDice().clear();
                                            }
                                        }
                                    }
                                }
                                return;
                            }

                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == '2') {
                                toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -3));
                                for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                                    if (dieRoller.getDice().get(i) instanceof D20) {
                                        dieRoller.getDice().remove(i);
                                        i = 0;
                                        if (dieRoller.getDice().size() > 0) {
                                            if (dieRoller.getDice().get(0) instanceof D20 && dieRoller.getDice().size() == 1) {
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
                    if (toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -3));
                        return;
                    }
                }
                if (toBeRolled.getText().toString().length() > 0) {
                    toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -1));
                    return;
                }
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            toBeRolled.setText(toBeRolled.getText().toString() + " + ");
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            toBeRolled.setText(toBeRolled.getText().toString() + " + ");
                            return;
                        }
                    }
                }
            }
        });

        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().isEmpty()) {
                    return;
                }
                if (!dieRoller.getDice().isEmpty() || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) != ' ') {

                    if (toBeRolled.getText().toString().length() > 1) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                                || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                                || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                                dieRoller.rollAllDice();
                                total.setText(dieRoller.display());
                                dieRoller.getDice().clear();
                                toBeRolled.setText("");
                                return;
                            }
                        }
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                                && toBeRolled.getText().toString().length() > 2) {
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                                dieRoller.rollAllDice();
                                total.setText(dieRoller.display());
                                dieRoller.getDice().clear();
                                toBeRolled.setText("");
                                return;
                            }
                        }
                    }
                }
            }
        });

        builder.setView(view)
                .setPositiveButton(R.string.player_score_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user hit "OK" do nothing they are done
                    }
                }).setNegativeButton(R.string.player_score_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The user hit "CANCEL" do nothing they are done
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void cardsClicked(View view) {
        String gameInformation = gson.toJson(game);
        Log.d(TAG, "cardsClicked: Game String: " + gameInformation);
        Intent intent = new Intent(GameActivity.this, CardGameActivity.class);
        intent.putExtra(this.EXTRA_MESSAGE, gameInformation);
        startActivity(intent);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            //right or left swipe
            if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                if (diffX > 0) {
                    previousPlayerView();
                    return true;
                }
                else {
                    nextPlayerView();
                    return true;
                }
            }
        }

        else {
            //up or down swipe
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * Custom Adapter class used to show A player's List of Resources in a ListView.
     */
    class CustomAdapter extends BaseAdapter {
        Context context;
        private static final String TAG = "CustomAdapter";
        CustomAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            Log.d("Make Resource List View", "getCount: " + game.getPlayers().get(currentPlayer).getResources().size());
            return game.getPlayers().get(currentPlayer).getResources().size();
        }

        @Override
        public Object getItem(int position) {
            return game.getPlayers().get(currentPlayer).getResources().get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * As the adapter iterates through the List of Resources that belong to Current Player,
         * Create a display for that item. This creates a button that shows the value of that
         * Resource and a TextView that shows the name of the resource.
         * @param position - The current location in the Resource List.
         * @param convertView - The View to be changed before returning.
         * @param parent - The containing ViewGroup.
         * @return - The convertView after the appropriate changes have been made.
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Log.v(TAG, "getView: Position:" + position);

            convertView = getLayoutInflater().inflate(R.layout.custom_resource_layout, null);

            Log.v(TAG, "getView: Post convert = inflate");

            //Get references to both of the new Views.
            TextView textView = (TextView) convertView.findViewById(R.id.resourceNameTextView);
            Button button = (Button) convertView.findViewById(R.id.resourceButton);

            // We want to know if they are somehow null.
            assert button != null;
            assert textView != null;

            List <Resource> currentPlayerResources = game.getPlayers().get(currentPlayer).getResources();

            //Initialize the Values to be displayed.
            textView.setText(currentPlayerResources.get(position).getName());
            int amount = currentPlayerResources.get(position).getAmount();
            button.setText(" " + amount);

            //Assign each View a new ID so that we don't call the wrong View when all of the custom
            //Buttons share an ID.
            final int newButtonId = Button.generateViewId();
            final int newTextViewId = Button.generateViewId();
            textView.setId(newTextViewId);
            button.setId(newButtonId);

            Log.v(TAG ,"getView: Post Set Defaults");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editPlayerScore((Button) GameActivity.this.findViewById(newButtonId), position);
                }
            });

            Log.v(TAG, "getView: Post set on click");
            return convertView;
        }
    }
}
