package com.example.Table_Top_Gaming;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * This class creates a Dialog box that promps the user to enter a name for a new resource and
 * the starting amount for that resource.
 */
public class AddResourceDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource_dialog);
    }
}
