package com.github.andrew0030.pandora_core.modules.fastlib.render;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class CullBox {
	public double minX, minY, minZ;
	public double maxX, maxY, maxZ;
	
	public CullBox(
			double minX, double minY, double minZ,
			double maxX, double maxY, double maxZ
	) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	public CullBox(AABB box) {
		this.minX = box.minX;
		this.minY = box.minY;
		this.minZ = box.minZ;
		this.maxX = box.maxX;
		this.maxY = box.maxY;
		this.maxZ = box.maxZ;
	}
	
	public CullBox offset(double x, double y, double z) {
		minX += x;
		minY += y;
		minZ += z;
		maxX += x;
		maxY += y;
		maxZ += z;
		return this;
	}
	
	public CullBox set(
			double minX, double minY, double minZ,
			double maxX, double maxY, double maxZ
	) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		return this;
	}

	public CullBox set(AABB box) {
		this.minX = box.minX;
		this.minY = box.minY;
		this.minZ = box.minZ;
		this.maxX = box.maxX;
		this.maxY = box.maxY;
		this.maxZ = box.maxZ;
		return this;
	}

	public CullBox set(BlockPos pos) {
		this.minX = pos.getX();
		this.minY = pos.getY();
		this.minZ = pos.getZ();
		this.maxX = minX + 1;
		this.maxY = minY + 1;
		this.maxZ = minZ + 1;
		return this;
	}
	
	public double width() {
		return maxX - minX;
	}
	
	public double height() {
		return maxY - minY;
	}
	
	public double depth() {
		return maxZ - minZ;
	}
	
	public double calculateDiameter() {
		return Math.sqrt(width() * width() + height() * height() + depth() * depth());
	}
	
	public double centerX() {
		return (maxX + minX) * 0.5;
	}
	
	public double centerY() {
		return (maxY + minY) * 0.5;
	}
	
	public double centerZ() {
		return (maxZ + minZ) * 0.5;
	}
}
