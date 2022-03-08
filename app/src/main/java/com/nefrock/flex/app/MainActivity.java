package com.nefrock.flex.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickStartButton(View v) {
        Intent intent = new Intent(MainActivity.this, ReaderActivity.class);
        startActivity(intent);
    }
}