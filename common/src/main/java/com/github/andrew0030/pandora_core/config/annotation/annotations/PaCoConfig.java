package com.github.andrew0030.pandora_core.config.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class PaCoConfig {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Config {
        ConfigType type();
        String modId();
        String name();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Category {
        String value();
    }

    /**
     * Used to specify a sub-folder for the config file.<br/>
     * As an example to put the config into a sub-folder named {@code foo},
     * simply provide the {@link String} {@code "foo"} to this annotation.<br/>
     * <strong>Note</strong>: Multiple folders can be specified by separating
     * them with {@code /}. E.g. {@code "foo/some_other_folder"}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface SubFolder {
        String value();
    }

    /**
     * Used to add a comment above a Config category.<br/>
     * <code>\n</code> or multiline Strings may be used.<br/>
     * <br/>
     * <strong>padding</strong> - can be used to alter how many
     * empty spaces are to the left of the Comment.<br/>
     * Example with <code>" "</code> replaced by <code>"-"</code><br/>
     * <strong>Padding of <code>1</code>:</strong><br/>
     * #<code>-</code>Some Text...<br/>
     * <strong>Padding of <code>3</code>:</strong><br/>
     * #<code>---</code>Some Text...
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Comment {
        String value();
        int padding() default 1;
    }
}