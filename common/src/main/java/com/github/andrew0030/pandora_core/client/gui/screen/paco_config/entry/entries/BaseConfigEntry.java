package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries;

import com.github.andrew0030.pandora_core.client.gui.buttons.ConfigEntryNavigationButton;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolder;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.easing.Easing;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
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
// - Maybe add option to choose if a tooltip should favor top/bottom displaying
public abstract class BaseConfigEntry implements Renderable {
    private static final int SLICE_SIZE = 3;
    protected final PaCoConfigScreen screen;
    protected final ConfigTreeNode node;
    private final int x, y, width, height;
    protected final List<AbstractWidget> widgets = new ArrayList<>();
    protected boolean isHovered;
    protected boolean isFocused;
    protected boolean isInBounds;
    protected boolean isVisible;
    // Components
    protected final Component entryKey;
    protected final List<Component> entryTooltip = new ArrayList<>();
    protected final int tooltipHeight;
    // Animation & Fade-in
    private static final int TOOLTIP_DELAY_MS = 500;
    private static final int TEXT_ANIMATION_SPEED_MS = 300; // Note: above 0 to prevent divided by 0 exceptions
    private static final int TEXT_MOVEMENT_DISTANCE = 2;
    protected float hoverAnimationProgress = 0.0F;
    protected long lastUpdateTime = Util.getMillis();
    protected long hoverTime;
    // Navigation
    private ConfigEntryNavigationButton navButton; // TODO maybe remove this if I deem entry movement through nav panel overkill

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
        // Hover Logic
        this.isInBounds = this.screen.menuHeightStop >= this.getY() && this.screen.menuHeightStart < this.getY() + this.getHeight();
        boolean mouseInBounds = this.screen.isMouseInEntriesBounds(mouseX, mouseY);
        boolean isHovered = mouseX >= this.getX() && mouseX < this.getX() + this.getWidth() &&
                            mouseY >= this.getY() - 1 && mouseY < this.getY() + this.getHeight() + 1; // NOTE: -+1 so there are no gaps between buttons when hovering them
        this.isHovered = (this.navButton != null && this.navButton.isHoveredOrFocused()) || (mouseInBounds && isHovered);

        // No rendering is needed when the entry is out of bounds
        if (!this.isInBounds) {
            this.hoverAnimationProgress = 0F;
            return;
        }

        // Animation Times
        long currentTime = Util.getMillis();
        long deltaTime = Math.min(currentTime - this.lastUpdateTime, 100L); // Prevents huge animation jumps, if the game was paused
        this.lastUpdateTime = currentTime;

        // Animation Progress
        boolean isActive = this.isHoveredOrFocused();
        if (isActive) {
            if (this.hoverTime == 0) this.hoverTime = Util.getMillis();
            this.hoverAnimationProgress += deltaTime * (1F / TEXT_ANIMATION_SPEED_MS);
            if (this.hoverAnimationProgress > 1F) this.hoverAnimationProgress = 1F;
        } else {
            if (this.hoverTime != 0) hoverTime = 0;
            this.hoverAnimationProgress -= deltaTime * (1F / TEXT_ANIMATION_SPEED_MS);
            if (this.hoverAnimationProgress < 0F) this.hoverAnimationProgress = 0F;
        }
        float slideOffset = this.hoverAnimationProgress * TEXT_MOVEMENT_DISTANCE;

        // Element Background
        RenderSystem.enableBlend();
        graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 48, 122, 48, 48);
        RenderSystem.setShaderColor(1, 1, 1, isActive ? Easing.CUBIC_IN.apply(this.hoverAnimationProgress) : 1);
        // Background Highlight & Bars
        if (isActive) {
            graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 96, 122, 48, 48);
            int posY = this.getY() - 1;
            graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX(), posY, this.getWidth(), 1, 144, 122, 48, 1);
            posY += this.getHeight() + 1;
            graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX(), posY, this.getWidth(), 1, 144, 122, 48, 1);
        }
        RenderSystem.setShaderColor(1, 1, 1, 1);
        // Config Key
        graphics.pose().pushPose();
        graphics.pose().translate(this.getX() + PaCoConfigScreen.PADDING_FOUR + slideOffset, this.getY() + PaCoConfigScreen.PADDING_FOUR, 1);
        graphics.drawString(Minecraft.getInstance().font, this.entryKey, 0, 0, PaCoColor.WHITE, false);
        graphics.pose().popPose();




        // TODO: maybe move/change this to utilize minecraft's built in widget rendering ?
        // Widgets
        this.widgets.forEach(widget -> widget.render(graphics, mouseX, mouseY, partialTick));

        // Debug Outline
//        PaCoGuiUtils.renderBoxWithRim(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null, PaCoColor.color(255, 40, 40), 1);
    }

    /** @return Whether to render the tooltip */
    public boolean shouldRenderTooltip() {
        if (this.hoverTime == 0) return false;
        return this.hoverTime + TOOLTIP_DELAY_MS < this.lastUpdateTime;
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
        // Blur behind tooltip
        PaCoGuiUtils.blurRect(graphics, 10F, this.getX(), posY, this.getWidth(), this.getTooltipHeight(), partialTick);
        // Tooltip
        PaCoGuiUtils.renderFixedTooltipNineSliced(
                graphics, Minecraft.getInstance().font, this.entryTooltip, this.getX(), posY, this.getWidth(),
                PaCoScreen.TEXTURE, SLICE_SIZE, 144, 122, 48, 48
        );
    }

    // TODO write javadocs for the methods bellow

    public void tick() {}

    // TODO maybe handle navButton logic in a cleaner way?
    public void onPress() {
        // Moves the config entry into the menu panel bounds when focused
        this.moveEntryIntoFocus(false);
    }

    public List<AbstractWidget> getWidgets() {
        return this.widgets;
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    public boolean isFocused() {
        boolean wasFocused = this.isFocused;
        this.isFocused = false;
        for (AbstractWidget widget : this.widgets) {
            if (widget.isFocused()) {
                this.isFocused = true;
                break;
            }
        }
        // Moves the config entry into the menu panel bounds when focused
        if (this.isFocused && !wasFocused)
            this.moveEntryIntoFocus(false);

        return this.isFocused;
    }

    public boolean isHoveredOrFocused() {
        // NOTE: The isFocused check needs to be called first. The reason is that the check performs
        // state changes internally, and Java would skip it if isHovered returned true before the OR
        return this.isFocused() || this.isHovered();
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

    public ConfigTreeNode getNode() {
        return this.node;
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

    /** @return Whether this entry is within the bounds of the entries panel */
    public boolean isInBounds() {
        return this.isInBounds;
    }

    /** @return The component representing the config entry key */
    public Component getEntryKey() {
        return this.entryKey;
    }

    // TODO implement resizing logic in PaCoConfigScreen when more navigation logic is added?
    /** Moves the {@link BaseConfigEntry} up/down and adds padding if needed to avoid the gradient, or being hidden post resizing the {@link PaCoConfigScreen}. */
    public void moveEntryIntoFocus(boolean moveToTop) {
        if (this.screen.entriesScrollBar == null) return;
        if (!this.isVisible) return;
        int padding = 16; // We use padding because the gradient would interfere with the buttons otherwise.
        if (this.getY() < this.screen.menuHeightStart + padding) { // Top Area
            int pixels = this.screen.menuHeightStart - this.getY();
            this.screen.entriesScrollBar.setValue(this.screen.entriesScrollBar.getValue() - (pixels + padding));
        } else if (this.getY() + this.getHeight() > this.screen.menuHeightStop - padding) { // Bottom Area
            int pixels = this.getY() + this.getHeight() - this.screen.menuHeightStop;
            this.screen.entriesScrollBar.setValue(this.screen.entriesScrollBar.getValue() + (pixels + padding));
            // Used to move the button to the top of the list if possible
            if (moveToTop)
                this.screen.entriesScrollBar.setValue(this.screen.entriesScrollBar.getValue() + (this.screen.menuHeight - this.getHeight() - padding * 2));
        }
    }

    public void setNavButton(ConfigEntryNavigationButton button) {
        this.navButton = button;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isVisible() {
        return this.isVisible;
    }
}