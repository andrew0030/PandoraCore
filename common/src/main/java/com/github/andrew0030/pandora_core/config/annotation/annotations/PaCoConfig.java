package com.github.andrew0030.pandora_core.config.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//TODO: Deal with the mod id prefix in a better way, as it should automatically create configs using:
// "mod id" + "-" + "value" + ".toml" instead of having to manually specify the "mod id"
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PaCoConfig {
    String value();
}