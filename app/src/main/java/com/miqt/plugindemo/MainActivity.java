package com.miqt.plugindemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.miqt.pluginlib.annotation.PrintTime;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @PrintTime
    public void onClick(View view) {
        String text = "你好！";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
