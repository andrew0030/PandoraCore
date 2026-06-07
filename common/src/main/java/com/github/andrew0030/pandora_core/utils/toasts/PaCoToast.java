package com.github.andrew0030.pandora_core.utils.toasts;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.github.andrew0030.pandora_core.utils.toasts.background.AbstractToastBackground;
import com.github.andrew0030.pandora_core.utils.toasts.background.ToastBackground;
import com.github.andrew0030.pandora_core.utils.toasts.icon.PaCoIcon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

import javax.annotation.Nullable;

public class PaCoToast implements Toast {
	private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Toasts");
	
	private final Component title;
	@Nullable
	private final Component message;
	
	private Toast.Visibility visibility = Visibility.SHOW;
	
	private int colorTitle = -1, colorMessage = -1;
	
	AbstractToastBackground BG = ToastBackground.ADVANCEMENT;
	int numSlots = 1;
	
	public PaCoToast(Component title, @Nullable Component message) {
		this.title = title;
		this.message = message;
	}
	
	boolean resizableBG = false;
	
	public PaCoToast setBG(AbstractToastBackground BG) {
		this.BG = BG;
		resizableBG = BG.canBeResized();
		return this;
	}
	
	int width = 160;
	
	public PaCoToast setWidth(int width) {
		this.width = width;
		if (!resizableBG) {
			LOGGER.warn("setWidth called on a toast that has a non-resizable BG. This is not recommended.");
		}
		return this;
	}
	
	public PaCoToast setColorTitle(int colorTitle) {
		this.colorTitle = colorTitle;
		return this;
	}
	
	public PaCoToast setColorMessage(int colorMessage) {
		this.colorMessage = colorMessage;
		return this;
	}
	
	int ttl = 3600;
	PaCoIcon icon;
	PaCoIcon modIcon;
	
	public PaCoToast setTtl(int ttl) {
		this.ttl = ttl;
		return this;
	}
	
	public PaCoToast setIcon(PaCoIcon icon) {
		this.icon = icon;
		return this;
	}
	
	public PaCoToast setModIcon(PaCoIcon modIcon) {
		this.modIcon = modIcon;
		return this;
	}
	
	public PaCoToast setNumSlots(int numSlots) {
		this.numSlots = numSlots;
		if (!resizableBG) {
			LOGGER.warn("setNumSlots called on a toast that has a non-resizable BG. This is not recommended.");
		}
		return this;
	}
	
	long hideAt = 0;
	boolean hidden = false;
	long spawnTime;
	
	@Override
	public Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long activeTime) {
		if (spawnTime == 0) spawnTime = System.currentTimeMillis();
		long slideInAnim = 600; // takes 600 ms for the toast to slide in
		long logoAnim = 200; // we want the logo to slide in/out for 200 ms
		long displayTime = System.currentTimeMillis() - spawnTime;
		displayTime -= (slideInAnim - logoAnim);
		
		float extend = Mth.clamp(displayTime / (float) logoAnim, 0, 1);
		float retreat = 1;
		if (hidden) {
			retreat = 1 - Mth.clamp(((displayTime - logoAnim) - hideAt) / (float) logoAnim, 0, 1);
		}
		float offset = extend * retreat;
		
		if (modIcon != null) {
			int left = 0 - modIcon.getWidth();
			double center = (slotCount() * 32) / 2d - modIcon.getHeight() / 2d;
//			double center = (slotCount() * 32) / 2d;
			PoseStack stk = guiGraphics.pose();
			stk.pushPose();
			stk.translate(offset * left, 0, 0);
			modIcon.blit(
					guiGraphics,
					0, (int) center,
					displayTime, activeTime
			);
			stk.popPose();
		}
		
		BG.blit(guiGraphics, width(), height());
		
		if (icon != null) {
			icon.blit(
					guiGraphics,
					6, 6,
					displayTime, activeTime
			);
		}
		
		if (this.message == null) {
			guiGraphics.drawString(toastComponent.getMinecraft().font, this.title, 30, 12, colorTitle | -16777216, false);
		} else {
			guiGraphics.drawString(toastComponent.getMinecraft().font, this.title, 30, 7, colorTitle | -16777216, false);
			guiGraphics.drawString(toastComponent.getMinecraft().font, this.message, 30, 18, colorMessage | -16777216, false);
		}
		
		if (ttl != -1 && activeTime > ttl) {
			if (!hidden) {
				hidden = true;
				hideAt = displayTime;
			}
			return Visibility.HIDE;
		}
		
		hidden = visibility == Visibility.HIDE;
		hideAt = displayTime;
		return visibility;
	}
	
	@Override
	public int height() {
		return numSlots * 32;
	}
	
	@Override
	public int width() {
		return width;
	}
	
	@Override
	public int slotCount() {
		return numSlots;
	}
	
	public PaCoToast setVisibility(Visibility visibility) {
		this.visibility = visibility;
		return this;
	}
}
