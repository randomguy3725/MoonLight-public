package wtf.moonlight.events.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify the priority of an event handling method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventPriority {
    /**
     * The priority value of the event handling method. Methods with lower values will be executed first.
     *
     * @return The priority value, with a default of 10 if not specified.
     */
    int value() default 10;
}