package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.platform.services.IResourceLoaderHelper;
import com.github.andrew0030.pandora_core.utils.LogicalSide;
import com.github.andrew0030.pandora_core.utils.resource.PacoResourceManager;
import com.github.andrew0030.pandora_core.utils.resource.ResourceDispatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.core.appender.rewrite.MapRewritePolicy;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

public class FabricReloadListenerHelper implements IResourceLoaderHelper {
    static int index = 0;

    @Override
    public void registerResourceLoader(Function<LogicalSide, List<PacoResourceManager>> function) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ResourceManagerHelper helper = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
            List<PacoResourceManager> t = function.apply(LogicalSide.CLIENT);
            if (t != null) {
                for (PacoResourceManager resourceManagerReloadListener : t) {
                    int iDex = index++;
                    helper.registerReloadListener(new IdentifiableResourceReloadListener() {
                        @Override
                        public ResourceLocation getFabricId() {
                            return new ResourceLocation("pandoras_core:reload_listener_" + String.valueOf(iDex).replace("-", "_"));
                        }

                        @Override
                        public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                            ResourceDispatcher dispatcher = new ResourceDispatcher(
                                    preparationBarrier,
                                    preparationsProfiler, reloadProfiler,
                                    backgroundExecutor, gameExecutor
                            );
                            resourceManagerReloadListener.run(resourceManager, dispatcher);
                            return dispatcher.future();
                        }
                    });
                }
            }
        }

        ResourceManagerHelper helper = ResourceManagerHelper.get(PackType.SERVER_DATA);
        List<PacoResourceManager> t = function.apply(LogicalSide.SERVER);
        if (t == null)
            return;

        for (PacoResourceManager resourceManagerReloadListener : t) {
            int iDex = index++;
            helper.registerReloadListener(new IdentifiableResourceReloadListener() {
                @Override
                public ResourceLocation getFabricId() {
                    return new ResourceLocation("pandoras_core:reload_listener_" + String.valueOf(iDex).replace("-", "_"));
                }

                @Override
                public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                    ResourceDispatcher dispatcher = new ResourceDispatcher(
                            preparationBarrier,
                            preparationsProfiler, reloadProfiler,
                            backgroundExecutor, gameExecutor
                    );
                    resourceManagerReloadListener.run(resourceManager, dispatcher);
                    return dispatcher.future();
                }
            });
        }
    }

    @Override
    public void registerVanillaResourceLoader(Function<LogicalSide, List<ResourceManagerReloadListener>> function) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ResourceManagerHelper helper = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
            List<ResourceManagerReloadListener> t = function.apply(LogicalSide.CLIENT);
            if (t != null) {
                for (ResourceManagerReloadListener resourceManagerReloadListener : t) {
                    int iDex = index++;
                    helper.registerReloadListener(new IdentifiableResourceReloadListener() {
                        @Override
                        public ResourceLocation getFabricId() {
                            return new ResourceLocation("pandoras_core:reload_listener_" + String.valueOf(iDex).replace("-", "_"));
                        }

                        @Override
                        public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                            return resourceManagerReloadListener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
                        }
                    });
                }
            }
        }

        ResourceManagerHelper helper = ResourceManagerHelper.get(PackType.SERVER_DATA);
        List<ResourceManagerReloadListener> t = function.apply(LogicalSide.SERVER);
        if (t == null)
            return;

        for (ResourceManagerReloadListener resourceManagerReloadListener : t) {
            int iDex = index++;
            helper.registerReloadListener(new IdentifiableResourceReloadListener() {
                @Override
                public ResourceLocation getFabricId() {
                    return new ResourceLocation("pandoras_core:reload_listener_" + String.valueOf(iDex).replace("-", "_"));
                }

                @Override
                public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                    return resourceManagerReloadListener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
                }
            });
        }
    }
}
