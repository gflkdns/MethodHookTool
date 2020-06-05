package com.miqt.pluginlib.tools;

import android.os.SystemClock;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SampleTimePrint implements IMethodHookHandler {
    private static ThreadLocal<HashMap<String, Object>> local = new ThreadLocal<>();
    private static final String LINE = "======================================================================================";

    @Override
    public void onMethodEnter(Object o,
                              String className,
                              String methodName,
                              String argsType,
                              String returnType,
                              Object... args) {
        String name = className + methodName + returnType + argsType;
        HashMap<String, Object> map = local.get();
        if (map == null) {
            map = new HashMap<>(16);
            local.set(map);
        }
        InnerClass data = (InnerClass) map.get(name);
        if (data == null || data.time == null) {
            map.put(name, new SampleTimePrint.InnerClass(SystemClock.elapsedRealtime()));
            return;
        }

        data.integer.incrementAndGet();
    }

    @Override
    public void onMethodReturn(Object returnObj,
                               Object thisObj,
                               String className,
                               String methodName,
                               String argsType,
                               String returnType,
                               Object... args) {
        String name = className + methodName + returnType + argsType;
        Map map = local.get();
        InnerClass data = null;
        if (map != null) {
            data = (InnerClass) map.get(name);
        }
        if (data == null || data.time == null) {
            Log.d("MethodHookHandler", name + " <-- " + "not has data !");
            return;
        }

        if (data.integer.decrementAndGet() <= 0) {
            map.remove(name);
            if (map.size() == 0) {
                local.remove();
            }
            long time = SystemClock.elapsedRealtime() - data.time;
            int d = 30;
            if (time <= d) {
                return;
            }
            StringBuilder msgBuilder = new StringBuilder(16 * 10)
                    .append(" ")
                    .append("\n¨X").append(LINE)
                    .append("\n¨U[Thread]:").append(Thread.currentThread().getName())
                    .append("\n¨U[Class]:").append(className)
                    .append("\n¨U[Method]:").append(methodName)
                    .append("\n¨U[This]:").append(thisObj)
                    .append("\n¨U[ArgsType]:").append(argsType)
                    .append("\n¨U[ArgsValue]:").append(getArgsValue(args))
                    .append("\n¨U[Return]:").append(returnObj)
                    .append("\n¨U[ReturnType]:").append(returnType)
                    .append("\n¨U[Time]:").append(time).append(" ms")
                    .append("\n¨^").append(LINE);
            String msg = msgBuilder.toString();


            int i = 100;
            int w = 300;
            int e = 500;
            if (time <= i) {
                Log.d("MethodHookHandler", msg);
            } else if (time <= w) {
                Log.i("MethodHookHandler", msg);
            } else if (time <= e) {
                Log.w("MethodHookHandler", msg);
            } else {
                Log.e("MethodHookHandler", msg);
            }
        }
    }

    private String getArgsValue(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (int j = 0; j < args.length; j++) {
                builder.append(args[j])
                        .append(j == args.length - 1 ? ']' : ',');
            }
            return builder.toString();
        }
    }

    private String formatName(String name) {
        if (name.length() <= LINE.length() - 9) {
            return name;
        } else {
            return name.substring(0, LINE.length() - 9) + "\n¨U         " + formatName(name.substring(LINE.length() - 9));
        }
    }

    static class InnerClass {
        AtomicInteger integer;
        Long time;

        InnerClass(Long time) {
            this.integer = new AtomicInteger(1);
            this.time = time;
        }
    }
}
