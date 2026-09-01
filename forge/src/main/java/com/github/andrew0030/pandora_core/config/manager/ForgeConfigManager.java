package com.github.andrew0030.pandora_core.config.manager;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.forge_spec.ForgeConfigHandler;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.Collection;

public class ForgeConfigManager implements IConfigManager {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "ForgeConfigManager");
    // Config Managing
    private final ModConfig modConfig;
    private final ForgeConfigHandler handler;

    public ForgeConfigManager(ModConfig modConfig) {
        this.modConfig = modConfig;
        this.handler = new ForgeConfigHandler(modConfig);
    }

//    @Override
//    public void save() {
//        // - Get pending changes from the UI (we will design this next)
//        // - Apply them to modConfig.getConfigData()
//        // - Fire the ModConfigEvent.Reloading via reflection/mixin
//        // - modConfig.save()
//    }

    @Override
    public Collection<ConfigDataHolder<?>> getDataHolders() {
        return this.handler.getConfigDataHolders();
    }

    @Override
    public String getModId() {
        return this.modConfig.getModId();
    }

    @Override
    public String getConfigName() {
        return this.modConfig.getFileName(); // TODO maybe improve this?
    }
}