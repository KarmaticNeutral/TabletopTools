package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Table_Top_Gaming.Game;
import com.example.Table_Top_Gaming.GameActivity;
import com.example.Table_Top_Gaming.Player;
import com.example.Table_Top_Gaming.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class NewGameActivity extends AppCompatActivity {

    private Game game;
    private List<Player> players;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        // Initialize all the variables
        players = new ArrayList<>();
        players.add(new Player("Player 1"));
    }

    public void addPlayer(View view) {
        if (players.size() > 0) {
            players.add(new Player("Player " + (players.size() + 1)));
        }
    }

    public void subtractPlayer(View view) {
        if (players.size() > 1) {
            players.remove(players.size() - 1);
        }
    }

    /*
    This function starts a new game when the button is pressed and all the required information has
    been stored
     */
    public void startNewGame(View view) {

        // Make a new Game object and give it the list of players
        game = new Game(players);

        // Convert the Game object to a string and pass it to the next activity
        String gameInformation = gson.toJson(game);
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Game", gameInformation);
        startActivity(intent);
    }
}
