package com.github.andrew0030.pandora_core.utils.toasts.background;

import net.minecraft.client.gui.GuiGraphics;

public class ResizableToastBackground extends AbstractToastBackground {
	ToastBackground bg;
	
	public static final ResizableToastBackground ADVANCEMENT = new ResizableToastBackground(
			ToastBackground.ADVANCEMENT
	);
	public static final ResizableToastBackground RECIPE = new ResizableToastBackground(
			ToastBackground.RECIPE
	);
	public static final ResizableToastBackground TUTORIAL = new ResizableToastBackground(
			ToastBackground.TUTORIAL
	);
	public static final ResizableToastBackground NOTICE = new ResizableToastBackground(
			ToastBackground.NOTICE
	);
	public static final ResizableToastBackground WARNING = new ResizableToastBackground(
			ToastBackground.WARNING
	);
	public static final ResizableToastBackground ERROR = new ResizableToastBackground(
			ToastBackground.ERROR
	);
	
	public ResizableToastBackground(ToastBackground bg) {
		this.bg = bg;
	}
	
	public void blit(GuiGraphics guiGraphics, int width, int height) {
		int left = bg.borderWidth;
		int top = bg.borderWidth;
		int right = width - bg.borderWidth;
		int bottom = height - bg.borderWidth;
		
		int texRight = bg.width - bg.borderWidth;
		int texBottom = bg.height - bg.borderWidth;
		
		// TL
		guiGraphics.blit(
				bg.guiTex,
				0, 0,
				bg.pixelX, bg.pixelY,
				bg.borderWidth, bg.borderWidth
		);
		// TM
		guiGraphics.blit(
				bg.guiTex,
				left, 0,
				right - bg.borderWidth, bg.borderWidth,
				bg.pixelX + left, bg.pixelY,
				texRight - bg.borderWidth, bg.borderWidth,
				256, 256
		);
		// TR
		guiGraphics.blit(
				bg.guiTex,
				right, 0,
				bg.pixelX + bg.width - bg.borderWidth, bg.pixelY,
				bg.borderWidth, bg.borderWidth
		);
		
		// ML
		guiGraphics.blit(
				bg.guiTex,
				0, top,
				bg.borderWidth, bottom - bg.borderWidth,
				bg.pixelX, bg.pixelY + top,
				bg.borderWidth, texBottom - bg.borderWidth,
				256, 256
		);
		// MM
		guiGraphics.blit(
				bg.guiTex,
				left, top,
				right - bg.borderWidth, bottom - bg.borderWidth,
				bg.pixelX + left, bg.pixelY + top,
				texRight - bg.borderWidth, texBottom - bg.borderWidth,
				256, 256
		);
		// MR
		guiGraphics.blit(
				bg.guiTex,
				right, top,
				bg.borderWidth, bottom - bg.borderWidth,
				bg.pixelX + bg.width - bg.borderWidth, bg.pixelY + top,
				bg.borderWidth, texBottom - bg.borderWidth,
				256, 256
		);
		
		// BL
		guiGraphics.blit(
				bg.guiTex,
				0, bottom,
				bg.pixelX, bg.pixelY + bg.height - bg.borderWidth,
				bg.borderWidth, bg.borderWidth
		);
		// BM
		guiGraphics.blit(
				bg.guiTex,
				left, bottom,
				right - bg.borderWidth, bg.borderWidth,
				bg.pixelX + left, bg.pixelY + bg.height - bg.borderWidth,
				texRight - bg.borderWidth, bg.borderWidth,
				256, 256
		);
		// BR
		guiGraphics.blit(
				bg.guiTex,
				right, bottom,
				bg.pixelX + bg.width - bg.borderWidth, bg.pixelY + bg.height - bg.borderWidth,
				bg.borderWidth, bg.borderWidth
		);
	}
	
	@Override
	public void blit(GuiGraphics guiGraphics) {
		bg.blit(guiGraphics);
	}
	
	@Override
	public boolean canBeResized() {
		return true;
	}
}
