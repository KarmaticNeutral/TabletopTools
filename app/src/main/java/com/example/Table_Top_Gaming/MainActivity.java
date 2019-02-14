package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /*
        This function creates 3 save files and writes a small message to them when the
        New Game button is pressed;
    */
    public void createSave(View view) {
        // These strings are just to see if the desired information can be stored on the text files
        String temp  = "Testing Save File One.";
        String temp2 = "Testing Save File Two";
        String temp3 = "Testing Save File Three";

        // Create the object that it can open files
        FileOutputStream fos = null;

        // Open "save.txt" and write the message in "temp" in it
        // Next display a toast showing the file path and files saved to
        try {
            fos = openFileOutput("save.txt", MODE_PRIVATE);
            fos.write(temp.getBytes());

            Toast.makeText(this, "Saved to " + getFilesDir() + "/save.txt, save2.txt, and save3.txt", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        // Close the file
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Open "save2.txt" and write the message in "temp2" in it
        try {
            fos = openFileOutput("save2.txt", MODE_PRIVATE);
            fos.write(temp2.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        // Close the file
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Open "save3.txt" and write the message in "temp3" in it
        try {
            fos = openFileOutput("save3.txt", MODE_PRIVATE);
            fos.write(temp3.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        // Close the file
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /*
        Start a new Activity, or open the "Load a Game" window
    */
    public void loadGame(View view) {
        Intent intent = new Intent(this, LoadGameActivity.class);
        startActivity(intent);
    }
}
