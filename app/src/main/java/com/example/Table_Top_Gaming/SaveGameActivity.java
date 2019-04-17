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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        Map <String, Object> saveGame = new HashMap<>();
        TextView textView = findViewById(R.id.saveNameTextBox);
        String saveName = textView.getText().toString();

        saveGame.put("Name", saveName);
        saveGame.put("savedGame", message);

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

        Toast.makeText(this,"Information Saved", Toast.LENGTH_LONG).show();

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
     * @param v the view
     */
    @Override
    public void onClick(View v) {
        if(v == buttonLocalSave)
        {
            createLocalSave();
        }
        if (v == buttonCloudSave)
        {
            if(user != null) {
                createCloudSave();
            }
            else {
                Toast.makeText(this,"ERROR: Login First", Toast.LENGTH_LONG).show();
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
        // Create a new dieRoller that will keep track of all the dice
        final DieRoller dieRoller = new DieRoller();

        // Create a new Alert Dialog and set the view to the dice rolling custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(SaveGameActivity.this);
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.activity_roll_dice, null);

        // Create variables for the different text fields on the dice rolling custom layout
        final TextView toBeRolled = view.findViewById(R.id.diceBeingRolled);
        final TextView total = view.findViewById(R.id.sumOfDice);
        total.setMovementMethod(new ScrollingMovementMethod());

        ArrayList<Button> buttons = new ArrayList<>();

        // Create buttons for all the different buttons on the dice rolling custom layout
        buttons.add((Button)view.findViewById(R.id.zero));
        buttons.add((Button)view.findViewById(R.id.one));
        buttons.add((Button)view.findViewById(R.id.two));
        buttons.add((Button)view.findViewById(R.id.three));
        buttons.add((Button)view.findViewById(R.id.four));
        buttons.add((Button)view.findViewById(R.id.five));
        buttons.add((Button)view.findViewById(R.id.six));
        buttons.add((Button)view.findViewById(R.id.seven));
        buttons.add((Button)view.findViewById(R.id.eight));
        buttons.add((Button)view.findViewById(R.id.nine));
        Button delete = view.findViewById(R.id.delete);
        Button plus = view.findViewById(R.id.roll_plus);
        Button roll = view.findViewById(R.id.roll);
        Button d4 = view.findViewById(R.id.d4);
        Button d6 = view.findViewById(R.id.d6);
        Button d8 = view.findViewById(R.id.d8);
        Button d10 = view.findViewById(R.id.d10);
        Button d20 = view.findViewById(R.id.d20);

        for (final Button currentButton : buttons) {
            currentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: number clicked");
                    // If the to be rolled string is blank or the last character is a space you should not be able to use the number buttons
                    if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                        if(currentButton.getText().equals("0")) {
                            Log.d(TAG, "      onClick: return on first");
                            return;
                        }
                    }

                    // Check if the last entered string was that of a die, if it was you should not be able to use the zero button
                    if (toBeRolled.getText().toString().length() > 1) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                                || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                                || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                                Log.d(TAG, "      onClick: return on second");
                                return;
                            }
                        }

                        // Check if the last entered string was that of a die with 10 or 20 sides, if it is you should not be able to use the zero button
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                                && toBeRolled.getText().toString().length() > 2) {
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                                Log.d(TAG, "      onClick: return on third");
                                return;
                            }
                        }
                    }

                    // Having cleared all the checks add the 0 to the string
                    toBeRolled.setText(String.format("%s%s", toBeRolled.getText().toString(), currentButton.getText().toString()));
                    Log.d(TAG, "onClick: number after toberolled.settext");
                }
            });
        }

        // The D4 button has been pressed check for foolish user input and a some D4 to the dieRoller
        d4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for a number before a die can be added, if there is no number return
                if (toBeRolled.getText().toString().isEmpty()) {
                    return;
                }

                // If the last button pressed was a another die or the plus button you should not be able to use this button yet
                if (toBeRolled.getText().toString().length() > 1) {

                    // Check if the last button pressed was the plus button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == ' ') {
                        return;
                    }

                    // Checks if the last button pressed was a die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // Check if the last button pressed was a die with 10 or 20 sides you should not be able to use this button yet
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // If the string is not empty and the last button pressed was not the plus button grab the last number entered
                if (!toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    int startIndex = 0;

                    // This loop grabs the last number entered if the plus button has been pressed
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    // Grab the correct number of dice
                    int numDice;

                    // If there is multiple dice being rolled grab just the last number of dice added to the string
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex));
                    }

                    // This would be the first amount of dice to be rolled, grab the amount of dice
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    // Add all the dice to the dieRoller, a list of dice.
                    toBeRolled.setText(String.format("%sd4", toBeRolled.getText().toString()));
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(4));
                    }
                }
            }
        });

        // The D6 button has been pressed check for foolish user input and a some D6 to the dieRoller
        d6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for a number before a die can be added, if there is no number return
                if (toBeRolled.getText().toString().isEmpty()) {
                    return;
                }

                // If the last button pressed was a another die or the plus button you should not be able to use this button yet
                if (toBeRolled.getText().toString().length() > 1) {

                    // Check if the last button pressed was the plus button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == ' ') {
                        return;
                    }

                    // Checks if the last button pressed was a die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // Check if the last button pressed was a die with 10 or 20 sides you should not be able to use this button yet
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // If the string is not empty and the last button pressed was not the plus button grab the last number entered
                if (!toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    int startIndex = 0;

                    // This loop grabs the last number entered if the plus button has been pressed
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    // Grab the correct number of dice
                    int numDice;

                    // If there is multiple dice being rolled grab just the last number of dice added to the string
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex));
                    }

                    // This would be the first amount of dice to be rolled, grab the amount of dice
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    // Add all the dice to the dieRoller, a list of dice.
                    toBeRolled.setText(String.format("%sd6", toBeRolled.getText().toString()));
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(6));
                    }
                }
            }
        });

        // The D8 button has been pressed check for foolish user input and a some D8 to the dieRoller
        d8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for a number before a die can be added, if there is no number return
                if (toBeRolled.getText().toString().isEmpty()) {
                    return;
                }

                // If the last button pressed was a another die or the plus button you should not be able to use this button yet
                if (toBeRolled.getText().toString().length() > 1) {

                    // Check if the last button pressed was the plus button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == ' ') {
                        return;
                    }

                    // Checks if the last button pressed was a die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // Check if the last button pressed was a die with 10 or 20 sides you should not be able to use this button yet
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // If the string is not empty and the last button pressed was not the plus button grab the last number entered
                if (!toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    int startIndex = 0;

                    // This loop grabs the last number entered if the plus button has been pressed
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    // Grab the correct number of dice
                    int numDice;

                    // If there is multiple dice being rolled grab just the last number of dice added to the string
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex));
                    }

                    // This would be the first amount of dice to be rolled, grab the amount of dice
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    // Add all the dice to the dieRoller, a list of dice.
                    toBeRolled.setText(String.format("%sd8", toBeRolled.getText().toString()));
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(8));
                    }
                }
            }
        });

        // The D10 button has been pressed check for foolish user input and a some D10 to the dieRoller
        d10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for a number before a die can be added, if there is no number return
                if (toBeRolled.getText().toString().isEmpty()) {
                    return;
                }

                // If the last button pressed was a another die or the plus button you should not be able to use this button yet
                if (toBeRolled.getText().toString().length() > 1) {

                    // Check if the last button pressed was the plus button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == ' ') {
                        return;
                    }

                    // Checks if the last button pressed was a die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // Check if the last button pressed was a die with 10 or 20 sides you should not be able to use this button yet
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // If the string is not empty and the last button pressed was not the plus button grab the last number entered
                if (!toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    int startIndex = 0;

                    // This loop grabs the last number entered if the plus button has been pressed
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    // Grab the correct number of dice
                    int numDice;

                    // If there is multiple dice being rolled grab just the last number of dice added to the string
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex));
                    }

                    // This would be the first amount of dice to be rolled, grab the amount of dice
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    // Add all the dice to the dieRoller, a list of dice.
                    toBeRolled.setText(String.format("%sd10", toBeRolled.getText().toString()));
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(10));
                    }
                }
            }
        });

        // The D20 button has been pressed check for foolish user input and a some D20 to the dieRoller
        d20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for a number before a die can be added, if there is no number return
                if (toBeRolled.getText().toString().isEmpty()) {
                    return;
                }

                // If the last button pressed was a another die or the plus button you should not be able to use this button yet
                if (toBeRolled.getText().toString().length() > 1) {

                    // Check if the last button pressed was the plus button
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == ' ') {
                        return;
                    }

                    // Checks if the last button pressed was a die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }

                    // Check if the last button pressed was a die with 10 or 20 sides you should not be able to use this button yet
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                // If the string is not empty and the last button pressed was not the plus button grab the last number entered
                if (!toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    int startIndex = 0;

                    // This loop grabs the last number entered if the plus button has been pressed
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    // Grab the correct number of dice
                    int numDice;

                    // If there is multiple dice being rolled grab just the last number of dice added to the string
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex));
                    }

                    // This would be the first amount of dice to be rolled, grab the amount of dice
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    // Add all the dice to the dieRoller, a list of dice.
                    toBeRolled.setText(String.format("%sd20", toBeRolled.getText().toString()));
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(20));
                    }
                }
            }
        });

        // The delete button has been pressed, delete the text on the screen and remove dice from the diceRoller if necessary
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check for more than one item in the string
                if (toBeRolled.getText().toString().length() > 1) {

                    // Check if the last entered item in the string was a d4 die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            && toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {

                        // Delete "d4" from the string and remove all the d4 die objects from the list of die
                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -2));
                        for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                            if (dieRoller.getDice().get(i).getNumSides() == 4) {
                                dieRoller.getDice().remove(i);
                                i = 0;
                                if (dieRoller.getDice().size() > 0) {
                                    // This if statement now compares the current die to a "d4".
                                    if (dieRoller.getDice().get(0).getNumSides() == 4 && dieRoller.getDice().size() == 1) {
                                        dieRoller.getDice().clear();
                                    }
                                }
                            }
                        }
                        return;
                    }

                    // Check if the last entered item in the string was a d6 die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            && toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {

                        // Delete "d6" from the string and remove all the d6 die objects from the list of die
                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -2));
                        for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                            if (dieRoller.getDice().get(i).getNumSides() == 6) {
                                dieRoller.getDice().remove(i);
                                i = 0;
                                if (dieRoller.getDice().size() > 0) {
                                    if (dieRoller.getDice().get(0).getNumSides() == 6 && dieRoller.getDice().size() == 1) {
                                        dieRoller.getDice().clear();
                                    }
                                }
                            }
                        }
                        return;
                    }

                    // Check if the last entered item in the string was a d8 die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8'
                            && toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {

                        // Delete "d8" from the string and remove all the d8 die objects from the list of die
                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -2));
                        for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                            if (dieRoller.getDice().get(i).getNumSides() == 8) {
                                dieRoller.getDice().remove(i);
                                i = 0;
                                if (dieRoller.getDice().size() > 0) {
                                    if (dieRoller.getDice().get(0).getNumSides() == 8 && dieRoller.getDice().size() == 1) {
                                        dieRoller.getDice().clear();
                                    }
                                }
                            }
                        }
                        return;
                    }

                    // Check if the last entered item in the string was a d10 die
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == '1') {

                                // Delete "d10" from the string and remove all the d10 die objects from the list of die
                                toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -3));
                                for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                                    if (dieRoller.getDice().get(i).getNumSides() == 10) {
                                        dieRoller.getDice().remove(i);
                                        i = 0;
                                        if (dieRoller.getDice().size() > 0) {
                                            if (dieRoller.getDice().get(0).getNumSides() == 10 && dieRoller.getDice().size() == 1) {
                                                dieRoller.getDice().clear();
                                            }
                                        }
                                    }
                                }
                                return;
                            }

                            // Check if the last entered item in the string was a d20 die
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == '2') {

                                // Delete "d20" from the string and remove all the d20 die objects from the list of die
                                toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -3));
                                for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                                    if (dieRoller.getDice().get(i).getNumSides() == 20) {
                                        dieRoller.getDice().remove(i);
                                        i = 0;
                                        if (dieRoller.getDice().size() > 0) {
                                            if (dieRoller.getDice().get(0).getNumSides() == 20 && dieRoller.getDice().size() == 1) {
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

                    // Check if the last button press was the plus button, if true delete 3 characters at the end of the string
                    if (toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -3));
                        return;
                    }
                }

                // If all the other checks have been cleared delete a character at a time in the string
                if (toBeRolled.getText().toString().length() > 0) {
                    toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -1));
                }
            }
        });

        // The plus button has been press just display a plus on the screen and check for foolish user input
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the last button pressed was a plus or the string is empty to nothing
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }

                // If the last button pressed was a d4, d6, or a d8 add " + " to the string
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            toBeRolled.setText(String.format("%s + ", toBeRolled.getText().toString()));
                            return;
                        }
                    }

                    // If the last button pressed was a d10 or d20 add " + " to the string
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            toBeRolled.setText(String.format("%s + ", toBeRolled.getText().toString()));
                        }
                    }
                }
            }
        });

        // The roll button has been pressed roll all the dice and display the results to the screen and check for foolish user input
        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the string is empty the plus button should not be able to be used
                if (toBeRolled.getText().toString().isEmpty()) {
                    return;
                }

                // Check if the string is not empty and that the last button pressed was not a plus button
                if (!dieRoller.getDice().isEmpty() || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) != ' ') {

                    // If the last button pressed was a die, roll the dice
                    if (toBeRolled.getText().toString().length() > 1) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                                || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                                || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                                dieRoller.rollAllDice();

                                // Display the total sum of the dice rolled and clear the to be rolled text
                                total.setText(dieRoller.display());
                                dieRoller.getDice().clear();
                                toBeRolled.setText("");
                                return;
                            }
                        }

                        // Check if the last button pressed was a d10 or 20 die if so roll the dice
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                                && toBeRolled.getText().toString().length() > 2) {
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                                dieRoller.rollAllDice();

                                // Display the total sum of the dice rolled and clear the to be rolled text
                                total.setText(dieRoller.display());
                                dieRoller.getDice().clear();
                                toBeRolled.setText("");
                            }
                        }
                    }
                }
            }
        });

        builder.setView(view)
                // Add a OK button to the dialog
                .setPositiveButton(R.string.player_score_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user hit "OK" do nothing they are done
                    }
                }).setNegativeButton(R.string.player_score_cancel, new DialogInterface.OnClickListener() {
            // Add a CANCEL button to the dialog
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The user hit "CANCEL" do nothing they are done
            }
        });

        // Create and show the dialog to the screen
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
