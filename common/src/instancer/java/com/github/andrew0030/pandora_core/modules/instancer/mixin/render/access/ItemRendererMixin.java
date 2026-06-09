package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.access;

import com.github.andrew0030.pandora_core.modules.instancer.itf.ItemRendererAccessor;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemRendererMixin implements ItemRendererAccessor {
	@Shadow
	protected abstract int getRenderAmount(ItemStack stack);
	
	@Shadow
	@Final
	private RandomSource random;
	
	@Override
	public int pandoraCore$getRenderAmount(ItemStack stack) {
		return getRenderAmount(stack);
	}
	
	@Override
	public RandomSource pandoraCore$getRandom() {
		return random;
	}
}
