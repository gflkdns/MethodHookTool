package com.miqt.plugindemo;

import com.miqt.pluginlib.annotation.PrintTime;

public class Hello {
    @PrintTime
    public static String getStr() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }
}
