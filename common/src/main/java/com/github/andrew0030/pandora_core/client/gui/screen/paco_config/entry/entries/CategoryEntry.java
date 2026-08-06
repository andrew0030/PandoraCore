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
        CategoryButton widget = new CategoryButton(x, y, width, height, Component.literal("TODO"), node, screen); //TODO implement proper narration
        this.widgets.add(widget);
    }

    private static class CategoryButton extends AbstractWidget {
        private final ConfigTreeNode node;
        private final PaCoConfigScreen screen;

        public CategoryButton(int x, int y, int width, int height, Component message, ConfigTreeNode node, PaCoConfigScreen screen) {
            super(x, y, width, height, message);
            this.node = node;
            this.screen = screen;
        }

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