package com.github.andrew0030.pandora_core.test;

import com.github.andrew0030.pandora_core.client.render.obj.ObjLoader;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.test.TemplateShaderTest;
import com.github.andrew0030.pandora_core.utils.LogicalSide;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class ModelLoaderTest {
    private static final ObjLoader loader = new ObjLoader(
            List.of("test/obj"),
            (pth) -> pth.getNamespace().equals("pandora_core") && pth.getPath().endsWith(".obj"),
            (loader) -> TemplateShaderTest.uploadVBO(
                    loader.models.get(new ResourceLocation("pandora_core:test/obj/queen.obj")),
                    loader.models.get(new ResourceLocation("pandora_core:test/obj/cube.obj"))
            )
    );

    public static void init() {
        Services.RELOAD_LISTENER.registerResourceLoader((side) -> {
            if (side == LogicalSide.CLIENT)
                return List.of(loader);
            return null;
        });
    }
}
