package com.miqt.pluginlib.tools;

import android.os.SystemClock;
import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Copyright 2019 analysys Inc. All rights reserved.
 * @Description: 方法耗时时间打印
 * @Version: 1.0
 * @Create: 2019-11-13 11:26:43
 * @author: miqt
 * @mail: miqingtang@analysys.com.cn
 */
public class TimePrint {
    private static HashMap<String, InnerClass> map = new HashMap<>();

    public static void start(String name) {
        InnerClass data = map.get(name);

        if (data == null || data.time == null) {
            map.put(name, new InnerClass(SystemClock.elapsedRealtime()));
            return;
        }

        data.integer.incrementAndGet();

    }

    public static void end(String name) {
        InnerClass data = map.get(name);
        if (data == null || data.time == null) {
            Log.d("TimePrint", name + " <-- " + "not has data !");
            return;
        }

        if (data.integer.decrementAndGet() <= 0) {
            map.remove(name);
            long time = SystemClock.elapsedRealtime() - data.time;
            if (time <= 30) {
                return;
            }

            if (time <= 100) {
                Log.d("TimePrint",
                        " \n╔==============================================================" +
                                "\n║[Thread]:" + Thread.currentThread().getName() +
                                "\n║[Method]:" + name +
                                "\n║[Time]:" + time
                                + "\n╚==============================================================");
            } else if (time <= 300) {
                Log.i("TimePrint",
                        " \n╔==============================================================" +
                                "\n║[Thread]:" + Thread.currentThread().getName() +
                                "\n║[Method]:" + name +
                                "\n║[Time]:" + time
                                + "\n╚==============================================================");
            } else if (time <= 500) {
                Log.w("TimePrint",
                        " \n╔==============================================================" +
                                "\n║[Thread]:" + Thread.currentThread().getName() +
                                "\n║[Method]:" + name +
                                "\n║[Time]:" + time
                                + "\n╚==============================================================");
            } else {
                Log.e("TimePrint",
                        " \n╔==============================================================" +
                                "\n║[Thread]:" + Thread.currentThread().getName() +
                                "\n║[Method]:" + name +
                                "\n║[Time]:" + time
                                + "\n╚==============================================================");
            }
        }
    }

    static class InnerClass {
        AtomicInteger integer;
        Long time;

        public InnerClass(Long time) {
            this.integer = new AtomicInteger(1);
            this.time = time;
        }
    }
}
