package com.github.andrew0030.pandora_core.utils.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class TheUnsafeHelper {
	private static Unsafe theUnsafe = null;
	
	static {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			theUnsafe = (Unsafe) f.get(null);
		} catch (Throwable ignored) {
			throw new RuntimeException("Could not get an instance of the unsafe");
		}
	}
	
	public static Unsafe getTheUnsafe() {
		return theUnsafe;
	}
}