package com.github.andrew0030.pandora_core.utils.mod_warnings;

import com.github.andrew0030.pandora_core.utils.shader_checker.ShaderChecker;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ModWarningFactory implements ModWarningProvider {

    public List<Component> getWarnings() {
        List<Component> warnings = new ArrayList<>();
        if (ShaderChecker.OF_HANDLER.isLoaded())
            warnings.add(Component.translatable("warnings.pandora_core.opti_fine.post_shader"));
        return warnings;
    }
}