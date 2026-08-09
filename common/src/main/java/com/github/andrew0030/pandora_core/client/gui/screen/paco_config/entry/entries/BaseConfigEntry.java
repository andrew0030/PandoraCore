package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolder;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO Some adjustments to config entries that might be needed/useful
// - Make the rendering more strict
// - Allow custom heights
// - Abstract value retrieval to avoid direct interaction with the config instance
// - Add bulk modification logic, rather than saving the config each time
public abstract class BaseConfigEntry implements Renderable {
    private static final int SLICE_SIZE = 3;
    protected final PaCoConfigScreen screen;
    protected final ConfigTreeNode node;
    private final int x, y, width, height;
    protected final List<AbstractWidget> widgets = new ArrayList<>();
    protected boolean isHovered;
    // TODO maybe make protected or add getters
    private final Component entryKey;
    private final List<Component> entryTooltip = new ArrayList<>();
    private final int tooltipHeight;

    public BaseConfigEntry(PaCoConfigScreen screen, ConfigTreeNode node, int x, int y, int width, int height) {
        this.screen = screen;
        this.node= node;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        ConfigDataHolder holder = this.node.getDataHolder();
        this.entryKey = holder.getKeyComponent() != null ? holder.getKeyComponent() : Component.literal(this.node.getName());
        // The user specified tooltip
        holder.getTooltipComponents().forEach(component -> {
            // Checks if a color was explicitly specified
            if (component.getStyle().getColor() == null) {
                // If no color was specified we set it to light gray
                this.entryTooltip.add(component.copy().withStyle(style -> style.withColor(ChatFormatting.GRAY)));
            } else {
                // If a color was specified we render the component as is
                this.entryTooltip.add(component);
            }
        });
        // Fallback tooltip
        if (this.entryTooltip.isEmpty() && !StringUtil.isNullOrEmpty(holder.getCommentRaw())) {
            // TODO maybe avoid modifying the lines?
            List<String> commentLines = Arrays.stream(holder.getCommentRaw().trim().split("\n")).map(String::trim).toList();
            commentLines.forEach(string -> this.entryTooltip.add(Component.literal(string).withStyle(style -> style.withColor(ChatFormatting.GRAY))));
        }


        //TODO maybe omit this if no other entries exist in the tooltip?
        // The entries path
        this.entryTooltip.add(Component.literal(holder.getPath()).withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY)));
        

        this.tooltipHeight = PaCoGuiUtils.getTooltipHeight(Minecraft.getInstance().font, this.entryTooltip, this.getWidth(), SLICE_SIZE);
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
        graphics.drawString(Minecraft.getInstance().font, this.entryKey, this.getX() + PaCoConfigScreen.PADDING_FOUR, this.getY() + PaCoConfigScreen.PADDING_FOUR, PaCoColor.WHITE, false);


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
        int posY = this.renderTooltipBelow() ? this.getY() + this.getHeight() : this.getY() - this.tooltipHeight;
        PaCoGuiUtils.renderFixedTooltipNineSliced(
                graphics, Minecraft.getInstance().font, this.entryTooltip, this.getX(), posY, this.getWidth(),
                PaCoScreen.TEXTURE, SLICE_SIZE, 150, 72, 75, 25
        );
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

    public int getTooltipHeight() {
        return this.tooltipHeight;
    }

    /** @return Whether to render the tooltip under the config entry */
    public boolean renderTooltipBelow() {
        int tooltipHeight = this.getTooltipHeight();
        int entryTop = this.getY();
        int entryBottom = entryTop + this.getHeight();
        int spaceAbove = entryTop - this.screen.menuHeightStart;
        int spaceBelow = this.screen.menuHeightStop - entryBottom;
        boolean renderBelow;
        // Checks if the tooltip fits below
        if (tooltipHeight <= spaceBelow)
            renderBelow = true;
        // Checks if the tooltip fits above
        else if (tooltipHeight <= spaceAbove)
            renderBelow = false;
        // If the tooltip doesn't fit anywhere, we place it based on vertical position
        else {
            // Calculates the center of the entries panel
            int menuCenter = this.screen.menuHeightStart + (this.screen.menuHeightStop - this.screen.menuHeightStart) / 2;
            // If the entry is in the top half we render the tooltip below
            // If the entry in the bottom half we render the tooltip above
            renderBelow = entryTop < menuCenter;
        }
        return renderBelow;
    }
}