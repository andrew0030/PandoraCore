package com.github.andrew0030.pandora_core.tab;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class TabInsertionManager {
    static final Map<ResourceKey<CreativeModeTab>, List<TabInsertion>> TAB_INSERTIONS = new HashMap<>();

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

        // Splits insertions into simple (no dependencies) and dependent
        var partitioned = list.stream().collect(Collectors.partitioningBy(TabInsertion::isTargetingInsertion));
        List<TabInsertion> simple = partitioned.get(false);
        List<TabInsertion> dependent = partitioned.get(true);

        List<TabInsertion> sortedDependent = new ArrayList<>();
        Set<TabInsertion> visited = new HashSet<>();
        // Starts a DFS traversal for each entry that has a dependency
        for (TabInsertion insertion : dependent)
            TabInsertionManager.visitInsertion(insertion, dependent, sortedDependent, visited);

        list.clear();
        // We insert simple entries first so that dependent entries can reference them reliably
        list.addAll(simple);
        list.addAll(sortedDependent);
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
            if (TabInsertionManager.dependsOn(insertion, other))
                TabInsertionManager.visitInsertion(other, all, sorted, visited);

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

    /**
     * Applies all {@link TabInsertion} objects to the given list of {@link ItemStack ItemStacks}.
     * <p>
     * Insertions are applied relative to their targets, which may be items in the original list
     * or items added by other insertions. Targets not found will result in fallback insertion
     * at the end of the list. Insertions are assumed to be pre-sorted to resolve dependencies.
     *
     * @param list       The original list of {@link ItemStack ItemStacks} in the tab; will be modified in-place.
     * @param insertions The list of {@link TabInsertion TabInsertions} to apply.
     */
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static void applyAllInsertions(List<ItemStack> list, List<TabInsertion> insertions) {
        List<ItemStack> result = new ArrayList<>(list.size() + insertions.size());
        Map<ItemKey, Integer> preciseIndex = new HashMap<>(); // For exact item+nbt targeting
        Map<Item, Integer> firstIndex = new HashMap<>(); // First occurrence of item (ignoring NBT)
        Map<Item, Integer> lastIndex = new HashMap<>();  // Last occurrence of item (ignoring NBT)

        // Copies the original list and builds the index maps
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = list.get(i);
            result.add(stack);

            if (stack.hasTag())
                preciseIndex.putIfAbsent(ItemKey.of(stack), i);

            Item item = stack.getItem();
            firstIndex.putIfAbsent(item, i);
            lastIndex.put(item, i);
        }

        // Loops over all insertions and adds the items to the list, based on the target index of the insertion
        // Following that the index maps are updated with the inserted items
        for (TabInsertion insertion : insertions) {
            List<ItemStack> stacks = insertion.getStacks();
            // If no stacks need to be inserted we return early, no need to run more logic...
            if (stacks.isEmpty()) continue;
            // Determines where to insert items, and then inserts them
            int insertPos = TabInsertionManager.determineInsertionPosition(insertion, preciseIndex, firstIndex, lastIndex, result.size());
            result.addAll(insertPos, stacks);

            // Updates the index maps for each inserted stack
            for (int i = 0; i < stacks.size(); i++) {
                ItemStack stack = stacks.get(i);
                int absolutePos = insertPos + i;

                if (stack.hasTag())
                    preciseIndex.putIfAbsent(ItemKey.of(stack), absolutePos);

                Item item = stack.getItem();
                firstIndex.putIfAbsent(item, absolutePos);
                lastIndex.put(item, absolutePos);
            }
        }

        // Overwrites original list
        list.clear();
        list.addAll(result);
    }

    /**
     * Determines the index at which a {@link TabInsertion} should insert its items
     * based on its target and insertion direction (before/after).
     *
     * @param insertion        The {@link TabInsertion} to evaluate.
     * @param preciseIndex     Map containing the index of each item with nbt.
     * @param firstIndex       Map of the first index of each item in the tab.
     * @param lastIndex        Map of the last index of each item in the tab.
     * @param fallbackPosition The fallback position to use if the target is not found.
     * @return The index at which the insertion's items should be inserted.
     */
    private static int determineInsertionPosition(TabInsertion insertion, Map<ItemKey, Integer> preciseIndex, Map<Item, Integer> firstIndex, Map<Item, Integer> lastIndex, int fallbackPosition) {
        if (!insertion.hasTarget())
            return fallbackPosition;
        ItemStack target = insertion.getTarget();
        if (target.hasTag()) {
            Integer pos = preciseIndex.get(ItemKey.of(target));
            return (pos != null)
                    ? (insertion.isInsertBefore() ? pos : pos + 1)
                    : fallbackPosition;
        } else {
            Item item = target.getItem();
            Integer pos = insertion.isInsertBefore()
                    ? firstIndex.get(item)
                    : lastIndex.get(item);
            return (pos != null)
                    ? (insertion.isInsertBefore() ? pos : pos + 1)
                    : fallbackPosition;
        }
    }
}