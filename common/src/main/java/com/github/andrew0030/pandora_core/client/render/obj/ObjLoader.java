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
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ObjLoader implements PacoResourceManager {
    private final List<String> paths;
    private final Predicate<ResourceLocation> validator;
    private final Consumer<ObjLoader> postReload;
    public final Map<ResourceLocation, ObjModel> models = new Object2ObjectRBTreeMap<>();

    public ObjLoader(
            List<String> paths,
            Predicate<ResourceLocation> validator,
            Consumer<ObjLoader> postReload
    ) {
        this.paths = paths;
        this.validator = validator;
        this.postReload = postReload;
    }

    @Override
    public void run(ResourceManager manager, ResourceDispatcher dispatcher) {
        ResourceDispatcher.SubDispatch<Void> disp = null;
        for (String path : paths) {
            Runnable r = () -> manager.listResources(
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
            if (disp == null) disp = dispatcher.prepare("paco_load_objs_" + path, r);
            else disp = disp.prepare("paco_load_objs_" + path, (v) -> {
                r.run();
            });
        }

        if (disp != null) disp.barrier().apply("paco_load_objs", (v) -> postReload.accept(this));
        else dispatcher.apply("paco_load_objs", () -> postReload.accept(this));
    }
}
