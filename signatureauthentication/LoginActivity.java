package com.andrei.signatureauthentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;


public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameView;
    public static final String EXTRA_MESSAGE = "com.andrei.signatureauthentication.IDSign";
    static final int SAVE_SIGNATURE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUsernameView = (EditText) findViewById(R.id.login_username);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SAVE_SIGNATURE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Button b = (Button) findViewById(R.id.sign_in_button);
                b.setEnabled(true);
            }
        }
    }
    public void startSigning(View view) {
        Intent i = new Intent(this, SignActivity.class);
        i.putExtra(EXTRA_MESSAGE, 6);
        startActivityForResult(i, SAVE_SIGNATURE_REQUEST);
    }

    public void attemptLogin(View view) {
        mUsernameView.setError(null);
        String userName = mUsernameView.getText().toString().trim();
        boolean isAuthentic;
        boolean cancel = false;
        String answer;
        View focusView = null;

        if (TextUtils.isEmpty(userName)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isValid(userName)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // focus the form field with an error
            focusView.requestFocus();
        } else {
            // authenticate
            Authenticator a = new Authenticator();
            isAuthentic = a.isAuthentic(getFilesDir(),
                    ManagerIO.loadUserData(getFilesDir(), userName));

            if (isAuthentic)
                answer = "Login successful";
            else
                answer = "Login failed";

            // Toast.makeText(getApplicationContext(), a.getScores() + " "
                    // + answer, Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), " "
                    + answer, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValid(String userName) {
        return new File(getFilesDir(), userName + ".txt").exists();
    }

    public void startRegister(View view) {
        Intent i = new Intent(this, RegisterNameActivity.class);
        startActivity(i);
    }

}

