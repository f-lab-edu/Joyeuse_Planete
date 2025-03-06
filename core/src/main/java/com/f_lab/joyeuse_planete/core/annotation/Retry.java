package com.f_lab.joyeuse_planete.core.annotation;


import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {

  int value() default 3;
  Class<? extends Exception>[] retryable() default { Exception.class };
  Class<? extends Exception>[] nonRetryable() default { JoyeusePlaneteApplicationException.class };
  Backoff backoff() default @Backoff();
}
