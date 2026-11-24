package com.github.andrew0030.pandora_core.modules.fastlib.mixin.render.iris;

import com.github.andrew0030.pandora_core.modules.fastlib.render.CullBox;
import com.github.andrew0030.pandora_core.modules.fastlib.render.CullSphere;
import com.github.andrew0030.pandora_core.modules.fastlib.render.PaCoFrustum;
import net.irisshaders.iris.shadows.frustum.BoxCuller;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = BoxCuller.class, remap = false)
public abstract class BoxCullerMixin implements PaCoFrustum {
	@Shadow public abstract boolean isCulled(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);
	@Shadow private double minAllowedX;
	@Shadow private double maxAllowedX;
	@Shadow private double minAllowedY;
	@Shadow private double minAllowedZ;
	@Shadow private double maxAllowedZ;
	@Shadow private double maxAllowedY;
	
	@Override
	public boolean pandoraCore$isInFrustum(CullBox box) {
		return !isCulled(
				box.minX,
				box.minY,
				box.minZ,
				box.maxX,
				box.maxY,
				box.maxZ
		);
	}
	
	@Override
	public boolean pandoraCore$isInFrustum(CullSphere sphere) {
		if (pandoraCore$isInFrustum(sphere.x, sphere.y, sphere.z)) {
			return true;
		}
		
		double distX = Math.max(Math.max(this.minAllowedX - sphere.x, sphere.x - this.maxAllowedX), (double)0.0F);
		double distY = Math.max(Math.max(this.minAllowedY - sphere.y, sphere.y - this.maxAllowedY), (double)0.0F);
		double distZ = Math.max(Math.max(this.minAllowedZ - sphere.z, sphere.z - this.maxAllowedZ), (double)0.0F);
		return Mth.lengthSquared(distX, distY, distZ) < sphere.diameter * 0.5;
	}
	
	@Override
	public boolean pandoraCore$isInFrustum(double x, double y, double z) {
		return x >= minAllowedX && x <= maxAllowedX &&
				y >= minAllowedY && y <= maxAllowedY &&
				z >= minAllowedZ && z <= maxAllowedZ;
	}
	
	@Override
	public boolean pandoraCore$containsAllCorners(CullBox box) {
		return
				box.minX > this.minAllowedX &&
				box.maxX < this.maxAllowedX &&
				box.minY > this.minAllowedY &&
				box.maxY < this.maxAllowedY &&
				box.minZ > this.minAllowedZ &&
				box.maxZ < this.maxAllowedZ;
	}
}