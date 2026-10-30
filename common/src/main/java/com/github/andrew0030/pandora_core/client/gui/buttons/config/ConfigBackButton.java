package com.github.andrew0030.pandora_core.client.gui.buttons.config;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.NotNull;

// TODO narration
public class ConfigBackButton extends ConfigActionButton {

    public ConfigBackButton(PaCoConfigScreen screen) {
        super(screen, Component.literal("Exit"), ConfigActionType.BACK);
    }

    @Override
    public void onPress() {
        // TODO Probably fine tune this to exit fully if optional instant escape close is enabled!
        if (StringUtil.isNullOrEmpty(this.screen.searchBox.getValue())) {
            this.screen.onClose();
        } else {
            this.screen.searchBox.setValue("");
        }
    }

    @NotNull
    @Override
    public Component getMessage() {
        // TODO Probably fine tune this to exit fully if optional instant escape close is enabled!
        if (!StringUtil.isNullOrEmpty(this.screen.searchBox.getValue()) || (this.screen.getCurrentNode() != this.screen.getRootNode() && this.screen.getCurrentNode().getParent() != null))
            return Component.literal("Back");
        return super.getMessage();
    }
}