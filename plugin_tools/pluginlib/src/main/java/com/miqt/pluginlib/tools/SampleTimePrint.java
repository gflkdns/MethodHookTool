package com.miqt.pluginlib.tools;

import android.os.SystemClock;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SampleTimePrint implements ITimePrint {
    private static ThreadLocal<Map> local = new ThreadLocal<>();

    @Override
    public void onMethodEnter(String name) {
        if (local.get() == null) {
            local.set(new HashMap<>(16));
        }
        Map map = local.get();
        SampleTimePrint.InnerClass data = (SampleTimePrint.InnerClass) map.get(name);

        if (data == null || data.time == null) {
            map.put(name, new SampleTimePrint.InnerClass(SystemClock.elapsedRealtime()));
            return;
        }

        data.integer.incrementAndGet();
    }

    @Override
    public void onMethodReturn(String name) {
        Map map = local.get();
        SampleTimePrint.InnerClass data = (SampleTimePrint.InnerClass) map.get(name);
        if (data == null || data.time == null) {
            Log.d("TimePrint", name + " <-- " + "not has data !");
            return;
        }

        if (data.integer.decrementAndGet() <= 0) {
            map.remove(name);
            if (map.size() == 0) {
                local.remove();
            }
            long time = SystemClock.elapsedRealtime() - data.time;
            if (time <= 30) {
                return;
            }

            if (time <= 100) {
                Log.d("TimePrint",
                        " \n╔======================================================================================" +
                                "\n║[Thread]:" + Thread.currentThread().getName() +
                                "\n║[Method]:" + name +
                                "\n║[Time]:" + time
                                + "\n╚======================================================================================");
            } else if (time <= 300) {
                Log.i("TimePrint",
                        " \n╔======================================================================================" +
                                "\n║[Thread]:" + Thread.currentThread().getName() +
                                "\n║[Method]:" + name +
                                "\n║[Time]:" + time
                                + "\n╚======================================================================================");
            } else if (time <= 500) {
                Log.w("TimePrint",
                        " \n╔======================================================================================" +
                                "\n║[Thread]:" + Thread.currentThread().getName() +
                                "\n║[Method]:" + name +
                                "\n║[Time]:" + time
                                + "\n╚======================================================================================");
            } else {
                Log.e("TimePrint",
                        " \n╔======================================================================================" +
                                "\n║[Thread]:" + Thread.currentThread().getName() +
                                "\n║[Method]:" + name +
                                "\n║[Time]:" + time
                                + "\n╚======================================================================================");
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
