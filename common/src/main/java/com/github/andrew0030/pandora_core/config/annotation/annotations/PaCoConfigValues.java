package com.github.andrew0030.pandora_core.config.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Helper class containing all the value annotations that can be used with {@link PaCoConfig}. */
public class PaCoConfigValues {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface BooleanValue {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface IntegerValue {
        int minValue() default Integer.MIN_VALUE;
        int maxValue() default Integer.MAX_VALUE;
        boolean showFullRange() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ByteValue {
        byte minValue() default Byte.MIN_VALUE;
        byte maxValue() default Byte.MAX_VALUE;
        boolean showFullRange() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ShortValue {
        short minValue() default Short.MIN_VALUE;
        short maxValue() default Short.MAX_VALUE;
        boolean showFullRange() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface DoubleValue {
        double minValue() default Double.MIN_VALUE;
        double maxValue() default Double.MAX_VALUE;
        boolean showFullRange() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface FloatValue {
        float minValue() default Float.MIN_VALUE;
        float maxValue() default Float.MAX_VALUE;
        boolean showFullRange() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface LongValue {
        long minValue() default Long.MIN_VALUE;
        long maxValue() default Long.MAX_VALUE;
        boolean showFullRange() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface StringValue {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ListValue {
        Class<?> elementType();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface EnumValue {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Comment {
        String value();
        int padding() default 1;
    }
}