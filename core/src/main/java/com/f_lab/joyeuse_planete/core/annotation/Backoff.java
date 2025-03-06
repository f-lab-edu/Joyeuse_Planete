package com.f_lab.joyeuse_planete.core.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.f_lab.joyeuse_planete.core.util.time.TimeConstants.TimeConstantsMillis.ONE_SECOND;

@Target({ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Backoff {
  int delay() default ONE_SECOND;
  int multiplier() default 0;
}
