package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoadGameActivity extends AppCompatActivity {

    // Create a KEY for passing information to the next activity
    public static final String EXTRA_MESSAGE = "com.example.load.MESSAGE";

    private ArrayList <String> CLOUD_GAME_NAMES;
    private ArrayList <String> LOCAL_GAME_NAMES;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    public LoadGameActivity() {
        CLOUD_GAME_NAMES = new ArrayList<>();
        LOCAL_GAME_NAMES = new ArrayList<>();
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
                            Log.d("PIE", "Inside IF Name&Save; List Length: " + CLOUD_GAME_NAMES.size());
                            CLOUD_GAME_NAMES.add(currentDocumentSnapshot.get("Name").toString());
                        }
                    }
                    CustomFirebaseAdapter customAdapterCloud = new CustomFirebaseAdapter();
                    ListView cloudGamesListView = (ListView) findViewById(R.id.cloudSavedGamesListView);
                    cloudGamesListView.setAdapter(customAdapterCloud);

                    CustomLocalAdapter customAdapterLocal = new CustomLocalAdapter();
                    ListView localGamesListView = (ListView) findViewById(R.id.localSavedGamesListView);
                    localGamesListView.setAdapter(customAdapterLocal);
                }
            });

            //TODO add the name of all games put into shared prefs or files to LOCAL_GAME_NAMES
        }
        else
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        Log.d("PIE", "After onSuccess in the code");
    }

    class CustomLocalAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return LOCAL_GAME_NAMES.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.custom_load_layout, null);

            TextView textView = (TextView) convertView.findViewById(R.id.textViewName);
            Button button = (Button) convertView.findViewById(R.id.selectionButton);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO recover game file from shared prefs or file here and convert it to the string below.

                    Intent intent = new Intent(LoadGameActivity.this, GameActivity.class);
                    intent.putExtra("Game", Objects.requireNonNull(/*TODO:A game message string*/""));
                    startActivity(intent);
                }
            });

            if (LOCAL_GAME_NAMES.size() > 0) {
                textView.setText(LOCAL_GAME_NAMES.get(position));
                return convertView;
            }

            return null;
        }
    }

    class CustomFirebaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return CLOUD_GAME_NAMES.size();
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
                                    if (Objects.requireNonNull(currentDocumentSnapshot.get("Name")).toString().equals(LoadGameActivity.this.CLOUD_GAME_NAMES.get(position))) {
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

            if (CLOUD_GAME_NAMES.size() > 0) {
                textView.setText(CLOUD_GAME_NAMES.get(position));
                return convertView;
            }
            return null;
        }
    }
}