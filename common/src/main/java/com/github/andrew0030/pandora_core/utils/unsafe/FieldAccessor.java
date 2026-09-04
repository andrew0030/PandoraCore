package com.github.andrew0030.pandora_core.utils.unsafe;

import com.github.andrew0030.pandora_core.client.render.optifine.TriangularSVB;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldAccessor {
	public final Field field;
	public final Object base;
	public final long offset;
	
	public FieldAccessor(
			Unsafe theUnsafe,
			Class<?> clazz,
			String fieldName
	) {
		Field fref;
		try {
			fref = clazz.getDeclaredField(fieldName);
		} catch (Throwable err) {
			try {
				fref = clazz.getField(fieldName);
			} catch (Throwable err1) {
				throw new RuntimeException(err);
			}
		}
		field = fref;
		if (Modifier.isStatic(field.getModifiers())) {
			base = theUnsafe.staticFieldBase(field);
			offset = theUnsafe.staticFieldOffset(field);
		} else {
			base = 0;
			offset = theUnsafe.objectFieldOffset(field);
		}
	}
	
	public <T> T get(Unsafe unsafe, Class<T> clazz) {
		return (T) unsafe.getObject(base, offset);
	}
	
	public <T> T get(Unsafe unsafe, Object base, Class<T> clazz) {
		return (T) unsafe.getObject(base, offset);
	}
	
	public <T> T getPrimitive(Unsafe unsafe, Class<T> clazz) {
		return getPrimitive(unsafe, clazz);
	}
	
	public <T> T getPrimitive(Unsafe unsafe, Object base, Class<T> clazz) {
		if (clazz.equals(boolean.class)) {
			return (T) (Boolean) unsafe.getBoolean(base, offset);
		} else if (clazz.equals(byte.class)) {
			return (T) (Byte) unsafe.getByte(base, offset);
		} else if (clazz.equals(short.class)) {
			return (T) (Short) unsafe.getShort(base, offset);
		} else if (clazz.equals(int.class)) {
			return (T) (Integer) unsafe.getInt(base, offset);
		} else if (clazz.equals(long.class)) {
			return (T) (Long) unsafe.getLong(base, offset);
		} else if (clazz.equals(float.class)) {
			return (T) (Float) unsafe.getFloat(base, offset);
		} else if (clazz.equals(double.class)) {
			return (T) (Double) unsafe.getDouble(base, offset);
		}
		return (T) unsafe.getObject(base, offset);
	}
	
	public void set(Unsafe unsafe, Object obj) {
		unsafe.putObject(base, offset, obj);
	}
	
	public void set(Unsafe unsafe, Object base, Object obj) {
		unsafe.putObject(base, offset, obj);
	}
}
