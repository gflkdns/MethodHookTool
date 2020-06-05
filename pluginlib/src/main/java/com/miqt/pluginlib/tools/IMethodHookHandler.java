package com.miqt.pluginlib.tools;

/**
 * @author miqt
 */
public interface IMethodHookHandler {

    void onMethodEnter(Object thisObj,
                       String className,
                       String methodName,
                       String argsType,
                       String returnType,
                       Object... args
    );


    void onMethodReturn(Object returnObj,
                        Object thisObj,
                        String className,
                        String methodName,
                        String argsType,
                        String returnType,
                        Object... args);
}
