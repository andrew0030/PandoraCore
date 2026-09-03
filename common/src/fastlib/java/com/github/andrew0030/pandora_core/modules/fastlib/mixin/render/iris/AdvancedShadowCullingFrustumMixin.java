package com.github.andrew0030.pandora_core.modules.fastlib.mixin.render.iris;

import com.github.andrew0030.pandora_core.modules.fastlib.render.CullBox;
import com.github.andrew0030.pandora_core.modules.fastlib.render.CullSphere;
import com.github.andrew0030.pandora_core.modules.fastlib.render.PaCoFrustum;
import net.irisshaders.iris.shadows.frustum.advanced.AdvancedShadowCullingFrustum;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AdvancedShadowCullingFrustum.class)
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
	public abstract boolean isVisible(AABB aabb);
	
	@Shadow
	protected abstract int isVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);
	
	@Override
	public boolean isInFrustum(CullBox box) {
		return isVisible(
				box.minX, box.minY, box.minZ,
				box.maxX, box.maxY, box.maxZ
		) != 0;
	}
	
	@Override
	public boolean isInFrustum(CullSphere sphere) {
		// TODO: ideally this gets implemented
		return true;
	}
	
	@Override
	public boolean isInFrustum(double x, double y, double z) {
		float f = (float) (x - this.x);
		float g = (float) (y - this.y);
		float h = (float) (z - this.z);
		return isInFrustum(f, g, h);
	}
	
	@Override
	public boolean containsAllCorners(CullBox box) {
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
