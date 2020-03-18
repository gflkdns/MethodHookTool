package com.miqt.plugindemo;

import android.util.Log;

import com.miqt.pluginlib.tools.ITimePrint;

public class MyTimeP implements ITimePrint {
    @Override
    public void onMethodEnter(String s) {
        Log.e("MyTimeP", s + "进入");
    }

    @Override
    public void onMethodReturn(String s) {
        Log.e("MyTimeP", s + "退出");
    }
}
