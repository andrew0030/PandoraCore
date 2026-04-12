package com.github.andrew0030.pandora_core.utils.mod_warnings;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.shader_checker.ShaderChecker;
import com.github.andrew0030.pandora_core.utils.update_checker.ComparableVersion;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for providing runtime mod warnings to be displayed in the PaCo mods screen.
 * <p>
 * <strong>Requirements:</strong>
 * <ul>
 *   <li>Class must be {@code public}</li>
 *   <li>Must contain a {@code public static} method named {@code getWarningFactory}</li>
 *   <li>Method signature must be exactly: {@code List<Component> getWarningFactory()}</li>
 * </ul>
 * <p>
 * <strong>Important:</strong> This class may be called on both client and server threads.
 * Avoid client-only logic unless properly gated with {@code DistExecutor} or similar.
 *
 * <h3>Example:</h3>
 * <pre>{@code
 * public class ExampleWarningFactory {
 *     public static List<Component> getWarningFactory() {
 *         List<Component> warnings = new ArrayList<>();
 *             // Dynamic check - evaluated each time the screen opens
 *             if (someRuntimeCondition()) {
 *                 warnings.add(Component.translatable("warnings.example_mod.runtime_issue"));
 *             }
 *             // Static check - result precomputed elsewhere and reused
 *             if (ExampleModStaticChecks.someStaticCheck) {
 *                 warnings.add(Component.translatable("warnings.example_mod.static_issue"));
 *             }
 *         return warnings;
 *     }
 * }
 * }</pre>
 */
public class PaCoWarningFactory {
    public static boolean isCTMDisabled = false;

    public static void init() {
        if (Services.PLATFORM.isModLoaded("sodium") && !Services.PLATFORM.isModLoaded("indium")) {
            // The version after which Sodium supports FRAPI natively (0.6.0+)
            ComparableVersion nativeSupportVersion = new ComparableVersion("0.6.0");
            ComparableVersion sodiumVersion = new ComparableVersion(PandoraCore.getModHolder("sodium").getModVersion());
            // If any Sodium version before 6.0.0 is used and Indium is missing, disables CTM
            if (sodiumVersion.compareTo(nativeSupportVersion) < 0) PaCoWarningFactory.isCTMDisabled = true;
        }
    }

    /**
     * Provides a list of warning messages to display in the PaCo mods screen.
     * <p>
     * <strong>Signature Requirements:</strong>
     * <ul>
     *   <li>Must be {@code public static}</li>
     *   <li>Method name must be exactly {@code getWarningFactory}</li>
     *   <li>Return type must be {@code List<Component>}</li>
     *   <li>Takes no parameters</li>
     * </ul>
     * <p>
     * This method is invoked lazily when warnings are needed (e.g., when the GUI opens),
     * enabling dynamic runtime checks.<br/>
     * <strong>Note:</strong> This method is called on every screen open. Cache results
     * internally if your warnings are static or expensive to compute.
     *
     * @return A {@link List} of {@link Component Components} representing warnings to display.
     */
    public static List<Component> getWarningFactory() {
        List<Component> warnings = new ArrayList<>();
        if (ShaderChecker.OF_HANDLER.isLoaded())
            warnings.add(Component.translatable("warnings.pandora_core.opti_fine.post_shader"));
        if (PaCoWarningFactory.isCTMDisabled)
            warnings.add(Component.translatable("warnings.pandora_core.sodium.indium"));
        return warnings;
    }
}