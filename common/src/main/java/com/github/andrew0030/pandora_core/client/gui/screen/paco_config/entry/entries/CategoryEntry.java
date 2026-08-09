package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class CategoryEntry extends BaseConfigEntry {

    public CategoryEntry(PaCoConfigScreen screen, ConfigTreeNode node, int x, int y, int width, int height) {
        super(screen, node, x, y, width, height);
        Button widget = new Button(this, Component.literal("TODO")); //TODO implement proper narration
        this.widgets.add(widget);
    }

    private static class Button extends AbstractWidget {
        private final BaseConfigEntry entry;
        private final ConfigTreeNode node;
        private final PaCoConfigScreen screen;

        public Button(BaseConfigEntry entry, Component message) {
            super(entry.getX(), entry.getY(), entry.getWidth(), entry.getHeight(), message);
            this.entry = entry;
            this.node = entry.node;
            this.screen = entry.screen;
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
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            // TODO replace this with a more proper category
            graphics.drawString(Minecraft.getInstance().font, ">",
                    this.getX() + this.width - 15,
                    this.getY() + (this.height / 2) - 4,
                    PaCoColor.WHITE, false);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            // TODO look into a cleaner way to get the title screen
            PaCoConfigScreen subScreen = new PaCoConfigScreen(this.screen.getManager(), this.node, screen.titleScreen, screen);
            Minecraft.getInstance().setScreen(subScreen);
        }
    }
}