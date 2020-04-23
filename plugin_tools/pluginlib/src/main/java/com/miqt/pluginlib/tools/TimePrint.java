package com.miqt.pluginlib.tools;

/**
 * @author miqt
 */
public class TimePrint {
    /**
     * 如果设置了impl参数，则此对象实现将被编译期替换
     */
    private static final com.miqt.pluginlib.tools.ITimePrint M_PRINT = new com.miqt.pluginlib.tools.SampleTimePrint();

    public static void enter(Object o,
                             String className,
                             String methodName,
                             String argsType,
                             String returnType,
                             Object... args
    ) {
        M_PRINT.onMethodEnter(o, className, methodName, argsType, returnType, args);
    }

    public static void exit(Object o,
                           String className,
                           String methodName,
                           String argsType,
                           String returnType,
                           Object... args) {
        M_PRINT.onMethodReturn(o, className, methodName, argsType, returnType, args);
    }
}