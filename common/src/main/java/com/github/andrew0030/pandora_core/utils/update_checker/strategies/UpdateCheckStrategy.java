package com.github.andrew0030.pandora_core.utils.update_checker.strategies;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;

import java.net.http.HttpClient;
import java.time.Duration;

public abstract class UpdateCheckStrategy {
    protected static final int HTTP_TIMEOUT_SECS = 15; // TODO: make this a config option
    protected static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(HTTP_TIMEOUT_SECS))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    private static String PACO_VERSION;

    protected String buildUserAgent() {
        // Initializes and caches the PaCo version
        if (PACO_VERSION == null) {
            ModDataHolder paco = PandoraCore.getModHolder("pandora_core");
            if (paco == null) PACO_VERSION = "";
            else if (paco.getModVersion() == null) PACO_VERSION = "";
            else PACO_VERSION = paco.getModVersion();
        }
        // Builds and returns an user agent string
        return String.format(
                "andrew0030/PandoraCore%s (https://github.com/andrew0030/PandoraCore; loader=%s; Java=%s; OS=%s)",
                PACO_VERSION.isEmpty() ? "" : "/" + PACO_VERSION,
                Services.PLATFORM.getPlatformName(),
                System.getProperty("java.version"),
                System.getProperty("os.name")
        );
    }

    public abstract void performUpdateCheck();
}