package com.miqt.pluginlib.tools;

/**
 * @author miqt
 */
public interface ITimePrint {
     /**
      * 方法进入
      * @param name 包含方法名，方法类名，方法返回值参数等的一系列信息
      */
     void onMethodEnter(String name);

     /**
      * 方法退出
      * @param name 包含方法名，方法类名，方法返回值参数等的一系列信息
      */
     void onMethodReturn(String name);
}
