package com.github.andrew0030.pandora_core.utils.shader_checker;

import com.github.andrew0030.pandora_core.utils.shader_checker.loaders.BaseShaderHandler;
import com.github.andrew0030.pandora_core.utils.shader_checker.loaders.IrisShaderHandler;
import com.github.andrew0030.pandora_core.utils.shader_checker.loaders.OFShaderHandler;

import java.util.ArrayList;
import java.util.List;

/** A helper class that allows to easily check if any shaders are loaded. */
public class ShaderChecker {
    private static final List<BaseShaderHandler> HANDLERS = new ArrayList<>();

    public static final BaseShaderHandler OF_HANDLER   = ShaderChecker.register(new OFShaderHandler());
    public static final BaseShaderHandler IRIS_HANDLER = ShaderChecker.register(new IrisShaderHandler());

    /**
     * Used to register {@link BaseShaderHandler} objects, this initializes them and adds them to
     * HANDLES if they are present, this way they are only checked if they are loaded.
     * @param handler The {@link BaseShaderHandler} that will be initialized
     * @return The given {@link BaseShaderHandler}, so they can be stored in a field
     */
    public static BaseShaderHandler register(BaseShaderHandler handler) {
        if (handler.isLoaded())
            HANDLERS.add(handler);
        return handler;
    }

    /** @return Whether there is an active Shader */
    public static boolean isShaderActive() {
        // Since we only add the handler to the list if its present this is pretty efficient,
        // as we skip checking all handlers that aren't actually installed
        for (BaseShaderHandler handler : HANDLERS)
            if (handler.isShaderLoaded())
                return true;
        return false;
    }
}