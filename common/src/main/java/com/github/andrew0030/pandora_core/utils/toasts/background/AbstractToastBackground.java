package com.github.andrew0030.pandora_core.utils.toasts.background;

import net.minecraft.client.gui.GuiGraphics;

public abstract class AbstractToastBackground {
	public abstract boolean canBeResized();
	
	public void blit(GuiGraphics guiGraphics, int width, int height) {
		blit(guiGraphics);
	}
	
	public abstract void blit(GuiGraphics guiGraphics);
}
