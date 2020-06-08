package com.miqt.pluginlib.tools;

import android.os.SystemClock;
import android.util.Log;

import com.miqt.pluginlib.annotation.IgnoreMethodHook;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
@IgnoreMethodHook
public class SampleTimePrint implements IMethodHookHandler {
    private static ThreadLocal<HashMap<String, Object>> local = new ThreadLocal<>();
    private static final String LINE = "══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════";

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
                    .append("\n╔").append(LINE)
                    .append("\n║ [Thread]:").append(Thread.currentThread().getName())
                    .append("\n║ [Class]:").append(className)
                    .append("\n║ [Method]:").append(methodName)
                    .append("\n║ [This]:").append(thisObj)
                    .append("\n║ [ArgsType]:").append(argsType)
                    .append("\n║ [ArgsValue]:").append(getArgsValue(args))
                    .append("\n║ [Return]:").append(returnObj)
                    .append("\n║ [ReturnType]:").append(returnType)
                    .append("\n║ [Time]:").append(time).append(" ms")
                    .append("\n╚").append(LINE);
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
            return name.substring(0, LINE.length() - 9) + "\n|         " + formatName(name.substring(LINE.length() - 9));
        }
    }

    @IgnoreMethodHook
    static class InnerClass {
        AtomicInteger integer;
        Long time;

        InnerClass(Long time) {
            this.integer = new AtomicInteger(1);
            this.time = time;
        }
    }
}
