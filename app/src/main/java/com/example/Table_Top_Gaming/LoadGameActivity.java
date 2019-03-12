package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadGameActivity extends AppCompatActivity {

    // Create a KEY for passing information to the next activity
    public static final String EXTRA_MESSAGE = "com.example.load.MESSAGE";

    //Create a String with the file name
    private String fileName = "save.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);
    }

    /*
        This function loads the desired GameActivity File and reads in the information and then passes that
        information to the next Activity or window
    */
    public void loadGameFile(View view) {

        // Create a new intent for the next Activity and a string to store the data from the file
        Intent intent = new Intent(this, GameActivity.class);
        String message = null;

        FirebaseFirestore firebase = FirebaseFirestore.getInstance();
        ArrayList<HashMap<String, Object>> savedGamesList;
        Task<QuerySnapshot> snapshotTask = firebase.collection("savedGames").get();
        snapshotTask.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot currentDocumentSnapshot : documentSnapshots) {
                    String filePath = currentDocumentSnapshot.getReference().getPath();
                    Log.d("TAG", "onSuccess: Filepath of a Saved Game:" + filePath);
                }
            }
        });

        // Display a toast to the screen showing the file path of the file being opened
        Toast.makeText(this, "Loading " + getFilesDir() + "/" + fileName,
                Toast.LENGTH_LONG).show();

        // Pass the gathered information to the next Activity or window and then open that Activity
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);



        // Open the file and read in the information
//        FileInputStream fis = null;
//        try {
//            fis = openFileInput(fileName);
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader br = new BufferedReader(isr);
//            StringBuilder sb = new StringBuilder();
//
//            // While there is still information in the file add it onto the string "message"
//            while((message = br.readLine()) != null) {
//                sb.append(message).append("\n");
//            }
//
//            message = sb.toString();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//
//        // Close the file
//        } finally {
//            if (fis != null) {
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }
}
