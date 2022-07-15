package com.andrei.signatureauthentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;

public class RegisterSignatureActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.andrei.signatureauthentication.IDSign";
    static final int SAVE_SIGNATURE_REQUEST = 1;
    private int nrSignature;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_signature);
        nrSignature = 0;
        username=getIntent().getStringExtra(RegisterNameActivity.EXTRA_MESSAGE);
    }

    public void startLogin(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SAVE_SIGNATURE_REQUEST) {
            if (resultCode == RESULT_OK) {
                nrSignature += 1;
            }
        }
    }

    public void displayRegistrationStatus(View view) {
        boolean status = false;
        Registrar r = new Registrar(getFilesDir());
        HashSet<Integer> badSigns = r.attemptRegistration();
        if (badSigns.isEmpty()) {
            ManagerIO.saveUserData(r.generateUserData(),getFilesDir(),username);   
            Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(getApplicationContext(),
                    "The samples are not similar enough. Please redraw them more carefully.",
                    Toast.LENGTH_SHORT).show();
            nrSignature=0;
            TextView t = (TextView) findViewById(R.id.register_signatures_counter);
            t.setText(Integer.toString(nrSignature) + " samples given");
            Button b = (Button) findViewById(R.id.register_confirm_signatures);
            b.setEnabled(false);
            b = (Button) findViewById(R.id.register_start_drawing);
            b.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView t = (TextView) findViewById(R.id.register_signatures_counter);
        t.setText(Integer.toString(nrSignature) + " samples given");
        Button b = (Button) findViewById(R.id.register_confirm_signatures);
        if (nrSignature == 5) {
            b.setEnabled(true);
            b = (Button) findViewById(R.id.register_start_drawing);
            b.setEnabled(false);
        }
    }

    public void startSigning(View view) {
        Intent i = new Intent(this, SignActivity.class);
        i.putExtra(EXTRA_MESSAGE, nrSignature + 1);
        startActivityForResult(i, SAVE_SIGNATURE_REQUEST);
    }
}
