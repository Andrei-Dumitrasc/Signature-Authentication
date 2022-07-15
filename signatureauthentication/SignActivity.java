package com.andrei.signatureauthentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class SignActivity extends AppCompatActivity {
    private int idSignature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        idSignature = getIntent().getIntExtra(RegisterSignatureActivity.EXTRA_MESSAGE, 0);
        if (idSignature == 0)
            idSignature = getIntent().getIntExtra(LoginActivity.EXTRA_MESSAGE, 0);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.draw_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_draw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CanvasView cv = (CanvasView) findViewById(R.id.sign_canvas);

        switch (item.getItemId()) {
            case R.id.action_confirm_signature:
                ArrayList<Float> x = cv.getSignatureX();
                if (x.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please draw your signature",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                ArrayList<Float> y = cv.getSignatureY();
                ArrayList<Integer> s = cv.getSignaturePenUps();
                Signature sign = new Signature(idSignature, x, y, s);
                ManagerIO.saveSignature(sign, getFilesDir());

                setResult(RESULT_OK);
                finish();
                return true;

            case R.id.action_clear_canvas:
                cv.clearCanvas();
                return true;

            case R.id.action_back:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
