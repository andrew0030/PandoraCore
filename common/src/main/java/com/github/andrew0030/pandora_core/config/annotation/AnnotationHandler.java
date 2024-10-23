package com.github.andrew0030.pandora_core.config.annotation;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import org.slf4j.Logger;

public class AnnotationHandler {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "AnnotationHandler");
    private final Class<?> configClass;
    private final String configName;

    public AnnotationHandler(Class<?> configClass) {
        this.configClass = configClass;
        this.configName = this.retrieveConfigName();
    }

    private String retrieveConfigName() {
        PaCoConfig configAnnotation = configClass.getAnnotation(PaCoConfig.class);
        if (configAnnotation == null)
            throw new IllegalArgumentException("Class " + configClass.getName() + " must be annotated with @PaCoConfig");
        return configAnnotation.value();
    }

    /** @return The name specified in the {@link PaCoConfig} annotation. */
    public String getConfigName() {
        return configName;
    }
}