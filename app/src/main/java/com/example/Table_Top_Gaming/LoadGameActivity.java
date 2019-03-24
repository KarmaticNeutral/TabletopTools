package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Map;
import java.util.Objects;

public class LoadGameActivity extends AppCompatActivity {

    // Create a KEY for passing information to the next activity
    public static final String EXTRA_MESSAGE = "com.example.load.MESSAGE";

    private ArrayList <String> GAME_NAMES;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    public LoadGameActivity() {
        GAME_NAMES = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        //used to get the user
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        FirebaseFirestore firebase = FirebaseFirestore.getInstance();
        if (user != null) {
            Task<QuerySnapshot> snapshotTask = firebase.collection(user.getUid()).get();
            Log.d("PIE", "Before onSuccess in the code");
            snapshotTask.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                    Log.d("PIE", "Inside Onsucess Outside List");
                    for (DocumentSnapshot currentDocumentSnapshot : documentSnapshots) {
                        String filePath = currentDocumentSnapshot.getReference().getPath();
                        Log.d("PIE", "onSuccess: Filepath of a Saved Game:" + filePath);

                        if (currentDocumentSnapshot.contains("Name") && currentDocumentSnapshot.contains("savedGame")) {
                            Log.d("PIE", "Inside IF Name&Save; List Length: " + GAME_NAMES.size());
                            GAME_NAMES.add(currentDocumentSnapshot.get("Name").toString());
                        }
                    }
                    CustomAdapter customAdapter = new CustomAdapter();
                    ListView savedGamesListView = (ListView) findViewById(R.id.savedGamesListView);
                    savedGamesListView.setAdapter(customAdapter);
                }
            });
        }
        else
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        Log.d("PIE", "After onSuccess in the code");
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return GAME_NAMES.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.custom_load_layout, null);

            TextView textView = (TextView) convertView.findViewById(R.id.textViewName);
            Button button = (Button) convertView.findViewById(R.id.selectionButton);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore firebase = FirebaseFirestore.getInstance();
                    Task<QuerySnapshot> snapshotTask = firebase.collection(user.getUid()).get();
                    Log.d("PIE", "Before onSuccess in the code");
                    snapshotTask.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot currentDocumentSnapshot : documentSnapshots) {
                                if(currentDocumentSnapshot.contains("Name")) {
                                    if (Objects.requireNonNull(currentDocumentSnapshot.get("Name")).toString().equals(LoadGameActivity.this.GAME_NAMES.get(position))) {
                                        Intent intent = new Intent(LoadGameActivity.this, GameActivity.class);
                                        intent.putExtra("Game", Objects.requireNonNull(currentDocumentSnapshot.get("savedGame")).toString());
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                    });


                }
            });

            if (GAME_NAMES.size() > 0) {
                textView.setText(GAME_NAMES.get(position));
                return convertView;
            }
            return null;
        }
    }
}