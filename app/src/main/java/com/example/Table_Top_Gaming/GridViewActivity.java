package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

public class GridViewActivity extends AppCompatActivity {
    private static final String TAG = "GridViewActivity";
    public static final String EXTRA_MESSAGE_GRID = "com.example.load.GRIDMESSAGE";
    private Game game;
    private Gson gson = new Gson();
    private int numPlayers;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);

        // Get the intent for this Activity
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.d(TAG, "onCreate: Extras is empty!");
        } else {
            Log.d(TAG, "onCreate: Extras has content!");
        }

        String message = extras.getString(GameActivity.EXTRA_MESSAGE);
        game = gson.fromJson(message, Game.class);

        numPlayers = game.getPlayers().size();

        buildTable();
    }

    public void buildTable() {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.table_main);

        //Start at -1 so that You get a row showing player names first
        for (int i = -1; i < game.getPlayers().get(0).getResources().size(); i++) {
            TableRow row = new TableRow(this);
            //Start at -1 so that you get a column of resource names first
            for (int j = -1; j < game.getPlayers().size(); j++) {
                Log.d(TAG, "onCreate: I: " + i + ", J: " + j);
                if (i == -1) {
                    Log.d(TAG, "onCreate: Player Name Row.");
                    TextView textView = new TextView(this);
                    textView.setWidth(100);
                    textView.setHeight(50);
                    textView.setTextColor(Color.BLACK);
                    if (j == -1) {
                        textView.setText("");
                    } else {
                        textView.setText(game.getPlayers().get(j).getName());
                    }
                    row.addView(textView);
                } else if (j == -1) {
                    Log.d(TAG, "onCreate: Resource Name #" + i);
                    TextView textView = new TextView(this);
                    textView.setWidth(100);
                    textView.setHeight(50);
                    textView.setText(game.getPlayers().get(0).getResources().get(i).getName());
                    textView.setTextColor(Color.BLACK);
                    row.addView(textView);
                } else {
                    if (game.getPlayers().get(j).getResources() != null) {
                        Button button = new Button(this);
                        button.setWidth(100);
                        button.setHeight(50);
                        button.setText(Integer.toString(game.getPlayers().get(j).getResources().get(i).getAmount()));
                        row.addView(button);
                    }
                }
                if(j == game.getPlayers().size() - 1) {
                    tableLayout.addView(row, i);
                }
            }
        }
    }
}
