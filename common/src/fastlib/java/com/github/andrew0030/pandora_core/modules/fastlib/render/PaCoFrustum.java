package com.github.andrew0030.pandora_core.modules.fastlib.render;

import org.joml.Vector3d;

public interface PaCoFrustum {
	boolean isInFrustum(CullBox box);
	boolean isInFrustum(CullSphere sphere);
	boolean isInFrustum(double x, double y, double z);
	
	default boolean isInFrustum(Vector3d point) {
		return isInFrustum(point.x, point.y, point.z);
	}
	
	default boolean containsAllCorners(CullBox box) {
		return
				isInFrustum(box.minX, box.minY, box.minZ) &&
				isInFrustum(box.maxX, box.minY, box.minZ) &&
				isInFrustum(box.minX, box.maxY, box.minZ) &&
				isInFrustum(box.minX, box.minY, box.maxZ) &&
				isInFrustum(box.maxX, box.minY, box.maxZ) &&
				isInFrustum(box.maxX, box.maxY, box.maxZ) &&
				isInFrustum(box.maxX, box.maxY, box.minZ) &&
				isInFrustum(box.minX, box.maxY, box.maxZ);
	}
}
