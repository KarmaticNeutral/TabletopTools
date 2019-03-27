package com.example.Table_Top_Gaming;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.Table_Top_Gaming.Game;
import com.example.Table_Top_Gaming.GameActivity;
import com.example.Table_Top_Gaming.Player;
import com.example.Table_Top_Gaming.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class NewGameActivity extends AppCompatActivity {
    private static final String TAG = "NewGameActivity";
    private Game game;
    private List<Player> players;
    private Gson gson = new Gson();
    private ListView listView;
    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        // Initialize all the variables
        players = new ArrayList<>();
        players.add(new Player("Player 1"));

        listView = findViewById(R.id.playerNameList);
        customAdapter = new CustomAdapter(this, listView, players);
        listView.setAdapter(customAdapter);
        //TODO: figure out why player 1 is not shown in the listView.
    }

    public void addPlayer(View view) {
        players.add(new Player("Player " + (players.size() + 1)));
        customAdapter.notifyDataSetChanged();
    }

    public void subtractPlayer(View view) {
        if (players.size() > 1) {
            players.remove(players.size() - 1);
            customAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "No players to remove!",
                    Toast.LENGTH_LONG).show();
        }
    }

    /*
    This function starts a new game when the button is pressed and all the required information has
    been stored
     */
    public void startNewGame(View view) {

        /*TODO find a way to cycle through the editText items to retreive names.
        This will probably require the getItem(int position) function in customAdapter.*/

        // Make a new Game object and give it the list of players
        game = new Game(players);

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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.custom_player_name_layout, null);
            final EditText editText = (EditText)convertView.findViewById(R.id.playerNameEditText);

            editText.setText(players.get(position).getName());

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus) {
                        players.get(position).setName(editText.getText().toString());
                    }
                }
            });
            return convertView;
        }
    }
}

