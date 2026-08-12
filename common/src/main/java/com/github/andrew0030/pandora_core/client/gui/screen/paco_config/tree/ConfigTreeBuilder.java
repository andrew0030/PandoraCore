package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree;

import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolder;

import java.util.Collection;

public class ConfigTreeBuilder {

    // TODO: ConfigDataHolderCategories should only be added if there are values within, to avoid empty UIs
    /**
     * Constructs a hierarchical {@link ConfigTreeNode} tree from a flat collection of {@link ConfigDataHolder} instances.
     *
     * @param holders A flat collection of {@link ConfigDataHolder} instances, used to populate the tree
     * @return The root {@link ConfigTreeNode} representing the fully constructed hierarchical {@link ConfigTreeNode} tree
     */
    public static ConfigTreeNode buildTree(Collection<ConfigDataHolder> holders) {
        // The base node that will get populated with all other nodes
        ConfigTreeNode root = new ConfigTreeNode("root");
        // Loops over the given data holders and populates the root node with children
        for (ConfigDataHolder holder : holders) {
            String path = holder.getPath();
            ConfigTreeNode current = root;
            // Keeps track of the relevant indexes, as this is faster than splitting the string
            int start = 0;
            int dotIdx = path.indexOf('.');
            // Traverses and creates intermediate branches
            while (dotIdx != -1) {
                current = current.getOrCreateChild(path.substring(start, dotIdx));
                start = dotIdx + 1;
                dotIdx = path.indexOf('.', start);
            }
            // Attaches the data holder to the final node of a branch
            current.getOrCreateChild(path.substring(start)).setDataHolder(holder);
        }
        // Lastly the now populated root node is returned
        return root;
    }
}