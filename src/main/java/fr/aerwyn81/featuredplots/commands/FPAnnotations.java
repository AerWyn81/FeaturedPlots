package fr.aerwyn81.featuredplots.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FPAnnotations {

    String command();

    String permission() default "";

    boolean isPlayerCommand() default false;

    String[] args() default {};

    boolean isVisible() default true;
}
