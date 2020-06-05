这是一个 android 方法hook的插件，在方法进入和方法退出时，将当前运行的所有参数回调到固定的接口中，利用这一点，可以进行方法切片式开发，也可以进行一些耗时统计等性能优化相关的统计。

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/miqingtang/maven/pluginSrc/images/download.svg)](https://bintray.com/miqingtang/maven/pluginSrc)

## 效果展示
代码：
```java
@HookMethod
public int add(int i, int i1) throws InterruptedException {
    int a = i + i1;
    Thread.sleep(a);
    return a;
}
```
日志打印：
```
2020-05-08 16:16:31.385 25969-26027/com.miqt.plugindemo W/MethodHookHandler:  
╔======================================================================================
║[Thread]:Thread-3
║[Class]:com.miqt.plugindemo.Hello
║[Method]:add
║[This]:com.miqt.plugindemo.Hello@c65e5c0
║[ArgsType]:[int, int]
║[ArgsValue]:[100,200]
║[Return]:300
║[ReturnType]:int
║[Time]:301 ms
╚======================================================================================
```

可以看出，这样的话方法名，运行线程，当前对象，入/出参数和耗时情况就都一目了然啦。

## 使用方法

项目根目录：build.gradle 添加以下代码

```groovy
dependencies {
    classpath 'me.miqt.plugin.tools:pluginSrc:0.2.1'
}
```

对应 module 中启用插件，可以是`application`也可以是`library`

```groovy
apply plugin: 'miqt.plugin.tools'

methodhook {
    enable = true //是否启用
    //项目中的class true：全部插桩 false：只有注解插桩
    all = true

    // 下面是非必要配置，无特殊需求可直接删除

    //指定插桩那些外部引用的jar，默认空，表示只对项目中的class插桩
    jarRegexs = [".*androidx.*"]
    //指定插桩那些类文件，默认空
    classRegexs = [".*view.*"]
    //所有包含 on 的方法,所有构造方法
    methodRegexs = [".*on.*", ".*init.*"]
    
    //编译时是否打印log
    log = true
    //是否用插桩后的jar包替换项目中的jar包，慎用
    replaceJar = false
    //是否生成详细mapping文件
    mapping = true
    //自定义方法统计实现类,不指定默认使用自带实现方式
    //impl = "com.miqt.plugindemo.MyTimeP"
}
```

添加类库依赖：

```groovy
dependencies {
    implementation 'me.miqt.plugin.tools:pluginlib:0.2.1'
}
```

这个插件是借鉴了很多大佬的代码，并结合自己的想法进行了一些调整，在此感谢他们付出的努力。

> https://github.com/novoda/bintray-release  
> https://github.com/JeasonWong/CostTime  
> https://github.com/MegatronKing/StringFog  

## 目前存在的已知问题

1. 在对jar进行方法hook的时候，如果这个jar经历过混淆，则插入代码后会因为jar2dex转换失败而编译不通过。
2. No such property: ASM6 for class: org.objectweb.asm.Opcodes 升级gradle版本可以解决。
