package com.github.andrew0030.pandora_core.modules.fastlib.mixin.render.sodium;

import com.github.andrew0030.pandora_core.modules.fastlib.render.CullBox;
import com.github.andrew0030.pandora_core.modules.fastlib.render.CullSphere;
import com.github.andrew0030.pandora_core.modules.fastlib.render.PaCoFrustum;
import me.jellysquid.mods.sodium.client.render.viewport.frustum.SimpleFrustum;
import org.joml.FrustumIntersection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimpleFrustum.class)
public abstract class SimpleFrustumMixin implements PaCoFrustum {
	@Shadow
	public abstract boolean testAab(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);
	
	@Shadow
	@Final
	private FrustumIntersection frustum;
	
	@Override
	public boolean isInFrustum(CullBox box) {
		return testAab(
				(float) box.minX, (float) box.minY, (float) box.minZ,
				(float) box.maxX, (float) box.maxY, (float) box.maxZ
		);
	}
	
	@Override
	public boolean isInFrustum(CullSphere sphere) {
		FrustumIntersection intersection1 = frustum;
		
		return intersection1.testSphere((float) sphere.x, (float) sphere.y, (float) sphere.z, (float) sphere.diameter);
	}
	
	@Override
	public boolean isInFrustum(double x, double y, double z) {
		return frustum.testPoint((float) x, (float) y, (float) z);
	}
	
	@Override
	public boolean containsAllCorners(CullBox box) {
		return this.frustum.intersectAab(
				(float) box.minX, (float) box.minY, (float) box.minZ,
				(float) box.maxX, (float) box.maxY, (float) box.maxZ
		) == FrustumIntersection.INSIDE;
	}
}
