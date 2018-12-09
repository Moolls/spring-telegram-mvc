package ru.moolls.telemvc.annotation;

import ru.moolls.telemvc.entity.MethodType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BotMapping {

    String msgPath() default "";

    String callbackPath() default "";

    String replyOn() default "";

    MethodType methodType() default MethodType.MSG;
}
