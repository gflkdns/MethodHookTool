package com.miqt.pluginlib.tools;

/**
 * @author miqt
 */
public interface ITimePrint {
    /**
     * 方法进入
     *
     * @param o          方法所在的对象，如果方法为static方法，则为null
     * @param className  方法所在的类名
     * @param methodName 方法的名称
     * @param returnType 方法的返回值类型
     * @param args       方法当前进入时传的参数
     */
    void onMethodEnter(Object o,
                       String className,
                       String methodName,
                       String argsType,
                       String returnType,
                       Object... args);

    /**
     * 方法退出
     *
     * @param o          方法所在的对象，如果方法为static方法，则为null
     * @param className  方法所在的类名
     * @param methodName 方法的名称
     * @param returnType 方法的返回值类型
     * @param args       方法当前进入时传的参数
     */
    void onMethodReturn(Object o,
                        String className,
                        String methodName,
                        String argsType,
                        String returnType,
                        Object... args);
}
