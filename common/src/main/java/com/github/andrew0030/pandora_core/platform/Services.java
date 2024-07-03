package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.platform.services.IKeyMappingHelper;
import com.github.andrew0030.pandora_core.platform.services.IPlatformHelper;
import com.github.andrew0030.pandora_core.platform.services.IResourceLoaderHelper;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import org.slf4j.Logger;

import java.util.ServiceLoader;

public class Services {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Services");
    public static final IPlatformHelper PLATFORM = Services.load(IPlatformHelper.class);
    public static final IKeyMappingHelper KEY_MAPPING = Services.load(IKeyMappingHelper.class);
    public static final IResourceLoaderHelper RELOAD_LISTENER = Services.load(IResourceLoaderHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}