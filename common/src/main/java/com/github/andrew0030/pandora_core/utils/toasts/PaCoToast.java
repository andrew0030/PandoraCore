package com.github.andrew0030.pandora_core.utils.toasts;

import com.github.andrew0030.pandora_core.utils.toasts.background.ToastBackground;
import com.github.andrew0030.pandora_core.utils.toasts.icon.PaCoIcon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

public class PaCoToast implements Toast {
	private final Component title;
	@Nullable
	private final Component message;
	
	private Toast.Visibility visibility = Visibility.SHOW;
	
	private int colorTitle = -1, colorMessage = -1;
	
	ToastBackground BG = ToastBackground.ADVANCEMENT;
	int numSlots = 1;
	
	public PaCoToast(Component title, @Nullable Component message) {
		this.title = title;
		this.message = message;
	}
	
	public PaCoToast setBG(ToastBackground BG) {
		this.BG = BG;
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
		return this;
	}
	
	long hideAt = 0;
	boolean hidden = false;
	long spawnTime;
	
	@Override
	public Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long l) {
		if (spawnTime == 0) spawnTime = System.currentTimeMillis();
		l = System.currentTimeMillis() - spawnTime;
		l -= 400;
		System.out.println(l);
		
		float extend = Mth.clamp(l / 200f, 0, 1);
		float retreat = 1;
		if (hidden) {
			retreat = 1 - Mth.clamp(((l - 200) - hideAt) / 200f, 0, 1);
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
					l
			);
			stk.popPose();
		}
		
		BG.blit(guiGraphics);
		
		if (icon != null) {
			icon.blit(
					guiGraphics, 6, 6, l
			);
		}
		
		if (this.message == null) {
			guiGraphics.drawString(toastComponent.getMinecraft().font, this.title, 30, 12, colorTitle | -16777216, false);
		} else {
			guiGraphics.drawString(toastComponent.getMinecraft().font, this.title, 30, 7, colorTitle | -16777216, false);
			guiGraphics.drawString(toastComponent.getMinecraft().font, this.message, 30, 18, colorMessage | -16777216, false);
		}
		
		if (ttl != -1 && l > ttl) {
			if (!hidden) {
				hidden = true;
				hideAt = l;
			}
			return Visibility.HIDE;
		}
		
		hidden = visibility == Visibility.HIDE;
		hideAt = l;
		return visibility;
	}
	
	@Override
	public int height() {
		return numSlots * 32;
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
