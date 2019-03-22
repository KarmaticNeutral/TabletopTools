package com.example.Table_Top_Gaming;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import static android.view.View.generateViewId;

public class GameActivity extends AppCompatActivity {
    private Game game;
    private Gson gson = new Gson();
    private int currentPlayer;
    private int numPlayers;
    private TextView textView;
    private GameActivity.CustomAdapter customAdapter;

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
                if (intent.getExtras() != null) {
                    message = intent.getExtras().getString("Game");
                }
            }
        }


        // Set player to player number 1
        currentPlayer = 0;

        // Parse the information saved in the String "message" and place it in the Game object
        game = gson.fromJson(message, Game.class);

        numPlayers = game.getPlayers().size();

        customAdapter = new CustomAdapter(this);
        ListView resourceListView = (ListView) findViewById(R.id.resourceListView);
        resourceListView.setAdapter(customAdapter);

        textView = (TextView) findViewById(R.id.playerNameHeader);

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
            game.getPlayers().get(currentPlayer).addCardToHand(game.getDeck().drawCard());
        }

        else {
            Toast.makeText(this, "Cannot draw a card. There are no more cards in the deck",
                    Toast.LENGTH_LONG).show();
        }

        Log.i("GameActivity", game.getPlayers().get(currentPlayer).getName() + " has drawn a card");
    }

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

    /*
    This function Allows the editing of a players score when the score button is pressed
     */
    public void editPlayerScore (final Button buttonThatCalledThisFunction, final int resourceIndex) {
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
                buttonThatCalledThisFunction.setText(" " + game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount());
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
                buttonThatCalledThisFunction.setText(" " + game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount());
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
                        buttonThatCalledThisFunction.setText(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount());
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
        textView.setText(game.getPlayers().get(currentPlayer).getName());
        customAdapter.notifyDataSetChanged();
    }

    public void nextPlayerView(View view) {
        if (currentPlayer < numPlayers) {
            currentPlayer++;
        }
        if (currentPlayer >= numPlayers) {
            currentPlayer = 0;
        }

        setPlayerView();
    }

    public void previousPlayerView(View view) {
        if (currentPlayer > 0) {
            currentPlayer--;
            setPlayerView();
        }
        else {
            currentPlayer = numPlayers - 1;
            setPlayerView();
        }
    }

    public void onClickAddResource(View view) {
        List<Player> players = game.getPlayers();

        boolean allPlayers = true;
        String newResourceName = "newResource";
        int defaultValue = 0;

        Resource resourceToBeAdded = new Resource(newResourceName, defaultValue);

        if (allPlayers) {
            for (Player currentPlayerToGetResource : players) {
                currentPlayerToGetResource.getResources().add(resourceToBeAdded);
            }
        } else {
            game.getPlayers().get(currentPlayer).getResources().add(resourceToBeAdded);
        }

        customAdapter.notifyDataSetChanged();
    }

    class CustomAdapter extends BaseAdapter {
        Context context;

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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Log.d("Make Resource List View", "getView: Position:" + position);

            convertView = getLayoutInflater().inflate(R.layout.custom_resource_layout, null);

            Log.d("Make Resource List View", "getView: Post convert = inflate");

            TextView textView = (TextView) convertView.findViewById(R.id.resourceNameTextView);
            Button button = (Button) convertView.findViewById(R.id.resourceButton);

            if (button == null) {
                Log.wtf("WTF", "getView: the button doesn't exist and should");
            }
            if (textView == null) {
                Log.wtf("WTF", "getView: the textView doesn't exist and should");
            }

            List <Resource> currentPlayerResources = game.getPlayers().get(currentPlayer).getResources();

            textView.setText(currentPlayerResources.get(position).getName());
            int amount = currentPlayerResources.get(position).getAmount();
            button.setText(" " + amount);
            final int newButtonId = Button.generateViewId();
            final int newTextViewId = Button.generateViewId();
            textView.setId(newTextViewId);
            button.setId(newButtonId);

            Log.d("Make Resource List View", "getView: Post Set Texts");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editPlayerScore((Button) GameActivity.this.findViewById(newButtonId), position);
                }
            });

            Log.d("Make Resource List View", "getView: Post set on click");
            return convertView;
        }
    }
}
