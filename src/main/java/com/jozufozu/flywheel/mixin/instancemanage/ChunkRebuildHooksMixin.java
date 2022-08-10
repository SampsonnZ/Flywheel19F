package com.jozufozu.flywheel.mixin.instancemanage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.backend.instancing.InstancedRenderDispatcher;
import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.jozufozu.flywheel.util.RenderChunkExtension;

import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;


@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask")
public abstract class ChunkRebuildHooksMixin {
	@Shadow @Nullable protected RenderChunkRegion region;
	@Unique
	private Level flywheel$level;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void setLevel(ChunkRenderDispatcher.RenderChunk renderChunk, double d, RenderChunkRegion renderChunkRegion, boolean bl, CallbackInfo ci) {
		flywheel$level = ((RenderChunkExtension) this).flywheel$getLevel();
	}

	@Redirect(method = "doTask", at = @At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"))
	private <E extends BlockEntity> boolean addAndFilterBEs(List<BlockEntity> self, Collection<? extends E> es) {
		if (!Backend.canUseInstancing(flywheel$level)) {
			return self.addAll(es);
		}

		boolean added = false;
		var instanced = new ArrayList<BlockEntity>();
		for (E be : es) {
			if (InstancedRenderRegistry.canInstance(be.getType())) {
				instanced.add(be);
			}

			if (!InstancedRenderRegistry.shouldSkipRender(be)) {
				self.add(be);
				added = true;
			}
		}
		InstancedRenderDispatcher.getBlockEntities(flywheel$level).queueAddAll(instanced);
		return added;
	}

	@Redirect(method = "doTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk;updateGlobalBlockEntities(Ljava/util/Collection;)V"))
	private void addAndFilterBEs(ChunkRenderDispatcher.RenderChunk self, Collection<BlockEntity> bes) {
		if (!Backend.canUseInstancing(flywheel$level)) {
			((RenderChunkAccessor) self).flywheel$updateGlobalBlockEntities(bes);
			return;
		}

		var global = new ArrayList<BlockEntity>();
		var instanced = new ArrayList<BlockEntity>();
		for (BlockEntity be : bes) {
			if (InstancedRenderRegistry.canInstance(be.getType())) {
				instanced.add(be);
			}

			if (!InstancedRenderRegistry.shouldSkipRender(be)) {
				global.add(be);
			}
		}

		InstancedRenderDispatcher.getBlockEntities(flywheel$level).queueAddAll(instanced);
		((RenderChunkAccessor) self).flywheel$updateGlobalBlockEntities(global);
	}
}
