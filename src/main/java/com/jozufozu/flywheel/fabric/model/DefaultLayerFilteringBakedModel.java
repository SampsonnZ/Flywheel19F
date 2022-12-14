package com.jozufozu.flywheel.fabric.model;

import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class DefaultLayerFilteringBakedModel extends ForwardingBakedModel {
	private static final ThreadLocal<DefaultLayerFilteringBakedModel> THREAD_LOCAL = ThreadLocal.withInitial(DefaultLayerFilteringBakedModel::new);

	public static BakedModel wrap(BakedModel model) {
		if (!((FabricBakedModel) model).isVanillaAdapter()) {
			DefaultLayerFilteringBakedModel wrapper = THREAD_LOCAL.get();
			wrapper.wrapped = model;
			return wrapper;
		}
		return model;
	}

	protected DefaultLayerFilteringBakedModel() {
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		context.pushTransform(DefaultLayerFilteringBakedModel::hasDefaultBlendMode);
		super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
		context.popTransform();
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
		context.pushTransform(DefaultLayerFilteringBakedModel::hasDefaultBlendMode);
		super.emitItemQuads(stack, randomSupplier, context);
		context.popTransform();
	}

	public static boolean hasDefaultBlendMode(QuadView quad) {
		return FabricModelUtil.getBlendMode(quad) == BlendMode.DEFAULT;
	}
}
