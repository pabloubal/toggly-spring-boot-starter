package io.github.pabloubal.togglysbstarter.core.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * Main entry point for the Toggly Framework
 * @author Pablo Ubal - pablo.ubal@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Toggly {
    @AliasFor("name")
    String value() default "";
    @AliasFor("value")
    String name() default "";
    Class<?>[] fallbackClass() default {};
}
