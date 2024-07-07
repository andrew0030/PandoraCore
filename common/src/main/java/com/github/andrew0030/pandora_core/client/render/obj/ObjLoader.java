package com.github.andrew0030.pandora_core.client.render.obj;

import com.github.andrew0030.pandora_core.utils.resource.PacoResourceManager;
import com.github.andrew0030.pandora_core.utils.resource.ResourceDispatcher;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ObjLoader implements PacoResourceManager {
    private final List<String> paths;
    private final Predicate<ResourceLocation> validator;
    public final Map<ResourceLocation, ObjModel> models = new Object2ObjectRBTreeMap<>();

    public ObjLoader(
            List<String> paths,
            Predicate<ResourceLocation> validator
    ) {
        this.paths = paths;
        this.validator = validator;
    }

    @Override
    public void run(ResourceManager manager, ResourceDispatcher dispatcher) {
        for (String path : paths) {
            dispatcher.apply(
                    "paco_load_objs_" + path,
                    () -> {
                        manager.listResources(
                                path,
                                validator
                        ).forEach((pth, res) -> {
                            try {
                                InputStream is = res.open();
                                ObjModel mdl = ObjModel.loadModel(is);
                                models.put(pth, mdl);
                            } catch (Throwable err) {
                                throw new RuntimeException("TODO: logging");
                            }
                        });
                    }
            );
        }
    }
}
