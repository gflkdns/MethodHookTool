
这个项目已经转移到：https://github.com/miqt/android-plugin/tree/master/plugin/hook-method 更新，请访问新地址。

这是一个 android 方法hook的插件，在方法进入和方法退出时，将当前运行的所有参数回调到固定的接口中，利用这一点，可以进行方法切片式开发，也可以进行一些耗时统计等性能优化相关的统计。

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/miqingtang/maven/pluginSrc/images/download.svg)](https://bintray.com/miqingtang/maven/pluginSrc)

## 效果展示
原始代码：
```java
@HookMethod
public int add(int num1, int num2) throws InterruptedException {
    int a = num1 + num2;
    Thread.sleep(a);
    return a;
}
```

实际编译插桩后代码：

```java
public int add(int num1, int num2) throws InterruptedException {
    MethodHookHandler.enter(this,"com.miqt.plugindemo.Hello","add","[int, int]","int",num1,num2);
    int a = num1 + num2;
    Thread.sleep(a);
    MethodHookHandler.exit(a,this,"com.miqt.plugindemo.Hello","add","[int, int]","int",num1,num2);
    return a;
}
```

稍作开发就可以实现一个方法出入日志打印功能：

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

可以看出，这样的话方法名，运行线程，当前对象，入/出参数和耗时情况就都一目了然啦。当然还可以做一些别的事情，例如hook点击事件等等。

## 使用方法

项目根目录：build.gradle 添加以下代码

```groovy
dependencies {
    classpath 'me.miqt.plugin.tools:pluginSrc:0.2.2'
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

    //    //指定插桩那些外部引用的jar，默认空，表示只对项目中的class插桩
    //    jarRegexs = [".*androidx.*"]
    //    //指定插桩那些类文件，默认空
    //    classRegexs = [".*view.*"]
    //    //所有包含 on 的方法,所有构造方法
    //    methodRegexs = [".*on.*", ".*init.*"]
    //
    //    //编译时是否打印log
    //    log = true
    //    //是否用插桩后的jar包替换项目中的jar包，慎用
    //    replaceJar = false
    //    //是否生成详细mapping文件
    //    mapping = true
    //    //自定义方法统计实现类,不指定默认使用自带实现方式
    //    //impl = "com.miqt.plugindemo.MyTimeP"
}
```

添加类库依赖：

```groovy
dependencies {
    implementation 'me.miqt.plugin.tools:pluginlib:0.2.2'
}
```

这时候点击 build 就可以在控制台看到输出了：

```
┌------------------------┐
|      Method Hook       |
└------------------------┘
[Config]:{"all":true,"impl":"","mapping":true,"log":true,"enable":true,"replaceJar":false,"jarRegexs":[".*"],"methodRegexs":[],"classRegexs":[]}
[jar][regex]:.* androidx.appcompat:appcompat:1.1.0 is injected.
[jar][regex]:.* androidx.constraintlayout:constraintlayout:1.1.3 is injected.
[jar][regex]:.* androidx.fragment:fragment:1.1.0 is injected.
[jar][regex]:.* androidx.appcompat:appcompat-resources:1.1.0 is injected.
[jar][regex]:.* androidx.drawerlayout:drawerlayout:1.0.0 is injected.
[jar][regex]:.* androidx.viewpager:viewpager:1.0.0 is injected.
[jar][regex]:.* androidx.loader:loader:1.0.0 is injected.
[jar][regex]:.* androidx.activity:activity:1.0.0 is injected.
[jar][regex]:.* androidx.vectordrawable:vectordrawable-animated:1.1.0 is injected.
[jar][regex]:.* androidx.vectordrawable:vectordrawable:1.1.0 is injected.
[jar][regex]:.* androidx.customview:customview:1.0.0 is injected.
[jar][regex]:.* androidx.core:core:1.1.0 is injected.
[jar][regex]:.* androidx.cursoradapter:cursoradapter:1.0.0 is injected.
[jar][regex]:.* androidx.versionedparcelable:versionedparcelable:1.1.0 is injected.
[jar][regex]:.* androidx.collection:collection:1.1.0 is injected.
[jar][regex]:.* androidx.lifecycle:lifecycle-runtime:2.1.0 is injected.
[jar][regex]:.* androidx.lifecycle:lifecycle-viewmodel:2.1.0 is injected.
[jar][regex]:.* androidx.savedstate:savedstate:1.0.0 is injected.
[jar][regex]:.* androidx.interpolator:interpolator:1.0.0 is injected.
[jar][regex]:.* androidx.lifecycle:lifecycle-livedata:2.0.0 is injected.
[jar][regex]:.* androidx.lifecycle:lifecycle-livedata-core:2.0.0 is injected.
[jar][regex]:.* androidx.arch.core:core-runtime:2.0.0 is injected.
[jar][regex]:.* androidx.arch.core:core-common:2.1.0 is injected.
[jar][regex]:.* androidx.lifecycle:lifecycle-common:2.1.0 is injected.
[jar][regex]:.* androidx.annotation:annotation:1.1.0 is injected.
[jar][regex]:.* androidx.constraintlayout:constraintlayout-solver:1.1.3 is injected.
[jar][regex]:.* :pluginlib is injected.
[jar][regex]:.* 900c6dbe31684fb656ee8cc9d146af579bae6f1f is injected.
[class]Hello$Inner$1.class is injected.
[class]Hello$Inner.class is injected.
[class]Hello.class is injected.
[class]MainActivity$1.class is injected.
[class]MainActivity.class is injected.
[class]MyTimeP.class is injected.
┌------------------------┐
|      Method Hook  √    |
└------------------------┘
```



>  这个插件是借鉴了很多大佬的代码，并结合自己的想法进行了一些调整，在此感谢他们付出的努力。
>
> https://github.com/novoda/bintray-release  
> https://github.com/JeasonWong/CostTime  
> https://github.com/MegatronKing/StringFog  