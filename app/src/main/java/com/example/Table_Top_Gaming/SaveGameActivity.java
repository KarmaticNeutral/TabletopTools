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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SaveGameActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_MESSAGE_SAVE = "com.example.Table_Top_Gaming.MESSAGE2";
    public static final String TAG = "hey";
    private String fileName;
    private String message;
    private Intent intent;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private Button buttonLocalSave;
    private Button buttonCloudSave;
    private Button buttonBack;
    private SharedPreferences sharedPreferences;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_game);

        // Get the intent
        intent = getIntent();

        // Store the game information in message
        message = intent.getExtras().getString("Game");

        //used to get the user
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //local save button
        buttonLocalSave = (Button) findViewById(R.id.buttonLocalSave);
        buttonLocalSave.setOnClickListener(this);

        //temp back button
        buttonBack = (Button) findViewById(R.id.buttonBackTemp);
        buttonBack.setOnClickListener(this);

        //cloud save button
        buttonCloudSave = (Button) findViewById(R.id.saveGameButton);

    }



    /**
     * This function creates a save file and writes the game information to it
    */

    public void createCloudSave() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map <String, Object> saveGame = new HashMap<>();
        TextView textView = findViewById(R.id.saveNameTextBox);
        String saveName = textView.getText().toString();

        saveGame.put("Name", saveName);
        saveGame.put("savedGame", message);
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

    public void createLocalSave() {
        EditText editText = (EditText) findViewById(R.id.saveNameTextBox);
        name = editText.getText().toString() + "|+|" + message;
        sharedPreferences = getSharedPreferences("com.example.Table_Top_Gaming", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedGame", name);
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        if(v == buttonLocalSave)
        {
            createLocalSave();
            String value = sharedPreferences.getString("savedGame", "");
            Toast.makeText(this, value, Toast.LENGTH_LONG).show();
        }
        if (v == buttonCloudSave)
        {
            createCloudSave();
        }
        if (v == buttonBack)
        {
            finish();
            startActivity(new Intent(this, GameActivity.class));
        }
    }
}
