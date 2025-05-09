package com.github.andrew0030.pandora_core;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.events.ForgeClientTickEvent;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoParentScreenGetter;
import com.github.andrew0030.pandora_core.utils.ConnectedTextureBakedModel;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;

public class PandoraCoreClientForge {

    public static void init(IEventBus modEventBus, IEventBus forgeEventBus)
    {
        PandoraCoreClient.earlyInit();
        // Mod Event Bus
        modEventBus.addListener(PandoraCoreClientForge::clientSetup);
        modEventBus.addListener(PandoraCoreClientForge::onModelBake);
        // Forge Event Bus
        forgeEventBus.addListener(ForgeClientTickEvent::init);

        // Registers Config Screen (Basically opens the PaCo screen if you press the config button in the Forge Mods Screen)
        //TODO probably alter this a bit so it opens the actual config screen directly?
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mcClient, screen) -> {
                    if (screen instanceof IPaCoParentScreenGetter pacoParentScreenGetter) {
                        if (pacoParentScreenGetter.pandoraCore$getParentScreen() instanceof TitleScreen titleScreen) {
                            return new PaCoScreen(titleScreen, screen);
                        }
                    }
                    return new PaCoScreen(null, screen);
                })
        );
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        // Common Module Initialization.
        PandoraCoreClient.init();
        event.enqueueWork(PandoraCoreClient::initThreadSafe);

        // Loader Module Initialization.
        // Nothing atm...
    }

    private static void onModelBake(ModelEvent.ModifyBakingResult event) {
        for (Map.Entry<ResourceLocation, BakedModel> entry : event.getModels().entrySet()) {
            ResourceLocation id = entry.getKey();
            if (id.getNamespace().equals(PandoraCore.MOD_ID) && id.getPath().equals("connected_block")) {
                BakedModel original = entry.getValue();
                ConnectedTextureBakedModel wrapped = new ConnectedTextureBakedModel(original);
                event.getModels().put(id, wrapped);
            }
        }
    }
}