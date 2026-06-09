package com.github.andrew0030.pandora_core.modules.instancer.itf;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

public interface ItemRendererAccessor {
	int pandoraCore$getRenderAmount(ItemStack stack);
	
	RandomSource pandoraCore$getRandom();
}
