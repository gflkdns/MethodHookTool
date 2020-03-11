package com.analysys.plugin.config

class MethodTimerConfig {
    //是否启用
    boolean enable = true
    //是否将所有的方法都统计，否则只统计注解和正则设置的
    boolean all = false
    //是否打印日志
    boolean log = true
    //是否保存mapping
    boolean mapping = true
    //jar包 名称 正则表达式,不设置默认不插桩jar包
    List<String> jarRegexs = []
    //类名称 正则表达式
    List<String> classRegexs = []
    //方法名称 正则表达式
    List<String> methodRegexs = []
    //是否用插桩后的jar包替换项目中的jar包
    boolean replaceJar = false
}
