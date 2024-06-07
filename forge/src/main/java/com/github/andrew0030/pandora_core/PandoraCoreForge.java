package com.github.andrew0030.pandora_core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(PandoraCore.MOD_ID)
public class PandoraCoreForge {

    public PandoraCoreForge() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::commonSetup);
        if (FMLEnvironment.dist == Dist.CLIENT)
            PandoraCoreClientForge.init(modEventBus, forgeEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Common Module Initialization.
        PandoraCore.init();
        event.enqueueWork(PandoraCore::initThreadSafe);

        // Loader Module Initialization.
        // Nothing atm...
    }
}