package com.jozufozu.flywheel.mixin.instancemanage;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;

@Mixin(ChunkRenderDispatcher.RenderChunk.class)
public class RenderChunkMixin implements com.jozufozu.flywheel.util.RenderChunkExtension {


	@Override
	public ClientLevel flywheel$getLevel() {
		return ((ChunkRenderDispatcherAccessor) (Object)this).getLevel();
	}
}
