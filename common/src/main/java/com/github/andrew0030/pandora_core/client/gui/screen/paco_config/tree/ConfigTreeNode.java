package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree;

import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

// TODO write javadocs
public class ConfigTreeNode {
    private final String name;
    private final ConfigTreeNode parent;
    private final Map<String, ConfigTreeNode> children = new LinkedHashMap<>();
    private ConfigDataHolder<?> dataHolder;
    private ConfigTreeNode lastChild;

    public ConfigTreeNode(String name, @Nullable ConfigTreeNode parent) {
        this.name = name;
        this.parent = parent;
    }

    public ConfigTreeNode(String name) {
        this(name, null);
    }

    public String getName() {
        return this.name;
    }

    public Collection<ConfigTreeNode> getChildren() {
        return this.children.values();
    }

    /** @return The last child {@link ConfigTreeNode}, or {@code null} if this node has no children */
    public ConfigTreeNode getLastChild() {
        return this.lastChild;
    }

    public ConfigTreeNode getParent() {
        return this.parent;
    }

    /** @return Whether this {@link ConfigTreeNode} has a {@link ConfigDataHolder} with a corresponding {@code field} */
    public boolean isValue() {
        return dataHolder != null && !dataHolder.isCategory();
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
        return this.children.computeIfAbsent(name, childName -> this.lastChild = new ConfigTreeNode(childName, this));
    }

    public ConfigDataHolder<?> getDataHolder() {
        return this.dataHolder;
    }

    public void setDataHolder(ConfigDataHolder<?> dataHolder) {
        this.dataHolder = dataHolder;
    }
}