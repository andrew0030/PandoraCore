package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.platform.services.IRegistryHelper;
import net.minecraft.core.Registry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import java.util.Map;
import java.util.function.Supplier;

public class ForgeRegistryHelper implements IRegistryHelper {

    @Override
    public <T> void register(Registry<T> registry, String modId, Map<String, Supplier<T>> entries) {
        DeferredRegister<T> deferred = DeferredRegister.create(registry.key(), modId);
        entries.forEach(deferred::register);
        deferred.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}