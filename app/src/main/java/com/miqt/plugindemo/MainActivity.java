package com.miqt.plugindemo;

import android.os.Bundle;
import android.view.View;

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
        new Thread() {
            @Override
            public void run() {
                mmm();
            }
        }.start();
    }

    @PrintTime
    private void mmm() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String text = "你好！";
    }
}
