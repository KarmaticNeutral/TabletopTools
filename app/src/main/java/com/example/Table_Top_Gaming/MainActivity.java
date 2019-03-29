package com.example.Table_Top_Gaming;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences prefs;

    private FirebaseAuth firebaseAuth;
    private Button buttonLogOut;
    //will be used to display the users email
    private TextView textViewUserEmail;
    //used to get the user information
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getPreferences(Context.MODE_PRIVATE);
        buttonLogOut = (Button) findViewById(R.id.buttonLogOut);
        buttonLogOut.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();

        textViewUserEmail = (TextView) findViewById(R.id.textView5);

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