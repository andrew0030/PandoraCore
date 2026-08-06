package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree;

import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

// TODO write javadocs
public class ConfigTreeNode {
    private final String name;
    private ConfigDataHolder dataHolder;
//    private ConfigTreeNode parent;
    private final Map<String, ConfigTreeNode> children = new LinkedHashMap<>();

    public ConfigTreeNode(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public ConfigDataHolder getDataHolder() {
        return this.dataHolder;
    }

    public void setDataHolder(ConfigDataHolder dataHolder) {
        this.dataHolder = dataHolder;
    }

    public Collection<ConfigTreeNode> getChildren() {
        return this.children.values();
    }

    // TODO: maybe reintroduce the parent if its needed for easy UI navigation?
//    public void addChild(ConfigTreeNode child) {
//        child.parent = this;
//        this.children.put(child.getName(), child);
//    }

//    public ConfigTreeNode getParent() {
//        return this.parent;
//    }

//    public ConfigTreeNode getChild(String name) {
//        return this.children.get(name);
//    }

    /** @return Whether this {@link ConfigTreeNode} has a {@link ConfigDataHolder} with a corresponding {@code field} */
    public boolean isValue() {
        return dataHolder != null && dataHolder.hasField();
    }

    /**
     * Retrieves an existing child node by name, or creates and adds a new one if it doesn't exist.
     * <p>
     * <strong>NOTE:</strong> This is packet private, as only the {@link ConfigTreeBuilder} should
     * be populating the {@code children} of a {@link ConfigTreeNode} instance.
     * </p>
     *
     * @param name The name of the child node that will be retrieved or created
     * @return The {@link ConfigTreeNode} instance that was retrieved or created
     */
    ConfigTreeNode getOrCreateChild(String name) {
        ConfigTreeNode node = this.children.get(name);
        if (node == null)
            this.children.put(name, node = new ConfigTreeNode(name));
        return node;
    }
}