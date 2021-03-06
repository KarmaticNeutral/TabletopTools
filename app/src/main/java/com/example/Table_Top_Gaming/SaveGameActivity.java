package com.example.Table_Top_Gaming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.example.Table_Top_Gaming.GridViewActivity.EXTRA_MESSAGE_GRID;

/**
 * This class represents the SaveGameActivity window and provides the different functions and buttons
 * for this activity
 */
public class SaveGameActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_MESSAGE = "com.example.load.MESSAGE";
    public static final String EXTRA_MESSAGE_SAVE = "com.example.Table_Top_Gaming.MESSAGE2";
    public static final String TAG = "hey";
    private String message;
    private FirebaseUser user;
    private Button buttonLocalSave;
    private Button buttonCloudSave;
    private Gson gson = new Gson();
    private Game game;


    /**
     * Create the SaveGameActivity window and set the default values
     *
     * @param savedInstanceState Just the beginning state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_game);
        setTheme(R.style.DialogTheme);

        // Get the intent
        Intent intent = getIntent();

        // Store the game information in message
        message = Objects.requireNonNull(intent.getExtras()).getString("Game");
        if (message == null) {
            message = intent.getExtras().getString(EXTRA_MESSAGE_GRID);
        }
        if (message == null) {
            message = intent.getExtras().getString(CardGameActivity.EXTRA_MESSAGE_CARD);
        }
        //used to get the user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //local save button
        buttonLocalSave = findViewById(R.id.buttonLocalSave);
        buttonLocalSave.setOnClickListener(this);

        //cloud save button
        buttonCloudSave = findViewById(R.id.saveGameButton);
        buttonCloudSave.setOnClickListener(this);

        game = gson.fromJson(message, Game.class);

        initBotNav();
    }

    /**
     * This function creates a save file and writes the game information to it
     */
    public void createCloudSave() {

        //get the instance of firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //saveGame is going to store the information that we want to put inside the cloud
        Map<String, Object> saveGame = new HashMap<>();
        TextView textView = findViewById(R.id.saveNameTextBox);
        String saveName = textView.getText().toString();

        saveGame.put("Name", saveName);
        saveGame.put("savedGame", message);
        saveGame.put("timeStamp", getCurrentTimeStamp());

        //store info in the the users specific cloud
        db.collection(user.getUid())
                .add(saveGame)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Log.i(TAG, "Document Saved Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        Toast.makeText(this, "Information Saved", Toast.LENGTH_LONG).show();

        // After the information is saved pass back the game information and restart the
        // GameActivity
        Intent intent2 = new Intent(this, GameActivity.class);
        intent2.putExtra(EXTRA_MESSAGE_SAVE, message);
        startActivity(intent2);
    }

    /**
     * Creates a local save for a game
     */
    @SuppressLint("ShowToast")
    public void createLocalSave() {
        //grabs the save game name and either creates or makes a new save game
        EditText editText = findViewById(R.id.saveNameTextBox);
        String name = editText.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.Table_Top_Gaming", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, message);
        editor.apply();
        Toast.makeText(this, "Information Saved", Toast.LENGTH_LONG).show();
    }

    /**
     * In essence the button hub, holds all the buttons functionality
     *
     * @param v the view
     */
    @Override
    public void onClick(View v) {
        if (v == buttonLocalSave) {
            createLocalSave();
        }
        if (v == buttonCloudSave) {
            if (user != null) {
                createCloudSave();
            } else {
                Toast.makeText(this, "ERROR: Login First", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Initialize the bottom navigation bar.
     */
    public void initBotNav() {
        final BottomNavigationView bottomNavigationView = findViewById(R.id.navigationMenu);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setSelectedItemId(R.id.navigation_save);

        View navDice = bottomNavigationView.findViewById(R.id.navigation_dice);
        View navGrid = bottomNavigationView.findViewById(R.id.navigation_grid);
        View navHome = bottomNavigationView.findViewById(R.id.navigation_home);
        View navCard = bottomNavigationView.findViewById(R.id.navigation_cards);
        View navSave = bottomNavigationView.findViewById(R.id.navigation_save);

        navDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diceClicked();
            }
        });

        navGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridClicked();
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
                // Do nothing we hare on the save game activity
            }
        });
    }

    /**
     * Move to the Home activity (GameActivity)
     */
    public void homeClicked() {
        assert game != null;
        String gameInformation = gson.toJson(game);
        Intent intent = new Intent(SaveGameActivity.this, GameActivity.class);
        intent.putExtra("Game", gameInformation);
        startActivity(intent);
    }

    /**
     * Move to the Card activity
     */
    public void cardClicked() {
        assert game != null;
        String gameInformation = gson.toJson(game);
        Intent intent = new Intent(SaveGameActivity.this, CardGameActivity.class);
        intent.putExtra(EXTRA_MESSAGE, gameInformation);
        startActivity(intent);
    }

    /**
     * Move to the Save activity
     */
    public void saveClicked() {

    }

    public void gridClicked() {
        String gameInformation = gson.toJson(game);
        Intent intent = new Intent(SaveGameActivity.this, GridViewActivity.class);
        intent.putExtra(EXTRA_MESSAGE, gameInformation);
        startActivity(intent);
    }

    /**
     * This function calls a dialog box that lets a user roll different kinds of dice and displays
     * the results on the screen
     */
    public void diceClicked() {
        DiceDialog diceDialog = new DiceDialog(SaveGameActivity.this);
    }

    public void returnToMainMenu(View view) {
        Intent intent = new Intent(SaveGameActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public static String getCurrentTimeStamp() {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}