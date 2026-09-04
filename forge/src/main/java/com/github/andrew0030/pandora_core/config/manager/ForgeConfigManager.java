package com.github.andrew0030.pandora_core.config.manager;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.config.forge_spec.ForgeConfigHandler;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Locale;

public class ForgeConfigManager implements IConfigManager {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "ForgeConfigManager");
    // Config Managing
    private final ModConfig modConfig;
    private final ForgeConfigHandler handler;
    // Util
    private final String formatedName;

    public ForgeConfigManager(ModConfig modConfig) {
        this.modConfig = modConfig;
        this.handler = new ForgeConfigHandler(modConfig);
        String name = this.modConfig.getFileName().replace(this.getModId(), "").replace(".toml", "");
        if (!name.toLowerCase(Locale.ROOT).contains("config"))
            name = name + " config";
        this.formatedName = PaCoGuiUtils.toTitleCaseFormat(name);
    }

//     TODO: implement bulk saving
//      - Get the pending changes from some sort of map
//      - Apply them to modConfig.getConfigData()
//      - Fire the ModConfigEvent.Reloading via reflection/mixin
//      - And lastly save modConfig.save()
//    @Override
//    public void save() {

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
        return this.formatedName;
    }
}