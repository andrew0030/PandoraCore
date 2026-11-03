package com.github.andrew0030.pandora_core.modules.fastlib.render;

import org.joml.Vector3d;

public interface PaCoFrustum {
	boolean pandoraCore$isInFrustum(CullBox box);
	boolean pandoraCore$isInFrustum(CullSphere sphere);
	boolean pandoraCore$isInFrustum(double x, double y, double z);
	
	default boolean pandoraCore$isInFrustum(Vector3d point) {
		return pandoraCore$isInFrustum(point.x, point.y, point.z);
	}
	
	default boolean pandoraCore$containsAllCorners(CullBox box) {
		return
				pandoraCore$isInFrustum(box.minX, box.minY, box.minZ) &&
				pandoraCore$isInFrustum(box.maxX, box.minY, box.minZ) &&
				pandoraCore$isInFrustum(box.minX, box.maxY, box.minZ) &&
				pandoraCore$isInFrustum(box.minX, box.minY, box.maxZ) &&
				pandoraCore$isInFrustum(box.maxX, box.minY, box.maxZ) &&
				pandoraCore$isInFrustum(box.maxX, box.maxY, box.maxZ) &&
				pandoraCore$isInFrustum(box.maxX, box.maxY, box.minZ) &&
				pandoraCore$isInFrustum(box.minX, box.maxY, box.maxZ);
	}
}