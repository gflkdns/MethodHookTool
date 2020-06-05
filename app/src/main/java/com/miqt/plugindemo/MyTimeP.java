package com.miqt.plugindemo;


import android.util.Log;

import com.miqt.pluginlib.tools.IMethodHookHandler;

public class MyTimeP implements IMethodHookHandler {

    @Override
    public void onMethodEnter(Object o, String s, String s1, String s2, String s3, Object... objects) {
        Log.e("onMethodEnter", s + "." + s1 + "()");
    }

    @Override
    public void onMethodReturn(Object o, Object o1, String s, String s1, String s2, String s3, Object... objects) {
        Log.e("onMethodReturn", s + "." + s1 + "()");
    }
}
