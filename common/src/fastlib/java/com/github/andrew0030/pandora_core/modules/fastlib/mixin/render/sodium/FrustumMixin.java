//package com.github.andrew0030.pandora_core.modules.fastlib.mixin.render.sodium;
//
//import com.github.andrew0030.pandora_core.modules.fastlib.render.CullBox;
//import com.github.andrew0030.pandora_core.modules.fastlib.render.CullSphere;
//import com.github.andrew0030.pandora_core.modules.fastlib.render.PaCoFrustum;
//import me.jellysquid.mods.sodium.client.render.viewport.frustum.Frustum;
//import org.joml.FrustumIntersection;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//
//@Mixin(Frustum.class)
//public abstract class FrustumMixin implements PaCoFrustum {
//	@Shadow
//	public abstract boolean testAab(float v, float v1, float v2, float v3, float v4, float v5);
//
//	@Override
//	public boolean isInFrustum(CullBox box) {
//		return testAab(
//				(float) box.minX, (float) box.minY, (float) box.minZ,
//				(float) box.maxX, (float) box.maxY, (float) box.maxZ
//		);
//	}
//
//	// we don't have a cheap method of checking this, so just pretend it's always true
//	// properly checking this would defeat the point
//	@Override
//	public boolean isInFrustum(CullSphere sphere) {
//		return true;
//	}
//
//	// we don't have a cheap method of checking this, so just pretend it's always true
//	// properly checking this would defeat the point
//	@Override
//	public boolean isInFrustum(double x, double y, double z) {
//		return true;
//	}
//
//	@Override
//	public boolean containsAllCorners(CullBox box) {
//		return false;
//	}
//}
