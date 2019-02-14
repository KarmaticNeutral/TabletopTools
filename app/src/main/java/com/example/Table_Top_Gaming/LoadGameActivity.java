package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

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
        This function is called if the button "Load 1" is pressed and loads the "save.txt" file
    */
    public void loadSaveOne(View view) {
        fileName = "save.txt";
        loadGameFile(view);
    }

    /*
        This function is called if the button "Load 2" is pressed and loads the "save2.txt" file
    */
    public void loadSaveTwo(View view) {
        fileName = "save2.txt";
        loadGameFile(view);
    }

    /*
        This function is called if the button "Load 3" is pressed and loads the "save3.txt" file
    */
    public void loadSaveThree(View view) {
        fileName = "save3.txt";
        loadGameFile(view);
    }


    /*
        This function loads the desired Game File and reads in the information and then passes that
        information to the next Activity or window
    */
    public void loadGameFile(View view) {

        // Create a new intent for the next Activity and a string to store the data from the file
        Intent intent = new Intent(this, Game.class);
        String message = null;

        // Open the file and read in the information
        FileInputStream fis = null;
        try {
            fis = openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            // While there is still information in the file add it onto the string "message"
            while((message = br.readLine()) != null) {
                sb.append(message).append("\n");
            }

            message = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        // Close the file
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Display a toast to the screen showing the file path of the file being opened
        Toast.makeText(this, "Loading " + getFilesDir() + "/" + fileName,
                Toast.LENGTH_LONG).show();

        // Pass the gathered information to the next Activity or window and then open that Activity
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
