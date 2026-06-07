package com.github.andrew0030.pandora_core.utils.toasts.icon;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class PaCoAnimatedIcon extends PaCoIcon{
	int numFrames;
	int fps;
	int numIconsPerRow;
	
	public static final PaCoAnimatedIcon LOADING = new PaCoAnimatedIcon(
			"loading", PaCoIcon.PACO_GENERIC_ICONS,
			0, 0,
			20, 20,
			80, 80,
			8, 8, 4
	);
	
	public PaCoAnimatedIcon(String name, ResourceLocation guiTex, int pixelX, int pixelY, int width, int height, int numFrames, int fps, int numIconsPerRow) {
		super(name, guiTex, pixelX, pixelY, width, height);
		this.numFrames = numFrames;
		this.fps = fps;
		this.numIconsPerRow = numIconsPerRow;
	}
	
	public PaCoAnimatedIcon(String name, ResourceLocation guiTex, int pixelX, int pixelY, int width, int height, int spriteWidth, int spriteHeight, int numFrames, int fps, int numIconsPerRow) {
		super(name, guiTex, pixelX, pixelY, width, height, spriteWidth, spriteHeight);
		this.numFrames = numFrames;
		this.fps = fps;
		this.numIconsPerRow = numIconsPerRow;
	}
	
	public PaCoAnimatedIcon(String name, ResourceLocation guiTex, int pixelX, int pixelY, int width, int height, int displayWidth, int displayHeight, int spriteWidth, int spriteHeight, int numFrames, int fps, int numIconsPerRow) {
		super(name, guiTex, pixelX, pixelY, width, height, displayWidth, displayHeight, spriteWidth, spriteHeight);
		this.numFrames = numFrames;
		this.fps = fps;
		this.numIconsPerRow = numIconsPerRow;
	}
	
	@Override
	public void blit(
			GuiGraphics guiGraphics,
			int x, int y,
			long displayTime, long activeTime
	) {
		int iconWidth = spriteWidth / numIconsPerRow;
		int iconIndex = pixelX / iconWidth;
		
		double divisor = 1000d / fps;
		int frame = (int) (displayTime / divisor);
		frame %= numFrames;
		iconIndex = iconIndex + frame;
		
		int iconYOff = iconIndex / 4;
		int iconX = iconIndex % 4;
		
		int coordX = iconX * iconWidth;
		int coordY = iconYOff * iconWidth + pixelY;
		
		guiGraphics.blit(
				guiTex, x, y,
				displayWidth, displayHeight,
				coordX, coordY, width, height,
				spriteWidth, spriteHeight
		);
	}
}
