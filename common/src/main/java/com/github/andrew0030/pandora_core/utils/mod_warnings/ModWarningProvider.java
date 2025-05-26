package com.github.andrew0030.pandora_core.utils.mod_warnings;

import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

/**
 * Functional interface used to provide runtime mod warnings for display inside the PaCoScreen.
 * <p>
 * Implementing classes should return a {@link Supplier} that produces a list of
 * warning messages ({@link Component} instances) when invoked.
 * This allows warning logic to be evaluated lazily, when the GUI is opened,
 * rather than at registration time.
 * <p>
 * <strong>Important:</strong> This interface is called on both the {@code client} and {@code server}.
 * Avoid using client-only classes or logic unless properly gated.
 *
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * public class ModWarningFactory implements ModWarningProvider {
 *     @Override
 *     public Supplier<List<Component>> getWarnings() {
 *         return () -> {
 *             List<Component> warnings = new ArrayList<>();
 *             // Dynamic check - evaluated each time the screen opens
 *             if (someRuntimeCondition()) {
 *                 warnings.add(Component.translatable("warnings.example_mod.runtime_issue"));
 *             }
 *             // Static check - result precomputed elsewhere and reused
 *             if (ExampleModStaticChecks.someStaticCheck) {
 *                 warnings.add(Component.translatable("warnings.example_mod.static_issue"));
 *             }
 *             return warnings;
 *         };
 *     }
 * }
 * }</pre>
 */
@FunctionalInterface
public interface ModWarningProvider {

    /**
     * Returns a {@link Supplier} that provides a list of warnings to display.
     * <p>
     * The supplier is called only when the warnings are needed (e.g., when the PaCoScreen GUI opens),
     * allowing for dynamic, runtime evaluation of conditions.
     * <p>
     * If your warnings are static or only need to be checked once, you may cache results and reuse them.
     *
     * @return a {@link Supplier} that returns a {@link List} of {@link Component} warnings.
     */
    Supplier<List<Component>> getWarnings();
}