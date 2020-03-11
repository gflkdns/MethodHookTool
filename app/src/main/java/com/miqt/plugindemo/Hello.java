package com.miqt.plugindemo;

public class Hello {
    public static String getStr() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }
}
