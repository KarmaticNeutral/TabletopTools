package com.example.Table_Top_Gaming;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents the SaveGameActivity window and provides the different functions and buttons
 * for this activity
 */
public class SaveGameActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_MESSAGE_SAVE = "com.example.Table_Top_Gaming.MESSAGE2";
    public static final String TAG = "hey";
    private String message;
    private FirebaseUser user;
    private Button buttonLocalSave;
    private Button buttonCloudSave;
    private Button buttonBack;


    /**
     * Create the SaveGameActivity window and set the default values
     * @param savedInstanceState Just the beginning state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_game);

        // Get the intent
        Intent intent = getIntent();

        // Store the game information in message
        message = Objects.requireNonNull(intent.getExtras()).getString("Game");
        if (message == null) {
            message = intent.getExtras().getString(GridViewActivity.EXTRA_MESSAGE_GRID);
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

        //temp back button
        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(this);

        //cloud save button
        buttonCloudSave = findViewById(R.id.saveGameButton);
        buttonCloudSave.setOnClickListener(this);
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
    public void createLocalSave() {
        //grabs the save game name and either creates or makes a new save game
        EditText editText = findViewById(R.id.saveNameTextBox);
        String name = editText.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.Table_Top_Gaming", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, message);
        editor.apply();
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
        if (v == buttonBack)
        {
            //need this to go back to main page
            Intent intent = new Intent(SaveGameActivity.this, GameActivity.class);
            //game activity requires something to be passed in
            intent.putExtra(EXTRA_MESSAGE_SAVE, message);
            startActivity(intent);
        }
    }
}
