package me.gookven.swingx.generictable.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface GenericTableColumn {
    /**
     * Displayed name
     */
    String value();

    /**
     * Declarative exclusion from table columns
     */
    boolean visible() default true;

    /**
     * Sets editable property for the whole column
     */
    boolean editable() default false;
}
