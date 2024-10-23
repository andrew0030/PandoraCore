package com.github.andrew0030.pandora_core.config.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Helper class containing all the value annotations that can be used with {@link PaCoConfig}. */
public class PaCoConfigValues {

    /** Used to specify an {@link Integer} value. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface IntegerValue {
        int minValue() default Integer.MIN_VALUE;
        int maxValue() default Integer.MAX_VALUE;
    }
}