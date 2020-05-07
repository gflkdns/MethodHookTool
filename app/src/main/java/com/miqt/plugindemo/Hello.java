package com.miqt.plugindemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.miqt.pluginlib.annotation.HookMethod;

public class Hello extends BroadcastReceiver {
    public static String getStr() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }

    @HookMethod
    public static String getStr(String hello) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }

    @HookMethod
    public static String getStr(int hello) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }

    public int add(int i, int i1) throws InterruptedException {
        int a = i + i1;
        Thread.sleep(a);
        return a;
    }

    @HookMethod
    public String gegsfsa(int hello, boolean gfh, String fds) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }

    @HookMethod
    @Override
    public void onReceive(Context context, Intent intent) {

    }

    static class Inner {
        @HookMethod
        public static String getStr() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        }

        @HookMethod
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
