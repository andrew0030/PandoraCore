package com.github.andrew0030.pandora_core.modules.fastlib.render;

public class CullSphere {
	public double x, y, z;
	public double diameter;
	
	public CullSphere(double x, double y, double z, double diameter) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.diameter = diameter;
	}
	
	public CullSphere set(double x, double y, double z, double diameter) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.diameter = diameter;
		return this;
	}
	
	public CullSphere setPos(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public CullSphere setDiameter(double diameter) {
		this.diameter = diameter;
		return this;
	}
	
	public CullSphere contain(CullBox box) {
		diameter = box.calculateDiameter();
		x = box.centerX();
		y = box.centerY();
		z = box.centerZ();
		return this;
	}
}
