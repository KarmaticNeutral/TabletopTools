package com.example.Table_Top_Gaming;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AuthenticationActivity extends AppCompatActivity {


    private static final String TAG = "EmailPassword";
    private static final int RC_SIGN_IN = 123;

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignin;

    private ProgressDialog progressDialog;


    //private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        //mAuth = FirebaseAuth.getInstance();

        /*
        if (mAuth.getCurrentUser() != null){
            //start profile activity
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        */

        progressDialog = new ProgressDialog(this);

        //buttonRegister = (Button) findViewById(R.id.buttonRegister);

        //editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        //editTextPassword = (EditText) findViewById(R.id.editTextPass);

        //textViewSignin = (TextView) findViewById(R.id.textViewSignIn);

        //buttonRegister.setOnClickListener(this);
        //textViewSignin.setOnClickListener(this);
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please Enter an Email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this, "Please Enter a Password", Toast.LENGTH_LONG).show();
            return;
        }

        //Email and password are que bueno
        //we will show a progress bar

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        /*
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    //user successfully registers
                    //we will start the profile activity
                    Toast.makeText(AuthenticationActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                    //start profile activity
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                else {
                    Toast.makeText(AuthenticationActivity.this, "Failed to Registered", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //dismiss the bar when user is done registering
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View v){
        if(v == buttonRegister) {
            registerUser();
        }

        if(v == textViewSignin) {
            //will open login activity
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        */
    }


}
