package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewGameActivity extends AppCompatActivity {

    private int numPlayers;
    private int index;
    private Game game;
    private List<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        players = new ArrayList<>();
        numPlayers = 0;
        index = 0;
    }

    public void setNumPlayers(View view) {
        EditText edit = (EditText) findViewById(R.id.editText3);
        numPlayers = Integer.parseInt(edit.getText().toString());
        index = 0;
        if (!players.isEmpty()) {
            players.clear();
            TextView textView = findViewById(R.id.textView3);
            String temp = "Player " + Integer.toString((index + 1));
            textView.setText(temp);
        }
        Toast.makeText(this, edit.getText().toString(), Toast.LENGTH_LONG).show();
    }

    public void clear() {
        EditText edit = (EditText) findViewById(R.id.editText2);
        edit.setText("");
    }

    public void input(View view) {

        EditText edit = (EditText) findViewById(R.id.editText2);
        TextView textView = findViewById(R.id.textView3);

        if (numPlayers == index) {
            Toast.makeText(this, "All player names have been entered. To change the " +
                    "player names renter the number of players", Toast.LENGTH_LONG).show();
        }

        if (edit.getText().toString().equals("")) {
            Toast.makeText(this, "Please Enter A Player Name", Toast.LENGTH_LONG).show();
            return;
        }

        if (numPlayers == 0) {
            Toast.makeText(this, "Please Enter The Number of Players", Toast.LENGTH_LONG).show();
            return;
        }

        if (index < numPlayers) {
            players.add(new Player(edit.getText().toString()));
            index++;
            clear();
            if (index < numPlayers) {
                String temp = "Player " + Integer.toString((index + 1));
                textView.setText(temp);
            }

            else {
                String temp = "All Names Have Been Entered";
                textView.setText(temp);
            }
        }

    }

    public void startNewGame(View view) {
        if (index != numPlayers || numPlayers == 0) {
            Toast.makeText(this, "Please Enter All Player Names", Toast.LENGTH_LONG).show();
            return;
        }

        game = new Game(players);
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
