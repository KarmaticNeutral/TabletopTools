package com.example.Table_Top_Gaming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This is the main activity of the application that allows the players to keep track of their
 * scores and use a deck of cards
 */
public class GameActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private static final String TAG = "GameActivity";

    // Create a KEY for passing information to the next activity
    public static final String EXTRA_MESSAGE = "com.example.load.MESSAGE";

    private Game game;
    private Gson gson = new Gson();
    private int currentPlayer;
    private int numPlayers;
    private TextView playerNameHeader;
    private GameActivity.CustomAdapter customAdapter;
    private GestureDetectorCompat detector;
    private String newResourceName;
    private int defaultResourceValue;
    private ImageButton profilePicture;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    /**
     * Initialize values that are needed when the game Activity starts.
     * @param savedInstanceState The saved Instance state sent in to initialize this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setTheme(R.style.DialogTheme);

        //firebase stuff
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        // Set player to player number 1
        currentPlayer = 0;
        newResourceName = "";
        defaultResourceValue = 0;

        // Get the intent for this Activity
        Intent intent = getIntent();

        // Grab the information from LoadGameActivity passed to this Activity and place it in the
        // string message
        String message = intent.getStringExtra(LoadGameActivity.EXTRA_MESSAGE);
        Log.d(TAG, "onCreate: Extra From Load" + message);
        // If the GameActivity was reached by way of SaveGameActivity or NewGameActivity message
        // will be null
        if (message == null) {

            // Check to see if there is information to grab from the SaveGameActivity
            message = intent.getStringExtra(SaveGameActivity.EXTRA_MESSAGE_SAVE);
            Log.d(TAG, "onCreate: Extra From Save: " + message);
            // Check to see if there is information to grab from the NewGameActivity
            if (message == null) {

                message = intent.getStringExtra(CardGameActivity.EXTRA_MESSAGE_CARD);
                Log.d(TAG, "onCreate: Extra From Card: " + message);
                if (message == null) {
                    message = intent.getStringExtra(GridViewActivity.EXTRA_MESSAGE_GRID);
                    Log.d(TAG, "onCreate: Extra From Grid: " + message);
                    if (message == null) {
                        //Check to see if there is information to grab from CardGameActivity
                        //message = intent.getStringExtra(CardGameActivity.EXTRA_MESSAGE);
                        if (intent.getExtras() != null) {
                            message = intent.getExtras().getString("Game");
                            if (intent.getExtras().getString("CurrentPlayer") != null) {
                                currentPlayer = Integer.parseInt(Objects.requireNonNull(intent.getExtras().getString("CurrentPlayer")));
                            }
                        }
                    }
                }
            }
        }

        detector = new GestureDetectorCompat(this,this);

        // Set player to player number 1
        currentPlayer = 0;
        Log.d(TAG, "onCreate: GameContentMessage: " + message);
        // Parse the information saved in the String "message" and place it in the Game object
        game = gson.fromJson(message, Game.class);

        numPlayers = game.getPlayers().size();

        //Create a custom adapter for this activity
        customAdapter = new CustomAdapter(this);
        // Predefine  the ListView
        ListView resourceListView = findViewById(R.id.resourceListView);
        //Set up the custom adapter for the discovered ListView.
        resourceListView.setAdapter(customAdapter);

        playerNameHeader = findViewById(R.id.playerNameHeader);

        // Sets the player profile picture to a default.
        profilePicture = findViewById(R.id.playerProfileImageButton);
        profilePicture.setImageResource(R.drawable.avatar);

        setPlayerView();

        initBotNav();
    }

    /**
     * Initialize the bottom navigation bar.
     */
    public void initBotNav() {
        final BottomNavigationView bottomNavigationView = findViewById(R.id.navigationMenu);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        View navDice = bottomNavigationView.findViewById(R.id.navigation_dice);
        View navGrid = bottomNavigationView.findViewById(R.id.navigation_grid);
        View navHome = bottomNavigationView.findViewById(R.id.navigation_home);
        View navCard = bottomNavigationView.findViewById(R.id.navigation_cards);
        View navSave = bottomNavigationView.findViewById(R.id.navigation_save);

        navDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diceClicked();
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
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
                //Set this to do nothing to clear out the old OnClick.
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
                saveClicked();
            }
        });
    }

    /**
     * This function Allows the editing of a players score when the score button is pressed.
     * @param callingButton - The button that called this function.
     * @param resourceIndex - The index in the resource list of the currentPlayer that is being modified.
     */
    public void editPlayerScore (final Button callingButton, final int resourceIndex) {
        // Define a new AlertDialog box that will be called from this activity
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

        // Using a custom Layout from the EditPlayerScoreDialogActivity so we have to get that view
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.activity_edit_player_score_dialog, null);

        // Get the text field where the user will input a change in score
        final EditText input = view.findViewById(R.id.difference_in_score);
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                input.getText().clear();
            }
        });

        // Get the text view that displays the players score on the new activity window
        final TextView difference = view.findViewById(R.id.player_score);

        // Set the score display on the new window equal to Player 1's score
        String resourceInfo = game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getName();
        resourceInfo += ": " + Integer.toString(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount());
        difference.setText(resourceInfo);

        // There is a plus and a minus button on this custom layout so we have to grab them
        // and define them in this activity
        Button plus = view.findViewById(R.id.plus_button);
        Button minus = view.findViewById(R.id.minus_button);

        // When the user clicks the plus button add the input to the player's score
        plus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                if (input.getText().toString().equals("")) {
                    return;
                }

                // Set this integer to the amount the user wants to change the score by
                int diff = Integer.parseInt(input.getText().toString());

                // Add this amount to the current player score
                game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).setAmount(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount() + diff);

                // Change the display on the new window to the new amount
                String resourceInfo = game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getName();
                resourceInfo += ": " + Integer.toString(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount());
                difference.setText(resourceInfo);

                // Change the display on the GameActivity window score button to the new amount
                callingButton.setText(String.format(" %d", game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount()));
            }
        });

        // When the user clicks the minus button subtract the input from the player's score
        minus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                if (input.getText().toString().equals("")) {
                    return;
                }

                // Set this integer to the amount the user wants to change the score by
                int diff = Integer.parseInt(input.getText().toString());

                // Subtract this amount from the current player score
                game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).setAmount(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount() - diff);

                // Change the display on the new window to the new amount
                String resourceInfo = game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getName();
                resourceInfo += ": " + Integer.toString(game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount());
                difference.setText(resourceInfo);

                // Change the display on the GameActivity window score button to the new amount
                callingButton.setText(String.format(" %d", game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).getAmount()));
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

                        if (input.getText().toString().equals("")) {
                            return;
                        }
                        // Change the score to the number the user input instead of adding or subtracting it
                        game.getPlayers().get(currentPlayer).getResources().get(resourceIndex).setAmount(Integer.parseInt(input.getText().toString()));
                        setPlayerView();
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

    /**
     * Update the display to show that the currentPlayer has changed.
     */
    public void setPlayerView() {
        playerNameHeader.setText(game.getPlayers().get(currentPlayer).getName());

        String uriString = game.getPlayers().get(currentPlayer).getPathToImage();
        if (!uriString.equals("")) {
            profilePicture.setImageURI(Uri.parse(uriString));
            Log.d(TAG, "Profile picture set.\nUriString=" + uriString);
        }
        else {
           profilePicture.setImageResource(R.drawable.avatar);
        }

        customAdapter.notifyDataSetChanged();
    }

    /**
     * Shift the currentPlayer to be the next player in the List in the positive direction.
     */
    public void nextPlayerView() {
        if (currentPlayer < numPlayers) {
            currentPlayer++;
        }
        if (currentPlayer >= numPlayers) {
            currentPlayer = 0;
        }
        // Make the display reflect the change in the data structure.
        setPlayerView();
    }

    /**
     * Shift the currentPlayer to be the next player in the List in the negative direction.
     */
    public void previousPlayerView() {
        if (currentPlayer > 0) {
            currentPlayer--;
        }
        else {
            currentPlayer = numPlayers - 1;
        }
        // Make the display reflect the change in the data structure.
        setPlayerView();
    }

    /**
     * Add a blank resource to the resource List.
     * @param view2 - The View that called the function.
     */
    public void onClickAddResource(View view2) {


        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.activity_add_resource_dialog, null);

        final EditText resourceName = view.findViewById(R.id.enterResourceName);


        final EditText resourceValue = view.findViewById(R.id.enterResourceValue);

        resourceName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    resourceName.getText().clear();
                }
            }
        });

        resourceValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    resourceValue.getText().clear();
                }
            }
        });

        builder.setView(view)
                .setPositiveButton(R.string.add_resource_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user hit "OK" add the new resource with the name and amount they are done

                        if (!resourceName.getText().toString().isEmpty()) {
                            for (int i = 0; i < game.getPlayers().size(); i++) {
                                game.getPlayers().get(i).getResources().add(new Resource(newResourceName, defaultResourceValue));
                                game.getPlayers().get(i).getResources().get(game.getPlayers().get(i).getResources().size() - 1).setName(resourceName.getText().toString());

                                if (!resourceValue.getText().toString().isEmpty()) {
                                    game.getPlayers().get(i).getResources().get(game.getPlayers().get(i).getResources().size() - 1).setAmount(Integer.parseInt(resourceValue.getText().toString()));
                                }
                            }

                            // Make sure the Display reflects the change in the data structure.
                            customAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton(R.string.add_resource_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user hit "CANCEL" do nothing they are done
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Launches a dialog to allow user to select camera or gallery.
     * @param view The button that called the function.
     */
    public void cameraDialog(View view) {
        String gameGson = gson.toJson(game);
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("Game", gameGson);
        intent.putExtra("CurrentPlayer", Integer.toString(currentPlayer));
        startActivity(intent);
    }

    public void Logout(View view) {
        if (user != null) {
            firebaseAuth.signOut();
            //end the activity
            finish();
            //starts activity and logs them out back into the main activity
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    /**
     * This function calls a dialog box that lets a user roll different kinds of dice and displays
     * the results on the screen
     */
    public void diceClicked() {
        DiceDialog diceDialog = new DiceDialog(GameActivity.this);
    }

    /**
     * This function starts the card activity when the button is pressed
     */
    public void cardClicked() {
        String gameInformation = gson.toJson(game);
        Log.d(TAG, "cardsClicked: Game String: " + gameInformation);
        Intent intent = new Intent(GameActivity.this, CardGameActivity.class);
        intent.putExtra(EXTRA_MESSAGE, gameInformation);
        startActivity(intent);
    }

    public void gridClicked() {
        String gameInformation = gson.toJson(game);
        Log.d(TAG, "gridClicked: Game String: " + gameInformation);
        Intent intent = new Intent(GameActivity.this, GridViewActivity.class);
        intent.putExtra(EXTRA_MESSAGE, gameInformation);
        startActivity(intent);
    }

    /**
     * This function converts the information saved in the game object to a string and passes that
     * to the SaveGameActivity.
     */
    public void saveClicked() {
        String gameInformation = gson.toJson(game);
        Intent intent = new Intent(this, SaveGameActivity.class);
        intent.putExtra("Game", gameInformation);
        startActivity(intent);
    }

    /**
     * Overrides the onDown motion even when the user places their finger on the screen
     * @param e is the motion event detected
     * @return always return false because this is never to be used but must be overridden
     */
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    /**
     * Overrides the onShowPress motion event, to be honest, have no idea what this does but we are
     * not going to call this function but it must be overridden
     * @param e is the motion event detected
     */
    @Override
    public void onShowPress(MotionEvent e) {
    }

    /**
     * Overrides the onSingleTapUp motion event, to be honest, have no idea what this does but we are
     * not going to call this function but it must be overridden
     * @param e is the motion event detected
     * @return false because we don't use this function
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    /**
     * Overrides the onScroll motion event when the user scrolls up or down on the screen and calls
     * this function when that happens. We will not call or use this function though and it must be
     * overridden
     * @param e1 detects a down motion even
     * @param e2 detects an directional motion even
     * @param distanceX detects the distance that was input horizontally
     * @param distanceY detects the distance that was input vertically
     * @return false because we never use this function
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    /**
     * Overrides the onLongPress motion even that calls this function when the user holds their finger
     * on the screen. This function is never called though so it is empty
     * @param e is the motion even detected
     */
    @Override
    public void onLongPress(MotionEvent e) {
    }

    /**
     * Overrides the swipe motion even and detects if the user swiped left of right. If the user
     * swipes left display the next player, if the user swipes right display the previous player
     * @param downEvent detects when the user has placed their finger on the screen
     * @param moveEvent detects the distance swiped by the user
     * @param velocityX detects the speed the user swiped vertically
     * @param velocityY detects the speed the user swiped horizontally
     * @return true if a swipe right of left was detected and the function as played its roll, or
     * false if the user swiped up or down that should not do anything
     */
    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            //right or left swipe
            if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                if (diffX > 0) {
                    previousPlayerView();
                    return true;
                }
                else {
                    nextPlayerView();
                    return true;
                }
            }
        }

        else {
            return false;
            //up or down swipe
        }

        return false;
    }

    /**
     * If a motion even was detected notify the motion detector and call the proper function
     * @param event the motion event detected
     * @return boolean
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * Custom Adapter class used to show A player's List of Resources in a ListView.
     */
    class CustomAdapter extends BaseAdapter {
        Context context;
        private static final String TAG = "CustomAdapter";
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

        /**
         * As the adapter iterates through the List of Resources that belong to Current Player,
         * Create a display for that item. This creates a button that shows the value of that
         * Resource and a TextView that shows the name of the resource.
         * @param position - The current location in the Resource List.
         * @param convertView - The View to be changed before returning.
         * @param parent - The containing ViewGroup.
         * @return - The convertView after the appropriate changes have been made.
         */
        @SuppressLint({"InflateParams", "DefaultLocale", "ViewHolder"})
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Log.v(TAG, "getView: Position:" + position);

            convertView = getLayoutInflater().inflate(R.layout.custom_resource_layout, null);

            Log.v(TAG, "getView: Post convert = inflate");

            //Get references to both of the new Views.
            TextView textView = convertView.findViewById(R.id.resourceNameTextView);
            Button button = convertView.findViewById(R.id.resourceButton);

            // We want to know if they are somehow null.
            assert button != null;
            assert textView != null;

            List <Resource> currentPlayerResources = game.getPlayers().get(currentPlayer).getResources();

            //Initialize the Values to be displayed.
            textView.setText(currentPlayerResources.get(position).getName());
            int amount = currentPlayerResources.get(position).getAmount();
            button.setText(String.format("%d", amount));

            //Assign each View a new ID so that we don't call the wrong View when all of the custom
            //Buttons share an ID.
            final int newButtonId = Button.generateViewId();
            final int newTextViewId = Button.generateViewId();
            textView.setId(newTextViewId);
            button.setId(newButtonId);

            Log.v(TAG ,"getView: Post Set Defaults");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editPlayerScore((Button) GameActivity.this.findViewById(newButtonId), position);
                }
            });

            Log.v(TAG, "getView: Post set on click");
            return convertView;
        }
    }
}
