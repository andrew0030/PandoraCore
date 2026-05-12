package com.github.andrew0030.pandora_core.mixin.container;

import com.github.andrew0030.pandora_core.mixin_interfaces.container.IPaCoCheckInventory;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;

/**
 * The reason this {@code mixin} is empty, is because we are simply adding a superinterface.<br/>
 * And since {@link Container} is an {@code interface}, we simply define the default
 * behaviour inside {@link IPaCoCheckInventory}, resulting in an empty body.
 */
@Mixin(Container.class)
public interface ContainerMixin extends IPaCoCheckInventory {}