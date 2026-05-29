package com.github.andrew0030.pandora_core.utils.unsafe;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class FieldAccessor {
	public final Field field;
	public final Object base;
	public final long offset;
	
	public FieldAccessor(
			Unsafe theUnsafe,
			Class<?> clazz,
			String fieldName
	) {
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
		base = theUnsafe.staticFieldBase(field);
		offset = theUnsafe.staticFieldOffset(field);
	}
	
	public <T> T get(Unsafe unsafe, Class<T> clazz) {
		return (T) unsafe.getObject(base, offset);
	}
	
	public <T> T get(Unsafe unsafe, Object base, Class<T> clazz) {
		return (T) unsafe.getObject(base, offset);
	}
}
