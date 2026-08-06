package com.github.andrew0030.pandora_core.modules.instancer.state;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.world.phys.Vec3;

import java.util.Queue;

public class Dummy {
	private void updateRenderChunks(Object renderChunkStorage, Object renderInfoMap, Vec3 viewPos, Queue<LevelRenderer.RenderChunkInfo> renderQueue, boolean smartCull) {
		ChunkRenderDispatcher.RenderChunk renderChunk = null;
		ChunkRenderDispatcher.CompiledChunk compiledChunk = renderChunk.compiled.get();
		boolean used = compiledChunk.isEmpty(null);
		if (used || renderChunk.isDirty()) {
		}
	}
}
