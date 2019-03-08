package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SaveGameActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE_SAVE = "com.example.Table_Top_Gaming.MESSAGE2";
    public static final String TAG = "hey";
    private String fileName;
    private String message;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_game);

        // Get the intent
        intent = getIntent();

        // Store the game information in message
        message = intent.getExtras().getString("Game");

        // Create a TextView object and set the text field to the information stored in message
//        TextView textView = findViewById(R.id.saveNameTextBox);
//        textView.setText(message);

    }

    /*
        This function creates a save file and writes the game information to it
    */
    public void createSave(View view) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map <String, Object> saveGame = new HashMap<>();
        TextView textView = findViewById(R.id.saveNameTextBox);
        String saveName = textView.getText().toString();

        saveGame.put("Name", saveName);
        saveGame.put("savedGame", message);
        db.collection("savedGames")
                .add(saveGame)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


//        // Create the object that it can open files
//        FileOutputStream fos = null;
//
//        // Open "save.txt" and write the message in "temp" in it
//        // Next display a toast showing the file path and files saved to
//        try {
//            fos = openFileOutput(fileName, MODE_PRIVATE);
//            fos.write(message.getBytes());
//
//            // Display the file path the information is being saved to
//            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + fileName,
//                    Toast.LENGTH_LONG).show();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//
//            // Close the file
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        // After the information is saved pass back the game information and restart the
        // GameActivity
        Intent intent2 = new Intent(this, GameActivity.class);
        intent2.putExtra(EXTRA_MESSAGE_SAVE, message);
        startActivity(intent2);
    }
}
