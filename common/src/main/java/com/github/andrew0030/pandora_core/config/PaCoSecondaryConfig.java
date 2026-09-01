package com.github.andrew0030.pandora_core.config;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.annotations.ConfigType;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfigValues;

@PaCoConfig.Config(type = ConfigType.COMMON, modId = PandoraCore.MOD_ID, name = "secondary")
@PaCoConfig.SubFolder("paco_sub_folder")
public class PaCoSecondaryConfig {

    @PaCoConfigValues.Comment("Just some pointless placeholder value to make a valid config.")
    @PaCoConfigValues.BooleanValue
    public static Boolean someBoolean = true;
}