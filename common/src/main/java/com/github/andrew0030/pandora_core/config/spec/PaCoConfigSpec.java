package com.github.andrew0030.pandora_core.config.spec;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.github.andrew0030.pandora_core.config.annotation.AnnotationHandler;
import com.github.andrew0030.pandora_core.platform.Services;

public class PaCoConfigSpec {

    private final AnnotationHandler annotationHandler;
    private final FileConfig fileConfig;

    public PaCoConfigSpec(Class<?> configClass) {
        this.annotationHandler = new AnnotationHandler(configClass);
        this.fileConfig = FileConfig.builder(String.format("%s\\%s.toml", Services.PLATFORM.getConfigDirectory(), this.annotationHandler.getConfigName())).build();

        this.fileConfig.load();
    }
}