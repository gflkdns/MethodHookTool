package com.miqt.pluginlib.tools;

/**
 * @author miqt
 */
public class TimePrint {
    /**
     * 如果设置了impl参数，则此对象实现将被编译期替换
     */
    private static final com.miqt.pluginlib.tools.ITimePrint M_PRINT = new com.miqt.pluginlib.tools.SampleTimePrint();

    public static void start(String name) {
        M_PRINT.onMethodEnter(name);
    }

    public static void end(String name) {
        M_PRINT.onMethodReturn(name);
    }
}