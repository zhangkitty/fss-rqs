package com.znv.fssrqs.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Created by dongzelong on  2019/9/2 13:55.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ListParam {

    /**
     * Alias for {@link #name}.
     */
    @AliasFor("name") String value() default "";

    /**
     * @since 4.2
     */
    @AliasFor("value") String name() default "";
}
