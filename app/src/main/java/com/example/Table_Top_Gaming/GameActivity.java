package com.example.Table_Top_Gaming;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class GameActivity extends AppCompatActivity {
    private Game game;
    private Gson gson = new Gson();
    private Button scoreButton;
    private int player;
    private int size;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

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

        // Set player to player number 1
        player = 0;

        // Parse the information saved in the String "message" and place it in the Game object
        game = gson.fromJson(message, Game.class);

        size = game.getPlayers().size();

        textView = (TextView) findViewById(R.id.textView2);

        // Testing Player Score button
        scoreButton = findViewById(R.id.player_score_button);
        // Set the Text on the button equal to the first players score
        setPlayerView();

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

    public void drawCard(View view) {

        if (!game.getDeck().getDeck().isEmpty()) {
            game.getPlayers().get(player).addCardToHand(game.getDeck().drawCard());
        }

        else {
            Toast.makeText(this, "Cannot draw a card. There are no more cards in the deck",
                    Toast.LENGTH_LONG).show();
        }
        String message = gson.toJson(game);

        Log.i("GameActivity", game.getPlayers().get(player).getName() + " has drawn a card");
    }

    public void drawHand(View view) {
        int numCards = 5;

        if (!game.getPlayers().get(player).canDraw()) {
            Toast.makeText(this, game.getPlayers().get(player).getName() +
                            " has already drawn a hand", Toast.LENGTH_LONG).show();
            return;
        }
        game.getPlayers().get(player).setHand(game.getDeck().drawHand(numCards));

        String message = gson.toJson(game);

        Log.i("GameActivity", game.getPlayers().get(player).getName() + " has drawn a hand");
    }

    /*
    This function Allows the editing of a players score when the score button is pressed
     */
    public void editPlayerScore (View view2) {
        // Define a new AlertDialog box that will be called from this activity
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

        // Using a custom Layout from the EditPlayerScoreDialogActivity so we have to get that view
        View view = getLayoutInflater().inflate(R.layout.activity_edit_player_score_dialog, null);

        // Get the text field where the user will input a change in score
        final EditText input = (EditText) view.findViewById(R.id.difference_in_score);

        // Get the text view that displays the players score on the new activity window
        final TextView difference = (TextView) view.findViewById(R.id.player_score);

        // Set the score display on the new window equal to Player 1's score
        String resourceInfo = game.getPlayers().get(player).getResources().get(0).getName();
        resourceInfo += ": " + Integer.toString(game.getPlayers().get(player).getResources().get(0).getAmount());
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
                game.getPlayers().get(player).getResources().get(0).setAmount(game.getPlayers().get(player).getResources().get(0).getAmount() + diff);

                // Change the display on the new window to the new amount
                String resourceInfo = game.getPlayers().get(player).getResources().get(0).getName();
                resourceInfo += ": " + Integer.toString(game.getPlayers().get(player).getResources().get(0).getAmount());
                difference.setText(resourceInfo);

                // Change the display on the GameActivity window score button to the new amount
                scoreButton.setText(Integer.toString(game.getPlayers().get(player).getResources().get(0).getAmount()));
            }
        });

        // When the user clicks the minus button subtract the input from the player's score
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set this integer to the amount the user wants to change the score by
                int diff = Integer.parseInt(input.getText().toString());

                // Subtract this amount from the current player score
                game.getPlayers().get(player).getResources().get(0).setAmount(game.getPlayers().get(player).getResources().get(0).getAmount() - diff);

                // Change the display on the new window to the new amount
                String resourceInfo = game.getPlayers().get(player).getResources().get(0).getName();
                resourceInfo += ": " + Integer.toString(game.getPlayers().get(player).getResources().get(0).getAmount());
                difference.setText(resourceInfo);

                // Change the display on the GameActivity window score button to the new amount
                scoreButton.setText(Integer.toString(game.getPlayers().get(player).getResources().get(0).getAmount()));
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
                        game.getPlayers().get(player).getResources().get(0).setAmount(Integer.parseInt(input.getText().toString()));
                        scoreButton.setText(Integer.toString(game.getPlayers().get(player).getResources().get(0).getAmount()));
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

    public void setPlayerView() {
        scoreButton.setText(Integer.toString(game.getPlayers().get(player).getResources().get(0).getAmount()));
        textView.setText(game.getPlayers().get(player).getName());
    }

    public void nextPlayerView(View view) {
        if (player < size) {
            player++;
        }
        if (player >= size) {
            player = 0;
        }

        setPlayerView();
    }

    public void previousPlayerView(View view) {
        if (player > 0) {
            player--;
            setPlayerView();
            return;
        }
        if (player <= 0) {
            player = size - 1;
            setPlayerView();
            return;
        }
    }
}
