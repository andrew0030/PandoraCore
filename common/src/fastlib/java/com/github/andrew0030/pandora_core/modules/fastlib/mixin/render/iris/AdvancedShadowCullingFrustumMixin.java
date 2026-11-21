package com.github.andrew0030.pandora_core.modules.fastlib.mixin.render.iris;

import com.github.andrew0030.pandora_core.modules.fastlib.render.CullBox;
import com.github.andrew0030.pandora_core.modules.fastlib.render.CullSphere;
import com.github.andrew0030.pandora_core.modules.fastlib.render.PaCoFrustum;
import net.irisshaders.iris.shadows.frustum.BoxCuller;
import net.irisshaders.iris.shadows.frustum.advanced.AdvancedShadowCullingFrustum;
import net.minecraft.world.phys.AABB;
import org.joml.Math;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AdvancedShadowCullingFrustum.class, remap = false)
public abstract class AdvancedShadowCullingFrustumMixin implements PaCoFrustum {
	@Shadow
	protected abstract int checkCornerVisibility(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);
	
	@Shadow
	public double z;
	@Shadow
	public double y;
	@Shadow
	public double x;
	
	@Shadow
	public abstract boolean checkCornerVisibilityBool(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);
	
	@Shadow
	protected abstract int isVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);
	
	// NOTE: The iris "AdvancedShadowCullingFrustum" extends minecraft's "Frustum", which is where this method originates from
	@Shadow(remap = true)
	public abstract boolean isVisible(AABB aabb);
	
	@Shadow
	@Final
	protected BoxCuller boxCuller;
	
	@Shadow
	@Final
	private Vector4f[] planes;
	
	@Shadow
	private int planeCount;
	
	@Override
	public boolean pandoraCore$isInFrustum(CullBox box) {
		return isVisible(
				box.minX, box.minY, box.minZ,
				box.maxX, box.maxY, box.maxZ
		) != 0;
	}
	
	@Override
	public boolean pandoraCore$isInFrustum(CullSphere sphere) {
		if (((PaCoFrustum) boxCuller).pandoraCore$isInFrustum(sphere)) {
			float f = (float) (sphere.x - this.x);
			float g = (float) (sphere.y - this.y);
			float h = (float) (sphere.z - this.z);

//			float dists = 0;
//
//			for(int i = 0; i < this.planeCount; ++i) {
//				Vector4f plane = planes[i];
//
//				float d = org.joml.Math.fma(plane.x(), f, Math.fma(plane.y(), g, plane.z() * h));
//				if (d < -plane.w()) {
//					d += plane.w();
//					dists += d * d;
//				}
//			}
//			dists = Math.sqrt(dists);
//
//			return dists < sphere.diameter / 2.0f;

			float radius = (float) (sphere.diameter * 0.5f);
			for(int i = 0; i < this.planeCount; ++i) {
				Vector4f plane = planes[i];

				float d = org.joml.Math.fma(plane.x(), f, Math.fma(plane.y(), g, plane.z() * h));
				d += radius;
				if (d < -plane.w()) {
					return false;
				}
			}
			
			return true;
		}
		return false;
	}
	
	@Override
	public boolean pandoraCore$isInFrustum(double x, double y, double z) {
		if (((PaCoFrustum) boxCuller).pandoraCore$isInFrustum(x, y, z)) {
			float f = (float) (x - this.x);
			float g = (float) (y - this.y);
			float h = (float) (z - this.z);
			
			for (Vector4f plane : planes) {
				if (org.joml.Math.fma(plane.x(), f, Math.fma(plane.y(), g, plane.z() * h)) < -plane.w()) {
					return false;
				}
			}
			
			return true;
		}
		return false;
//		return pandoraCore$isInFrustum(f, g, h);
	}
	
	@Override
	public boolean pandoraCore$containsAllCorners(CullBox box) {
		float f = (float) (box.minX - this.x);
		float g = (float) (box.minY - this.y);
		float h = (float) (box.minZ - this.z);
		float i = (float) (box.maxX - this.x);
		float j = (float) (box.maxY - this.y);
		float k = (float) (box.maxZ - this.z);
		return checkCornerVisibilityBool(
				f, g, h, i, j, k
		);
	}
}