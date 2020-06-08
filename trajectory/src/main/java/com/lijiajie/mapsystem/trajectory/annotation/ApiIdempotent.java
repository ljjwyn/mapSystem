package com.lijiajie.mapsystem.trajectory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author roman.zhang
 * @Date: 2019/8/27 11:36
 * @Version:V1.0
 * @Description:ApiIdempotent
 *   在保证接口幂等性的，controller方法上使用。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiIdempotent {
}
