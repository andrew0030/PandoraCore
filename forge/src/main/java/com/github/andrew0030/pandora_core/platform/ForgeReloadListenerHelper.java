package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.platform.services.IResourceLoaderHelper;
import com.github.andrew0030.pandora_core.utils.LogicalSide;
import com.github.andrew0030.pandora_core.utils.resource.PacoResourceManager;
import com.github.andrew0030.pandora_core.utils.resource.ResourceDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

public class ForgeReloadListenerHelper implements IResourceLoaderHelper {
    @Override
    public void registerResourceLoader(Function<LogicalSide, List<PacoResourceManager>> function) {
        final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        forgeEventBus.addListener((Consumer<AddReloadListenerEvent>) (evt) -> {
            List<PacoResourceManager> t = function.apply(LogicalSide.SERVER);
            if (t == null)
                return;

            for (PacoResourceManager resourceManagerReloadListener : t) {
                evt.addListener(new ResourceManagerReloadListener() {
                    @Override
                    public CompletableFuture<Void> reload(PreparationBarrier pStage, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
                        ResourceDispatcher dispatcher = new ResourceDispatcher(
                                pStage,
                                pPreparationsProfiler, pReloadProfiler,
                                pBackgroundExecutor, pGameExecutor
                        );
                        resourceManagerReloadListener.run(pResourceManager, dispatcher);
                        return dispatcher.future();
                    }

                    @Override
                    public void onResourceManagerReload(ResourceManager pResourceManager) {
                        // no-op
                    }
                });
            }
        });

        if (FMLEnvironment.dist.isClient()) {
            ReloadableResourceManager reloadableResourceManager = (ReloadableResourceManager) Minecraft.getInstance().getResourceManager();
            List<PacoResourceManager> t = function.apply(LogicalSide.CLIENT);
            if (t == null)
                return;

            for (PacoResourceManager resourceManagerReloadListener : t) {
                reloadableResourceManager.registerReloadListener(new ResourceManagerReloadListener() {
                    @Override
                    public CompletableFuture<Void> reload(PreparationBarrier pStage, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
                        ResourceDispatcher dispatcher = new ResourceDispatcher(
                                pStage,
                                pPreparationsProfiler, pReloadProfiler,
                                pBackgroundExecutor, pGameExecutor
                        );
                        resourceManagerReloadListener.run(pResourceManager, dispatcher);
                        return dispatcher.future();
                    }

                    @Override
                    public void onResourceManagerReload(ResourceManager pResourceManager) {
                        // no-op
                    }
                });
            }
        }
    }

    @Override
    public void registerVanillaResourceLoader(Function<LogicalSide, List<ResourceManagerReloadListener>> function) {
        final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        forgeEventBus.addListener((Consumer<AddReloadListenerEvent>) (evt) -> {
            List<ResourceManagerReloadListener> t = function.apply(LogicalSide.SERVER);
            if (t == null)
                return;

            for (ResourceManagerReloadListener resourceManagerReloadListener : t) {
                evt.addListener(resourceManagerReloadListener);
            }
        });

        if (FMLEnvironment.dist.isClient()) {
            ReloadableResourceManager reloadableResourceManager = (ReloadableResourceManager) Minecraft.getInstance().getResourceManager();
            List<ResourceManagerReloadListener> t = function.apply(LogicalSide.CLIENT);
            if (t == null)
                return;

            for (ResourceManagerReloadListener resourceManagerReloadListener : t) {
                reloadableResourceManager.registerReloadListener(resourceManagerReloadListener);
            }
        }
    }
}
