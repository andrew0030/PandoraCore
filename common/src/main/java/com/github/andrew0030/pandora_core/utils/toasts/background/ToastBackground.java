package com.github.andrew0030.pandora_core.utils.toasts.background;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ToastBackground extends AbstractToastBackground {
	public static final ResourceLocation TOASTS = new ResourceLocation("textures/gui/toasts.png");
	public static final ResourceLocation TOASTS_PACO = new ResourceLocation("pandora_core:textures/gui/toast/toast_backgrounds.png");
	
	public static final ToastBackground ADVANCEMENT = new ToastBackground(
			"advancement", TOASTS,
			0, 0,
			160, 32,
			4
	);
	public static final ToastBackground RECIPE = new ToastBackground(
			"recipe", TOASTS,
			0, 32,
			160, 32,
			4
	);
	public static final ToastBackground SYSTEM = new ToastBackground(
			"system", TOASTS,
			0, 64,
			160, 32
	);
	public static final ToastBackground TUTORIAL = new ToastBackground(
			"tutorial", TOASTS,
			0, 96,
			160, 32
	);
	
	public static final ToastBackground NOTICE = new ToastBackground(
			"notice", TOASTS_PACO,
			0, 0,
			160, 32,
			4
	);
	public static final ToastBackground WARNING = new ToastBackground(
			"warning", TOASTS_PACO,
			0, 32,
			160, 32,
			4
	);
	public static final ToastBackground ERROR = new ToastBackground(
			"error", TOASTS_PACO,
			0, 64,
			160, 32
	);
	
	String name;
	ResourceLocation guiTex;
	int pixelX, pixelY;
	int width, height;
	int borderWidth = 3;
	
	public ToastBackground(String name, ResourceLocation guiTex, int pixelX, int pixelY, int width, int height, int borderWidth) {
		this.name = name;
		this.guiTex = guiTex;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.width = width;
		this.height = height;
		this.borderWidth = borderWidth;
	}
	
	public ToastBackground(String name, ResourceLocation guiTex, int pixelX, int pixelY, int width, int height) {
		this.name = name;
		this.guiTex = guiTex;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.width = width;
		this.height = height;
	}
	
	public void blit(GuiGraphics guiGraphics) {
		guiGraphics.blit(guiTex, 0, 0, pixelX, pixelY, width, height);
	}
	
	@Override
	public boolean canBeResized() {
		return false;
	}
}
