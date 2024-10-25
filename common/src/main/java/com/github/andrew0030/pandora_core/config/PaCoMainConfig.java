package com.github.andrew0030.pandora_core.config;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfigValues;

//TODO: create a config system using NightConfig
// - Probably add annotations as its a nice clean and simple way to create configs
// - Create a Screen builder that uses the PaCoConfigs and well creates a screen
// - Try to wrap the ForgeConfigSpec so I can generate PaCoConfigs for all the mods that use ForgeConfigSpec but don't provide any screen
// - Use optional mixins to create config screen providers, for the popular config APIs. Probably this will work by grabbing the config and using the package to get the mod they belong to.
// - Alternatively if mods like Catalogue or Mod Menu are installed, check their config screen entry points
// - Maybe keep track of mods that used the PaCoMainConfig system...

@PaCoConfig(modId = PandoraCore.MOD_ID, name = "main")
public class PaCoMainConfig {

    @PaCoConfigValues.BooleanValue
    public boolean falseValue = false;

    @PaCoConfigValues.BooleanValue
    public boolean trueValue = true;

    @PaCoConfigValues.IntegerValue
    public int integerValue1 = 11;

    @PaCoConfigValues.IntegerValue
    public int integerValue2 = 12;

    @PaCoConfigValues.IntegerValue
    public int integerValue3 = 13;

    @PaCoConfigValues.IntegerValue(minValue = 0, maxValue = 100)
    public int rangedAltInt = 100;

    @PaCoConfigValues.IntegerValue(minValue = 0, maxValue = 10)
    public int rangedIntegerValue = 5;
}