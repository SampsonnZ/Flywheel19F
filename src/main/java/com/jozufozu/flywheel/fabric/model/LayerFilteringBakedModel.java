package com.jozufozu.flywheel.fabric.model;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class LayerFilteringBakedModel extends ForwardingBakedModel {
	private static final ThreadLocal<LayerFilteringBakedModel> THREAD_LOCAL = ThreadLocal.withInitial(LayerFilteringBakedModel::new);

	protected RenderType targetLayer;

	public static BakedModel wrap(BakedModel model, RenderType layer) {
		LayerFilteringBakedModel wrapper = THREAD_LOCAL.get();
		wrapper.wrapped = model;
		wrapper.targetLayer = layer;
		return wrapper;
	}

	protected LayerFilteringBakedModel() {
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		RenderType defaultLayer = ItemBlockRenderTypes.getChunkRenderType(state);
		if (super.isVanillaAdapter()) {
			if (defaultLayer == targetLayer) {
				super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
			}
		} else {
			context.pushTransform(quad -> {
				RenderType quadLayer = FabricModelUtil.getBlendMode(quad).blockRenderLayer;
				if (quadLayer == null) {
					quadLayer = defaultLayer;
				}
				return quadLayer == targetLayer;
			});
			super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
			context.popTransform();
		}
	}
}
