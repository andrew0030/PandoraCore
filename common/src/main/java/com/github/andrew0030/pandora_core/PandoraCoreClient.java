package com.github.andrew0030.pandora_core;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.LogicalSide;

import java.util.Arrays;

public class PandoraCoreClient {
    public static final TemplateShaderResourceLoader templateShaderLoader = new TemplateShaderResourceLoader();

    /* Early client init (mod construction) */
    public static void earlyInit() {
        Services.RELOAD_LISTENER.registerResourceLoader((side) -> {
            if (side == LogicalSide.CLIENT) {
                return Arrays.asList(
                        templateShaderLoader
                );
            } else return null;
        });
    }

    /** Client Init */
    public static void init() {
    }

    /** Thread Safe Client Init */
    public static void initThreadSafe() {

    }
}