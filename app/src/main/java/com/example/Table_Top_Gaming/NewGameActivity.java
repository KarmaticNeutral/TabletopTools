package com.example.Table_Top_Gaming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the NewGameActivity window and sets the buttons and functions for this window
 */
public class NewGameActivity extends AppCompatActivity {
    private static final String TAG = "NewGameActivity";
    private List<Player> players;
    private Gson gson = new Gson();
    private CustomAdapter customAdapter;

    /**
     * Create the NewGameActivity window and set the default values
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        // Initialize all the variables
        players = new ArrayList<>();
        players.add(new Player("Player 1"));

        ListView listView = findViewById(R.id.playerNameList);
        customAdapter = new CustomAdapter(this, listView, players);
        listView.setAdapter(customAdapter);
        //TODO: figure out why player 1 is not shown in the listView.
    }

    /**
     * Add a player to the game
     * @param view the button that calls this function
     */
    public void addPlayer(View view) {
        players.add(new Player("Player " + (players.size() + 1)));
        customAdapter.notifyDataSetChanged();
    }

    /**
     * Remove a player from the game
     * @param view the button that calls this funciton
     */
    public void subtractPlayer(View view) {
        if (players.size() > 1) {

            View current = getCurrentFocus();
            if (current != null) {
                current.clearFocus();
            }

            players.remove(players.size() - 1);
            customAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "No players to remove!",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
    This function starts a new game when the button is pressed and all the required information has
    been stored
     */
    public void startNewGame(View view) {

        /*TODO find a way to cycle through the editText items to retreive names.
        This will probably require the getItem(int position) function in customAdapter.*/

        if (getCurrentFocus() != null) {
            getCurrentFocus().clearFocus();
        }
        // Make a new Game object and give it the list of players
        Game game = new Game(players);

        // Convert the Game object to a string and pass it to the next activity
        String gameInformation = gson.toJson(game);
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Game", gameInformation);
        startActivity(intent);
    }

    class CustomAdapter extends BaseAdapter {
        Context context;
        ListView listView;
        List<Player> players;

        CustomAdapter(Context context, ListView listView, List <Player> players) {
            this.context = context;
            this.listView = listView;
            this.players = players;
        }

        @Override
        public int getCount() {
            return players.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint({"InflateParams", "ViewHolder"})
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "getView: Making view for player " + position + ". Their name is " + players.get(position).getName());
            convertView = getLayoutInflater().inflate(R.layout.custom_player_name_layout, null);
            final EditText editText = convertView.findViewById(R.id.playerNameEditText);

            editText.setText(players.get(position).getName());

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus) {
                        players.get(position).setName(editText.getText().toString());
                    }
                    if (hasFocus) {
                        editText.getText().clear();
                    }
                }
            });
            return convertView;
        }
    }
}

