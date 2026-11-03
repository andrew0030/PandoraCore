package com.github.andrew0030.pandora_core.modules.fastlib.mixin.render.sodium;

import com.github.andrew0030.pandora_core.modules.fastlib.render.CullBox;
import com.github.andrew0030.pandora_core.modules.fastlib.render.CullSphere;
import com.github.andrew0030.pandora_core.modules.fastlib.render.PaCoFrustum;
import me.jellysquid.mods.sodium.client.render.viewport.frustum.SimpleFrustum;
import org.joml.FrustumIntersection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = SimpleFrustum.class, remap = false)
public abstract class SimpleFrustumMixin implements PaCoFrustum {
	@Shadow @Final private FrustumIntersection frustum;
	@Shadow public abstract boolean testAab(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);

	@Override
	public boolean pandoraCore$isInFrustum(CullBox box) {
		return testAab(
				(float) box.minX, (float) box.minY, (float) box.minZ,
				(float) box.maxX, (float) box.maxY, (float) box.maxZ
		);
	}
	
	@Override
	public boolean pandoraCore$isInFrustum(CullSphere sphere) {
		FrustumIntersection intersection1 = frustum;
		
		return intersection1.testSphere((float) sphere.x, (float) sphere.y, (float) sphere.z, (float) sphere.diameter);
	}
	
	@Override
	public boolean pandoraCore$isInFrustum(double x, double y, double z) {
		return frustum.testPoint((float) x, (float) y, (float) z);
	}
	
	@Override
	public boolean pandoraCore$containsAllCorners(CullBox box) {
		return this.frustum.intersectAab(
				(float) box.minX, (float) box.minY, (float) box.minZ,
				(float) box.maxX, (float) box.maxY, (float) box.maxZ
		) == FrustumIntersection.INSIDE;
	}
}