package com.example.Table_Top_Gaming;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 3;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 4;
    ImageView imageView;
    private static final String TAG = "CameraActivity";
    private final int GALLERY_REQUEST_CODE = 1;
    private final int CAMERA_REQUEST_CODE = 2;
    private Gson gson = new Gson();
    private Game game;
    private String gameGson;
    private int currentPlayer;
    private String uriString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        imageView = (ImageView) findViewById(R.id.imageView);
        Intent intent = getIntent();
        gameGson = intent.getExtras().getString("Game");
        game = gson.fromJson(gameGson, Game.class);
        currentPlayer = Integer.parseInt(intent.getExtras().getString("CurrentPlayer"));

    }

    /**
     * Opens the default camera app and lets the user take a picture.
     * @param view Camera button
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void captureFromCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Make sure that the user has given camera permissions.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission denied");

            // If permission is denied, ask for permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
        else {
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Error creating file.");
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                }
            }
        }
    }

    /**
     * Opens the default gallery app and lets the user choose an image.
     * @param view Gallery button.
     */
    public void pickFromGallery(View view) {
        //Create an Intent with action as ACTION_OPEN_DOCUMENT
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        /*
        Make sure that the user has given storage permissions.
        Note that granting the WRITE_EXTERNAL_STORAGE permission automatically grants the
        READ_EXTERNAL_STORAGE permission.
        */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Write storage permission denied");

            // If permission is denied, ask for permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
        else {
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("image/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            // Launching the Intent
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }
    }

    private String cameraFilePath;
    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Log.i(TAG, "Time Stamp: " + timeStamp);

        String imageFileName = "jpeg_" + timeStamp + "_";
        Log.i(TAG, "imageFileName: " + imageFileName);

        //This is the directory in which the file will be created. This is the default location of Camera photos
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for using again
        cameraFilePath = "file://" + image.getAbsolutePath();
        Log.i(TAG, "Photo file path: " + cameraFilePath);
        return image;
    }

    /**
     * Sets the image view to the selected picture, and stores that picture as a URI
     * @param requestCode Checks if the user has selected an image.
     * @param resultCode Checks if the image was selected from camera or gallery
     * @param data The data which will store the image URI which will be returned to game activity.
     */
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    data.getData(); //returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    imageView.setImageURI(selectedImage);
                    uriString = selectedImage.toString();
                    game.getPlayers().get(currentPlayer).setPathToImage(uriString);
                    break;
                case CAMERA_REQUEST_CODE:
                    Uri image = Uri.parse(cameraFilePath);
                    imageView.setImageURI(Uri.parse(cameraFilePath));
                    uriString = image.toString();
                    game.getPlayers().get(currentPlayer).setPathToImage(uriString);
                    break;
            }
    }

    /**
     * Takes the user back to the game activity.
     * @param view The screen which contains a button that returns to the Game Activity.
     */
    public void returnToGame(View view) {
        String gameGson = gson.toJson(game);
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Game", gameGson);
        intent.putExtra("CurrentPlayer", Integer.toString(currentPlayer));
        startActivity(intent);
    }
}
