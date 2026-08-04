package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//TODO Some adjustments to config entries that might be needed/useful
// - Make the rendering more strict
// - Allow custom heights
// - Abstract value retrieval to avoid direct interaction with the config instance
// - Add bulk modification logic, rather than saving the config each time
public abstract class BaseConfigEntry implements Renderable {
    protected final PaCoConfigScreen screen;
    protected final ConfigTreeNode node;
    protected final int x, y, width, height;
    protected final List<AbstractWidget> widgets = new ArrayList<>();
    protected boolean isHovered;

    public BaseConfigEntry(PaCoConfigScreen screen, ConfigTreeNode node, int x, int y, int width, int height) {
        this.screen = screen;
        this.node= node;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        // TODO: Add the PaCoGuiUtils check that determines if the entry is within the bounds of the UI
        this.isHovered = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;

        // Element Background
        RenderSystem.enableBlend();
        graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.x, this.y, this.width, this.height, 0, 122, 48, 48);
        // Config Key
        graphics.drawString(Minecraft.getInstance().font, this.node.getName(), this.x + PaCoConfigScreen.PADDING_FOUR, this.y + PaCoConfigScreen.PADDING_FOUR, PaCoColor.WHITE, false);


        // TODO: maybe move/change this to utilize minecraft's built in widget rendering ?
        this.widgets.forEach(widget -> widget.render(graphics, mouseX, mouseY, partialTick));


        // TODO maybe move this if GL scissors messes with the tooltip
        // TODO implement focus/hover logic so keyboard navigation also triggers tooltips
        if (this.isHovered()) {
            // TODO remove this short lived list, instead the tooltip components should be retrieved on init and reused
            ArrayList<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal(this.node.getDataHolder().getComment()));

//            tooltip.add(Component.literal("This is a test comment"));
//            tooltip.add(Component.literal("will"));
//            tooltip.add(Component.literal("this"));
//            tooltip.add(Component.literal("work?"));
//            tooltip.add(Component.literal("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));

            PaCoGuiUtils.renderFixedTooltipNineSliced(
                    graphics, Minecraft.getInstance().font, tooltip, this.x, this.y + this.height, this.width,
                    PaCoScreen.TEXTURE, 3, 150, 72, 75, 25
            );
        }
    }

    // TODO write javadocs for the methods bellow

    public void tick() {}

    public List<AbstractWidget> getWidgets() {
        return this.widgets;
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    public boolean isFocused() {
        for (AbstractWidget widget : this.widgets)
            if (widget.isFocused()) return true;
        return false;
    }

    public boolean isHoveredOrFocused() {
        return this.isHovered() || this.isFocused();
    }
}