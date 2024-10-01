package com.github.andrew0030.pandora_core.utils.mod_warnings;

import net.minecraft.network.chat.Component;

import java.util.List;

/** This interface should be implemented for ModWarningFactory classes. */
@FunctionalInterface
public interface ModWarningProvider {
    List<Component> getWarnings();
}