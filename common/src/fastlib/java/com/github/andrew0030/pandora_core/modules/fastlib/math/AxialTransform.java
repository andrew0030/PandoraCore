//package com.github.andrew0030.pandora_core.modules.fastlib.math;
//
//import org.joml.Matrix4f;
//
//public class AxialTransform {
//	float x, y, z;
//	float sx = 1, sy = 1, sz = 1;
//	byte hOrient, vOrient;
//
//	private static final byte AXIS_X = 0;
//	private static final byte AXIS_Y = 1;
//	private static final byte AXIS_Z = 2;
//
//	public AxialTransform() {
//	}
//
//	public AxialTransform identity() {
//		x = 0;
//		y = 0;
//		z = 0;
//		sx = 1;
//		sy = 1;
//		sz = 1;
//		hOrient = 0;
//		vOrient = 0;
//		return this;
//	}
//
//	public static float swizzle(float x, float y, float z, byte axis, byte xO, byte yO) {
//		switch (yO) {
//			case 0 -> {
//				// no-op
//			}
//			case 1 -> {
//				float xv = x;
//				x = y;
//				y = xv;
//			}
//			case 2 -> {
//				x = -x;
//				y = -y;
//			}
//			case 3 -> {
//				float xv = x;
//				x = -y;
//				y = -xv;
//			}
//		}
//
//		if (axis != AXIS_Y) {
//			switch (xO) {
//				case 0 -> {
//					// no-op
//					x = x;
//					z = -z;
//				}
//				case 1 -> {
//					float xv = z;
//					z = -x;
//					x = -xv;
//				}
//				case 2 -> {
//					x = -x;
//					z = z;
//				}
//				case 3 -> {
//					float xv = z;
//					z = x;
//					x = xv;
//				}
//			}
//		}
//
//		return switch (axis) {
//			case AXIS_X -> x;
//			case AXIS_Y -> y;
//			case AXIS_Z -> z;
//			default -> throw new RuntimeException("...");
//		};
//	}
//
//	public AxialTransform translate(float x, float y, float z) {
//		this.x = Math.fma(swizzle(x, y, z, AXIS_X, hOrient, vOrient), Math.abs(sx), this.x);
//		this.y = Math.fma(swizzle(x, y, z, AXIS_Y, hOrient, vOrient), Math.abs(sy), this.y);
//		this.z = Math.fma(swizzle(x, y, z, AXIS_Z, hOrient, vOrient), Math.abs(sz), this.z);
//
//		return this;
//	}
//
//	public AxialTransform scale(float x, float y, float z) {
//		this.sx = swizzle(x, y, z, AXIS_X, hOrient, vOrient) * sx;
//		this.sy = swizzle(x, y, z, AXIS_Y, hOrient, vOrient) * sy;
//		this.sz = swizzle(x, y, z, AXIS_Z, hOrient, vOrient) * sz;
//
//		return this;
//	}
//
//	public AxialTransform translateAbs(float x, float y, float z) {
//		this.x += x;
//		this.y += y;
//		this.z += z;
//
//		return this;
//	}
//
//	public AxialTransform scaleAbs(float x, float y, float z) {
//		this.sx = x;
//		this.sy = y;
//		this.sz = z;
//
//		return this;
//	}
//
//	public AxialTransform rotateX(int amount) {
//		if ((amount & 0x03) == 0) throw new RuntimeException("...");
//		hOrient = (byte) ((hOrient + amount) & 0x03);
////		if (hOrient >= 4) hOrient -= 4;
//		return this;
//	}
//
//	public AxialTransform rotateY(int amount) {
//		if ((amount & 0x03) == 0) throw new RuntimeException("...");
//		vOrient = (byte) ((vOrient + amount) & 0x03);
////		if (vOrient >= 4) vOrient -= 4;
//		return this;
//	}
//
//	public AxialTransform set(AxialTransform to) {
//		this.x = to.x;
//		this.y = to.y;
//		this.z = to.z;
//		this.sx = to.sx;
//		this.sy = to.sy;
//		this.sz = to.sz;
//		this.hOrient = to.hOrient;
//		this.vOrient = to.vOrient;
//		return this;
//	}
//
//	public void toMatrix(Matrix4f m4) {
//		if (vOrient != 0) throw new RuntimeException("TODO: vertical rotation");
//
//		m4.zero();
//		switch (hOrient) {
//			case 0 -> {
//				m4.set(3, 0, x);
//				m4.set(3, 1, y);
//				m4.set(3, 2, z);
//				m4.set(0, 0, -sx);
//				m4.set(1, 1, sy);
//				m4.set(2, 2, sz);
//			}
//			case 1 -> {
//				m4.set(3, 0, x);
//				m4.set(3, 1, y);
//				m4.set(3, 2, z);
//				m4.set(0, 2, -sx);
//				m4.set(1, 1, sy);
//				m4.set(2, 0, sz);
//			}
//			case 2 -> {
//				m4.set(3, 0, x);
//				m4.set(3, 1, y);
//				m4.set(3, 2, z);
//				m4.set(0, 0, sx);
//				m4.set(1, 1, sy);
//				m4.set(2, 2, -sz);
//			}
//			case 3 -> {
//				m4.set(3, 0, x);
//				m4.set(3, 1, y);
//				m4.set(3, 2, z);
//				m4.set(0, 2, sx);
//				m4.set(1, 1, sy);
//				m4.set(2, 0, -sz);
//			}
//		}
//		m4.set(3, 3, 1);
//
//		m4.determineProperties();
//	}
//}
