package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class UnsupportedEntry extends BaseConfigEntry {
    public UnsupportedEntry(PaCoConfigScreen screen, ConfigTreeNode node, int x, int y, int width, int height) {
        super(screen, node, x, y, width, height);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        // TODO make this entire entry fancier
        Font font = Minecraft.getInstance().font;
        graphics.drawString(Minecraft.getInstance().font, "Unsupported", this.x + this.width - font.width("Unsupported") - PaCoConfigScreen.PADDING_TWO, this.y + PaCoConfigScreen.PADDING_FOUR, PaCoColor.color(200, 80, 80), false);
    }
}