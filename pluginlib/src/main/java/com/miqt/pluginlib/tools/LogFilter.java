package com.miqt.pluginlib.tools;

public interface LogFilter {
    /**
     * 日志过滤器
     * @param thread
     * @param className
     * @param methodName
     * @param o
     * @param ages
     * @return false 允许打印，true 不允许打印
     */
    boolean onInvoke(Thread thread, String className, String methodName, Object o, Object[] ages);
}
