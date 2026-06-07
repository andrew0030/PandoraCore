package com.github.andrew0030.pandora_core.utils.toasts.icon;

import com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection.ModImageManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class PaCoIcon {
	public static final ResourceLocation PACO_GENERIC_ICONS = new ResourceLocation("pandora_core:textures/gui/toast/toast_icons.png");
	
	public static final PaCoIcon ERROR = new PaCoIcon(
			"warning", PACO_GENERIC_ICONS,
			0 * 20, 2 * 20,
			20, 20,
			20 * 4, 20 * 4
	);
	public static final PaCoIcon WARNING = new PaCoIcon(
			"warning", PACO_GENERIC_ICONS,
			1 * 20, 2 * 20,
			20, 20,
			20 * 4, 20 * 4
	);
	
	private static final ResourceLocation PACO_LOGO = new ResourceLocation("pandora_core:textures/gui/pandora_core.png");
	private static final ResourceLocation VANILLA_LOGO = new ResourceLocation("pandora_core:textures/gui/mc_mod_icon.png");
	private static final ResourceLocation FORGE_LOGO = new ResourceLocation("pandora_core:textures/gui/forge_mod_icon.png");
	private static final ResourceLocation FABRIC_LOGO = new ResourceLocation("pandora_core:textures/gui/missing_mod_icon.png");
	
	// mod logos
	public static final PaCoIcon PACO = new PaCoIcon(
			"paco", PACO_LOGO,
			0, 0,
			23, 23,
			23, 23
	);
	public static final PaCoIcon VANILLA_20x20 = new PaCoIcon(
			"vanilla", VANILLA_LOGO,
			0, 0,
			23, 23,
			20, 20,
			23, 23
	);
	public static final PaCoIcon FORGE_20x20 = new PaCoIcon(
			"forge", FORGE_LOGO,
			0, 0,
			23, 23,
			20, 20,
			23, 23
	);
	public static final PaCoIcon FABRIC_20x20 = new PaCoIcon(
			"fabric", FABRIC_LOGO,
			0, 0,
			23, 23,
			20, 20,
			23, 23
	);
	
	protected final String name;
	protected final ResourceLocation guiTex;
	protected final int pixelX, pixelY;
	protected final int width, height;
	protected final int displayWidth, displayHeight;
	
	protected final int spriteWidth, spriteHeight;
	
	public PaCoIcon(String name, ResourceLocation guiTex, int pixelX, int pixelY, int width, int height) {
		this.name = name;
		this.guiTex = guiTex;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.width = width;
		this.height = height;
		displayWidth = width;
		displayHeight = height;
		spriteWidth = 256;
		spriteHeight = 256;
	}
	
	public PaCoIcon(String name, ResourceLocation guiTex, int pixelX, int pixelY, int width, int height, int spriteWidth, int spriteHeight) {
		this.name = name;
		this.guiTex = guiTex;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.width = width;
		this.height = height;
		displayWidth = width;
		displayHeight = height;
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
	}
	
	public PaCoIcon(String name, ResourceLocation guiTex, int pixelX, int pixelY, int width, int height, int displayWidth, int displayHeight, int spriteWidth, int spriteHeight) {
		this.name = name;
		this.guiTex = guiTex;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.width = width;
		this.height = height;
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
	}
	
	public void blit(
			GuiGraphics guiGraphics,
			int x, int y,
			long displayTime, long activeTime
	) {
		guiGraphics.blit(guiTex, x, y, displayWidth, displayHeight, pixelX, pixelY, width, height, spriteWidth, spriteHeight);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
