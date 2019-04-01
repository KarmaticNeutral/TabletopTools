package com.example.Table_Top_Gaming;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This Activity is for user loading a game either locally or from the cloud
 */
public class LoadGameActivity extends AppCompatActivity {

    // Create a KEY for passing information to the next activity
    public static final String EXTRA_MESSAGE = "com.example.load.MESSAGE";
    private static final String TAG = "LoadGameActivity";

    private ArrayList <String> CLOUD_GAME_NAMES;
    private ArrayList <String> LOCAL_GAME_NAMES;

    //firebase authentication variables
    private FirebaseAuth firebaseAuth; //instance of
    private FirebaseUser user; //user info

    //local saves
    private SharedPreferences sharedPreferences;


    /**
     * Initializes the Array List necessary to store and display data to the screen
     */
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

        //gets the local saves
        sharedPreferences = getSharedPreferences("com.example.Table_Top_Gaming", Context.MODE_PRIVATE);

        createCloud();

        createLocal();
        Log.d("PIE", "After onSuccess in the code");
    }

    /**
     * Creates a cloud save list that will be displayed in a list view on the screen
     */
    public void createCloud() {


        FirebaseFirestore firebase = FirebaseFirestore.getInstance();

        //do this only if the user is logged in
        if (user != null) {
            //get information that is stored under their username
            Task<QuerySnapshot> snapshotTask = firebase.collection(user.getUid()).get();
            Log.d("PIE", "Before onSuccess in the code");
            snapshotTask.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                    Log.d("PIE", "Inside Onsuccess Outside List");
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
                }
            });
        }
    }

    /**
     * Creates a local save list that will be displayed in a list view on the screen
     */
    public void createLocal() {
        CustomLocalAdapter customAdapterLocal = new CustomLocalAdapter();
        ListView localGamesListView = (ListView) findViewById(R.id.localSavedGamesListView);
        localGamesListView.setAdapter(customAdapterLocal);

        //gets all the names of saved games that the user has saved in shared prefs and stores it
        //in LOCAL_GAME_NAMES
        Map<String, ?> key = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry: key.entrySet()) {
            LOCAL_GAME_NAMES.add(entry.getKey());
            Log.v("map values",entry.getKey() + "Size: " + LOCAL_GAME_NAMES.size());
        }
    }

    class CustomLocalAdapter extends BaseAdapter {

        @Override
        public int getCount() { return LOCAL_GAME_NAMES.size(); }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * Gets the view of the Local Saves for the List View and sets button functionality
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.custom_load_layout, null);

            final TextView textView = (TextView) convertView.findViewById(R.id.textViewName);
            Button button = (Button) convertView.findViewById(R.id.selectionButton);
            textView.setText(LOCAL_GAME_NAMES.get(position));

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String gameInfo = sharedPreferences.getString(LOCAL_GAME_NAMES.get(position), "");

                    Intent intent = new Intent(LoadGameActivity.this, GameActivity.class);
                    intent.putExtra(LoadGameActivity.EXTRA_MESSAGE, gameInfo);
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

        /**
         * Gets the view for the Cloud Saves and sets button functionality
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
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