package com.miqt.pluginlib.tools;

public class TimePrint {
    private static final com.miqt.pluginlib.tools.ITimePrint M_PRINT = new com.miqt.pluginlib.tools.SampleTimePrint();

    public static void start(String name) {
        M_PRINT.onMethodEnter(name);
    }

    public static void end(String name) {
        M_PRINT.onMethodReturn(name);
    }
}