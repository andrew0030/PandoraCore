package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries;

import com.github.andrew0030.pandora_core.client.gui.edit_boxes.PaCoEditBox;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.config.manager.IConfigValueHolder;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class NumberEntry<T extends Comparable<T>> extends BaseConfigEntry<T> {
    private static final int NORMAL_TEXT_COLOR = 14737632;
    private static final int RED_TEXT_COLOR = PaCoColor.color(235, 74, 74);
    private final TextField<T> widget;

    public NumberEntry(PaCoConfigScreen screen, ConfigTreeNode node, int y, int height, boolean hasScrollBar, Function<String, T> parser) {
        super(screen, node, y, height, hasScrollBar);
        // Creates the interactable widget
        // TODO maybe improve what kind of data is passed to the widgets? Something to look into after more of the types are implemented!
        this.widget = new TextField<>(this, parser, Component.literal("TODO")); //TODO fix narration
        this.widget.setForceLineIndicator(true);
        this.widget.setMidpointCharSelection(true);
//        this.widget.setBackgroundHidden(true);
//        this.widget.setRimHidden(true);
        // Sets the value to the current value from the config
        this.widget.setValue(this.getValue().toString());
        // Lastly we add the widget to the list
        this.widgets.add(this.widget);
    }

    @Override
    public void tick() {
        this.widget.tick();
    }

    /** Checks if the parsed value is within the min/max range. */
    @SuppressWarnings("unchecked")
    private boolean isInRange(T value) {
        if (!this.holder.hasValue()) return false;
        IConfigValueHolder<T> valueHolder = (IConfigValueHolder<T>) this.holder;
        T min = valueHolder.getMinVal();
        T max = valueHolder.getMaxVal();

        if (min != null && value.compareTo(min) < 0) return false;
        if (max != null && value.compareTo(max) > 0) return false;
        return true;
    }

    private static class TextField<T extends Comparable<T>> extends PaCoEditBox {
        private final NumberEntry<T> entry;
        private final Function<String, T> parser;

        public TextField(NumberEntry<T> entry, Function<String, T> parser, Component message) {
            super(Minecraft.getInstance().font, entry.getX() + entry.getWidth() - 79, entry.getY() + 1, 78, entry.getHeight() - 2, message);
            this.entry = entry;
            this.parser = parser;
        }

        // NOTE: The code in this block should be implemented by all widgets used for config entries, the methods
        //       ensure that the widgets move along the config entries, and aren't clickable when out of bounds!
        // #########################################################################################################
        @Override
        public int getY() {
            return super.getY() + this.entry.getScrollOffset();
        }
        @Override
        public boolean isHovered() {
            return this.entry.isHovered() && super.isHovered();
        }
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.entry.screen.isMouseInEntriesBounds(mouseX, mouseY) && super.mouseClicked(mouseX, mouseY, button);
        }
        // #########################################################################################################


        @Override
        public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(graphics, mouseX, mouseY, partialTick);

//            RenderSystem.enableBlend();
//            int textLength = this.font.width(this.getValue());
//            graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX() + 4, this.getY(), textLength, this.getHeight(), 48, 122, 48, 48);
        }

        @Override
        public void onTextChanged(String newText) {
            // Early exit if the input is empty or just the start of a negative number
            if (newText.isEmpty() || newText.equals("-")) {
                this.setTextColor(NumberEntry.RED_TEXT_COLOR);
                return;
            }
            // Tries to parse the given value and checks if it's in bounds
            try {
                T value = this.parser.apply(newText);
                if (!this.entry.isInRange(value)) {
                    this.setTextColor(NumberEntry.RED_TEXT_COLOR);
                    return;
                }
                // If the given value was parsed properly and is within the bounds, it's added as a pending change
                this.setTextColor(NumberEntry.NORMAL_TEXT_COLOR);
                this.entry.setValue(value);
            } catch (NumberFormatException e) {
                this.setTextColor(NumberEntry.RED_TEXT_COLOR);
            }
        }
    }
}