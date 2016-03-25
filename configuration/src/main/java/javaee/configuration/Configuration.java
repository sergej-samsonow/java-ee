package javaee.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Configuration injection qualifier expected collection name as parameter.
 *
 * @author Sergej Samsonow<sergej.samsonow.public@googlemail.com>
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Configuration {

    /**
     * Collection name.
     *
     * @return name.
     */
    String value() default "";

}
