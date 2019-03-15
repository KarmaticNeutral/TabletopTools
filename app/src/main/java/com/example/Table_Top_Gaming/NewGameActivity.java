package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class NewGameActivity extends AppCompatActivity {

    private int numPlayers;
    private int index;
    private Game game;
    private List<Player> players;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        // Initialize all the variables
        players = new ArrayList<>();
        numPlayers = 0;
        index = 0;
    }

    public void setNumPlayers(View view) {
        // Grab the number of players entered by the user from the text field
        EditText edit = (EditText) findViewById(R.id.editText3);

        // This number is a string so it must be converted into an integer
        numPlayers = Integer.parseInt(edit.getText().toString());

        // In case the the number of players is reset, the index must be reset
        // so all the names can be stored
        index = 0;

        // If there are players in the list erase them
        if (!players.isEmpty()) {
            players.clear();

            // Reset the text field to show the player name that needs to be entered
            TextView textView = findViewById(R.id.textView3);
            String temp = "Player " + Integer.toString((index + 1));
            textView.setText(temp);
        }
        clear(edit);
    }

    /*
    This function clears the text field after a name is entered
     */
    public void clear(EditText edit) {
        edit.setText("");
    }

    public void input(View view) {

        EditText edit = (EditText) findViewById(R.id.editText2);
        TextView textView = findViewById(R.id.textView3);

        // Display a toast if all names have been entered
        if (numPlayers == index) {
            Toast.makeText(this, "All player names have been entered. To change the " +
                    "player names renter the number of players", Toast.LENGTH_LONG).show();
            return;
        }

        // Display this toast if the user tries to enter a blank player name
        if (edit.getText().toString().equals("")) {
            Toast.makeText(this, "Please Enter A Player Name", Toast.LENGTH_LONG).show();
            return;
        }

        // If the number of players is ZERO you won't to store any names
        if (numPlayers == 0) {
            Toast.makeText(this, "Please Enter The Number of Players", Toast.LENGTH_LONG).show();
            return;
        }

        // Check to make sure that player names still need to be entered
        if (index < numPlayers) {

            // Add a player to the list of players
            players.add(new Player(edit.getText().toString()));
            index++;

            // Clear the text field for the next player's name
            clear(edit);

            // If more player names still need to be stored change the text of the player name that
            // needs to be entered
            if (index < numPlayers) {
                String temp = "Player " + Integer.toString((index + 1));
                textView.setText(temp);
            }

            // If all the player names have been grabbed, display this message on the screen
            else {
                String temp = "All Names Have Been Entered";
                textView.setText(temp);
            }
        }

    }

    /*
    This function starts a new game when the button is pressed and all the required information has
    been stored
     */
    public void startNewGame(View view) {

        // Check to make sure the required information has been received from the user before starting
        // a new game
        if (index != numPlayers || numPlayers == 0) {
            Toast.makeText(this, "Please Enter All Player Names", Toast.LENGTH_LONG).show();
            return;
        }

        // Make a new Game object and give it the list of players
        game = new Game(players);

        // Convert the Game object to a string and pass it to the next activity
        String gameInformation = gson.toJson(game);
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Game", gameInformation);
        startActivity(intent);
    }
}
