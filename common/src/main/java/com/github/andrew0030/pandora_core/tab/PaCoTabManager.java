package com.github.andrew0030.pandora_core.tab;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * This class provides a simple API for adding custom entries to existing creative mode tabs, along
 * with optional control over insertion order and visibility. It also handles the sorting of insertions
 * based on dependencies between inserted items.
 * <p>
 * It also includes a {@link PaCoTabManager#builder()} method, which can be used inside
 * the common module to easily get access to a {@link CreativeModeTab.Builder}.
 * </p>
 */
public class PaCoTabManager {
    static final Map<ResourceKey<CreativeModeTab>, List<TabInsertion>> TAB_INSERTIONS = new HashMap<>();

    /**
     * A quality of life method that allows retrieving a creative
     * mode tab builder in the common module more easily.
     *
     * @return A new {@link CreativeModeTab.Builder}
     */
    public static CreativeModeTab.Builder builder() {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0);
    }

    /**
     * This method can be used to insert items into an existing {@link CreativeModeTab}.
     *
     * <p>Usage example:</p>
     * <pre>{@code
     * PaCoTabManager.insertionBuilder(CreativeModeTabs.REDSTONE_BLOCKS)
     *     .add(new ItemStack(ExItems.FUNKY.get()), new ItemStack(ExBlocks.TEST.get()))
     *     .insertBefore(Items.REDSTONE_TORCH)
     *     .apply();
     * }</pre>
     *
     * @param tab A {@link ResourceKey<CreativeModeTab>} pointing to the {@link CreativeModeTab}, items will be inserted into.
     * @return A new {@link TabInsertionBuilder} instance.
     */
    public static TabInsertionBuilder insertionBuilder(ResourceKey<CreativeModeTab> tab) {
        return new TabInsertionBuilder(tab);
    }

    /**
     * Retrieves a list of {@link TabInsertion} objects associated with the specified creative mode tab.
     * If no insertions are found for the given tab, an empty list is returned.
     *
     * <p>This method provides a way to fetch tab insertions for a specific tab in the creative menu, which can then be
     * applied or manipulated as needed. It will return an empty list if no insertions exist for the specified tab.</p>
     *
     * @param tab The {@link ResourceKey} of the {@link CreativeModeTab} for which insertions are being requested.
     * @return A list of {@link TabInsertion} objects associated with the specified tab, or an empty list if no insertions
     *         are found for that tab.
     */
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static List<TabInsertion> getInsertionsFor(ResourceKey<CreativeModeTab> tab) {
        return TAB_INSERTIONS.getOrDefault(tab, List.of());
    }

    /**
     * Reorders the tab insertions for a specific creative mode tab by performing a topological sort.
     * This method is called to ensure the proper order of insertions based on their dependencies.
     *
     * @param tab The {@link ResourceKey} for the {@link CreativeModeTab} whose insertions need to be reordered.
     */
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static void reorderTabInsertions(ResourceKey<CreativeModeTab> tab) {
        List<TabInsertion> list = TAB_INSERTIONS.get(tab);
        if (list == null) return;

        List<TabInsertion> sorted = new ArrayList<>();
        Set<TabInsertion> visited = new HashSet<>();
        // Starts a DFS traversal for each entry
        for (TabInsertion insertion : list)
            PaCoTabManager.visitInsertion(insertion, list, sorted, visited);

        list.clear();
        list.addAll(sorted);
    }

    /**
     * Performs a Depth-First Search (DFS) traversal on the list of tab insertions to determine their correct order.
     * This method adds insertions to the sorted list in the correct order based on their dependencies.
     *
     * @param insertion The current {@link TabInsertion} being processed.
     * @param all       The list of all {@link TabInsertion} entries to consider.
     * @param sorted    The list to store the sorted insertions in the correct order.
     * @param visited   A set that keeps track of which insertions have already been visited during the traversal.
     */
    private static void visitInsertion(TabInsertion insertion, List<TabInsertion> all, List<TabInsertion> sorted, Set<TabInsertion> visited) {
        if (visited.contains(insertion))
            return;
        visited.add(insertion);

        // Recursively visits all insertions that the current one depends on
        for (TabInsertion other : all)
            if (PaCoTabManager.dependsOn(insertion, other))
                PaCoTabManager.visitInsertion(other, all, sorted, visited);

        // After visiting dependencies, adds the current insertion to the sorted list
        sorted.add(insertion);
    }

    /**
     * Determines whether the current {@link TabInsertion} depends on another {@link TabInsertion}.
     * A tab insertion depends on another if the target of the current insertion is present in the list
     * of items inserted by the other insertion.
     *
     * @param insertion The {@link TabInsertion} being checked for dependencies.
     * @param other     The {@link TabInsertion} to check if it is a dependency of the first.
     * @return {@code true} if the current insertion depends on the other, {@code false} otherwise.
     */
    private static boolean dependsOn(TabInsertion insertion, TabInsertion other) {
        // Some early returns
        if (insertion == other) return false;
        if (!insertion.hasTarget()) return false;

        // Checks if insertion's target is among the inserted stacks from other
        if (other.isInserting(insertion.getTarget())) {
            // Safety check to prevent endless loops
            // If other depends on insertions we simply return false to keep insertion
            // in its current spot, which will cause it to get added to the end of the list
            return !insertion.isInserting(other.getTarget());
        }

        // If no matches are found insertion is not depending on other
        return false;
    }
}