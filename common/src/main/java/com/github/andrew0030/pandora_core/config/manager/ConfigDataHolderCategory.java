package com.github.andrew0030.pandora_core.config.manager;

public class ConfigDataHolderCategory extends ConfigDataHolder<Void> {

    public ConfigDataHolderCategory() {}

    @Override
    public boolean isCategory() {
        return true;
    }
}