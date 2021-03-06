package com.example.Table_Top_Gaming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This class represents the Main Activity window and sets the functions and buttons for that activity
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private Button buttonLogOut;
    //used to get the user information
    private FirebaseUser user;

    /**
     * Create the Main Activity and set the default values
     * @param savedInstanceState
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        buttonLogOut = findViewById(R.id.buttonLogOut);
        buttonLogOut.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();

        //will be used to display the users email
        TextView textViewUserEmail = findViewById(R.id.welcomeTextView);

        //if the user is logged in
        if (user != null) {
            //display welcome sign with the users email
            textViewUserEmail.setText("Welcome " + user.getEmail());
            //change the text for logging in
            buttonLogOut.setText("Sign Out");
        }
        else {
            //display the UI for a guest
            textViewUserEmail.setText("Welcome Guest");
            buttonLogOut.setText("Sign In");
        }
    }

    /**
        Start a new Activity, or open the "Load a GameActivity" window
    */
    public void loadGame(View view) {
        Intent intent = new Intent(this, LoadGameActivity.class);
        startActivity(intent);
    }

    /**
     * Is the intent for the button new game
     * @param view Gives the function the context
     */
    public void newGame(View view) {
        Intent intent = new Intent(this, NewGameActivity.class);
        startActivity(intent);
    }

    /**
     * The intent for the button take photo
     * @param view Gives the function the context
     */
    public void onTakePhoto(View view) {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }

    /**
     * Allows the user to sign in, holds the intent
     */
    public void signIn() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    /**
     * The Login and Logout button
     * @param v the view or context of the function
     */
    @Override
    public void onClick(View v) {
        //logs the user out if they are logged in and if the user is not logged in it logs them out
        if (v == buttonLogOut) {
            if (user != null) {
                firebaseAuth.signOut();
                //end the activity
                finish();
                //starts activity and logs them out back into the main activity
                startActivity(new Intent(this, MainActivity.class));
            }
            else {
                //directs the user it a sign in activity
                signIn();
            }
        }
    }

}