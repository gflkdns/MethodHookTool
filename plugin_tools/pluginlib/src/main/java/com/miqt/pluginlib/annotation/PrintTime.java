package com.miqt.pluginlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @Copyright 2019 analysys Inc. All rights reserved.
 * @Description: 方法耗时统计注解
 * @Version: 1.0
 * @Create: 2019-11-13 11:18:45
 * @author: miqt
 * @mail: miqingtang@analysys.com.cn
 */
@Target(ElementType.METHOD)
public @interface PrintTime {
}
