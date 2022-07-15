package com.andrei.signatureauthentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class RegisterNameActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.andrei.signatureauthentication.username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_name);
    }

    public void startLogin(View view) {
        finish();
    }

    public void proceedToSignatures(View view) {
        EditText t = (EditText) findViewById(R.id.register_username);
        String username = t.getText().toString().trim();
        // check validity
        if (TextUtils.isEmpty(username)){
            Toast.makeText(getApplicationContext(),
                    "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (new File(getFilesDir(), username + ".txt").exists()) {
            Toast.makeText(getApplicationContext(),
                    "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(this, RegisterSignatureActivity.class);
        i.putExtra(EXTRA_MESSAGE, username);
        startActivity(i);
    }
}
