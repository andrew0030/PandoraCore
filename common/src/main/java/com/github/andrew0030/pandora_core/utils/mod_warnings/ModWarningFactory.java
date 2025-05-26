package com.github.andrew0030.pandora_core.utils.mod_warnings;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.shader_checker.ShaderChecker;
import com.github.andrew0030.pandora_core.utils.update_checker.ComparableVersion;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModWarningFactory implements ModWarningProvider {
    public static boolean isCTMDisabled = false;

    public static void init() {
        if (Services.PLATFORM.isModLoaded("sodium") && !Services.PLATFORM.isModLoaded("indium")) {
            // The version after which Sodium supports FRAPI natively (0.6.0+)
            ComparableVersion nativeSupportVersion = new ComparableVersion("0.6.0");
            ComparableVersion sodiumVersion = new ComparableVersion(PandoraCore.getModHolder("sodium").getModVersion());
            // If any Sodium version before 6.0.0 is used and Indium is missing, disables CTM
            if (sodiumVersion.compareTo(nativeSupportVersion) < 0) ModWarningFactory.isCTMDisabled = true;
        }
    }

    public Supplier<List<Component>> getWarnings() {
        return () -> {
            List<Component> warnings = new ArrayList<>();
            if (ShaderChecker.OF_HANDLER.isLoaded())
                warnings.add(Component.translatable("warnings.pandora_core.opti_fine.post_shader"));
            if (ModWarningFactory.isCTMDisabled)
                warnings.add(Component.translatable("warnings.pandora_core.sodium.indium"));
            return warnings;
        };
    }
}