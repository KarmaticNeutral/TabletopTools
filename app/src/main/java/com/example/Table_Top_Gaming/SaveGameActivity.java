package com.example.Table_Top_Gaming;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveGameActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE_SAVE = "com.example.Table_Top_Gaming.MESSAGE2";
    private String fileName;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_game);
    }

    public void saveOne(View view) {
        fileName = "save.txt";
        message = "This is save one";

        createSave();
    }

    public void saveTwo(View view) {
        fileName = "save2.txt";
        message = "This is save two";

        createSave();
    }

    public void saveThree(View view) {
        fileName = "save3.txt";
        message = "This is save three";

        createSave();
    }

    /*
        This function creates a save file and writes a small message to it when the
        Save button is pressed;
    */
    public void createSave() {

        // Create the object that it can open files
        FileOutputStream fos = null;

        // Open "save.txt" and write the message in "temp" in it
        // Next display a toast showing the file path and files saved to
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(message.getBytes());

            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + fileName, Toast.LENGTH_LONG).show();
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

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(EXTRA_MESSAGE_SAVE, message);
        startActivity(intent);
    }
}
