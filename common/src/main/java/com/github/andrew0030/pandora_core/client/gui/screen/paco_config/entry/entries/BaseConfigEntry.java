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
    private final int x, y, width, height;
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
        boolean mouseInBounds = this.screen.isMouseInEntriesBounds(mouseX, mouseY);
        boolean isHovered = mouseX >= this.getX() && mouseX < this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();
        this.isHovered = mouseInBounds && isHovered;

        // Element Background
        RenderSystem.enableBlend();
        graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0, 122, 48, 48);
        // Config Key
        graphics.drawString(Minecraft.getInstance().font, this.node.getName(), this.getX() + PaCoConfigScreen.PADDING_FOUR, this.getY() + PaCoConfigScreen.PADDING_FOUR, PaCoColor.WHITE, false);


        // TODO: maybe move/change this to utilize minecraft's built in widget rendering ?
        this.widgets.forEach(widget -> widget.render(graphics, mouseX, mouseY, partialTick));
    }

    /**
     * Used to render the tooltip of this {@link BaseConfigEntry}.
     * <p>
     * NOTE: The reason tooltips are rendered in a separate method is, so we can avoid
     * cutting them with {@code GLScissors} that get applied to the normal render method.
     * </p>
     */
    public void renderTooltip(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.isHovered()) {
            // TODO remove this short lived list, instead the tooltip components should be retrieved on init and reused
            ArrayList<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal(this.node.getDataHolder().getComment()));

            // TODO implement focus/hover logic so keyboard navigation also triggers tooltips
            PaCoGuiUtils.renderFixedTooltipNineSliced(
                    graphics, Minecraft.getInstance().font, tooltip, this.getX(), this.getY() + this.getHeight(), this.getWidth(),
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

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y + this.getScrollOffset();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    /**
     * Should be used by super classes to apply an offset to the
     * {@code getY} method of {@link AbstractWidget} instances.
     *
     * @return The offset that should be applied on the Y-axis
     */
    protected int getScrollOffset() {
        if (this.screen.entriesScrollBar == null) return 0;
        return -Math.round((float) this.screen.entriesScrollBar.getValue());
    }
}