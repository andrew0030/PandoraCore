package com.github.andrew0030.pandora_core.modules.fastlib.mixin.render.vanilla;

import com.github.andrew0030.pandora_core.modules.fastlib.render.CullBox;
import com.github.andrew0030.pandora_core.modules.fastlib.render.CullSphere;
import com.github.andrew0030.pandora_core.modules.fastlib.render.PaCoFrustum;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.FrustumIntersection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Frustum.class)
public abstract class FrustumMixin implements PaCoFrustum {
	@Shadow
	protected abstract boolean cubeInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);
	
	@Shadow
	@Final
	private FrustumIntersection intersection;
	
	@Shadow
	private double camX;
	
	@Shadow
	private double camY;
	
	@Shadow
	private double camZ;
	
	@Override
	public boolean isInFrustum(CullBox box) {
		return cubeInFrustum(
				box.minX, box.minY, box.minZ,
				box.maxX, box.maxY, box.maxZ
		);
	}
	
	@Override
	public boolean isInFrustum(CullSphere sphere) {
		FrustumIntersection intersection1 = intersection;
		
		float x = (float) (sphere.x - camX);
		float y = (float) (sphere.y - camY);
		float z = (float) (sphere.z - camZ);
		return intersection1.testSphere(x, y, z, (float) sphere.diameter);
	}
	
	@Override
	public boolean isInFrustum(double x, double y, double z) {
		FrustumIntersection intersection1 = intersection;
		
		float ax = (float) (x - camX);
		float ay = (float) (y - camY);
		float az = (float) (z - camZ);
		return intersection1.testPoint(ax, ay, az);
	}
	
	@Override
	public boolean containsAllCorners(CullBox box) {
		float f = (float)(box.minX - this.camX);
		float f1 = (float)(box.minY - this.camY);
		float f2 = (float)(box.minZ - this.camZ);
		float f3 = (float)(box.maxX - this.camX);
		float f4 = (float)(box.maxY - this.camY);
		float f5 = (float)(box.maxZ - this.camZ);
		return this.intersection.intersectAab(f, f1, f2, f3, f4, f5) == FrustumIntersection.INSIDE;
	}
}
