package com.miqt.plugindemo;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.miqt.pluginlib.annotation.PrintTime;

public class Hello  extends BroadcastReceiver {
    @PrintTime
    public static String getStr() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }

    @PrintTime
    public static String getStr(String hello) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }
    @PrintTime
    public static String getStr(int hello) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }
    @PrintTime
    public  String gegsfsa(int hello,boolean gfh,String fds) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }

    @PrintTime
    @Override
    public void onReceive(Context context, Intent intent) {

    }

    static  class Inner {
        @PrintTime
        public static String getStr() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        }

        @PrintTime
        public static String getStr(String hello) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        }

        public void onClick(View view) {
            new Thread() {
                @Override
                public void run() {

                }
            }.start();
        }
    }
}
