package com.miqt.pluginlib.tools;

import android.os.SystemClock;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SampleTimePrint implements ITimePrint {
    private static ThreadLocal<Map> local = new ThreadLocal<>();
    private static final String LINE = "======================================================================================";

    public static int d = 30, i = 100, w = 300, e = 500;

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
            if (time <= d) {
                return;
            }
            StringBuilder msgBuilder = new StringBuilder(16 * 10)
                    .append(" ")
                    .append("\n╔").append(LINE)
                    .append("\n║[Thread]:").append(Thread.currentThread().getName())
                    .append("\n║[Method]:").append(formatName(name))
                    .append("\n║[TimeCo]:").append(time).append(" ms")
                    .append("\n╚").append(LINE);
            String msg = msgBuilder.toString();


            if (time <= i) {
                Log.d("TimePrint", msg);
            } else if (time <= w) {
                Log.i("TimePrint", msg);
            } else if (time <= e) {
                Log.w("TimePrint", msg);
            } else {
                Log.e("TimePrint", msg);
            }
        }
    }

    private String formatName(String name) {
        if (name.length() <= LINE.length() - 9) {
            return name;
        } else {
            return name.substring(0, LINE.length() - 9) + "\n║         " + formatName(name.substring(LINE.length()));
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
