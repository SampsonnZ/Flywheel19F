package com.jozufozu.flywheel.core.model;

import com.jozufozu.flywheel.fabric.model.FabricModelUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class ShadeSeparatingVertexConsumer implements VertexConsumer {
	protected VertexConsumer shadedConsumer;
	protected VertexConsumer unshadedConsumer;
	protected VertexConsumer activeConsumer;
	private final ShadeSeparatingBakedModel modelWrapper = new ShadeSeparatingBakedModel();
	public void prepare(VertexConsumer shadedConsumer, VertexConsumer unshadedConsumer) {
		this.shadedConsumer = shadedConsumer;
		this.unshadedConsumer = unshadedConsumer;
	}

	public void clear() {
		shadedConsumer = null;
		unshadedConsumer = null;
		activeConsumer = null;
	}
	public BakedModel wrapModel(BakedModel model) {
		modelWrapper.setWrapped(model);
		return modelWrapper;
	}
	@Override
	public void putBulkData(PoseStack.Pose poseEntry, BakedQuad quad, float[] colorMuls, float red, float green, float blue, int[] combinedLights, int combinedOverlay, boolean mulColor) {
		if (quad.isShade()) {
			shadedConsumer.putBulkData(poseEntry, quad, colorMuls, red, green, blue, combinedLights, combinedOverlay, mulColor);
		} else {
			unshadedConsumer.putBulkData(poseEntry, quad, colorMuls, red, green, blue, combinedLights, combinedOverlay, mulColor);
		}
	}

	/*@Override
	public void putBulkData(PoseStack.Pose matrixEntry, BakedQuad bakedQuad, float[] baseBrightness, float red, float green, float blue, float alpha, int[] lightmapCoords, int overlayCoords, boolean readExistingColor) {
		if (bakedQuad.isShade()) {
			shadedConsumer.putBulkData(matrixEntry, bakedQuad, baseBrightness, red, green, blue, alpha, lightmapCoords, overlayCoords, readExistingColor);
		} else {
			unshadedConsumer.putBulkData(matrixEntry, bakedQuad, baseBrightness, red, green, blue, alpha, lightmapCoords, overlayCoords, readExistingColor);
		}
	}*/
	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		activeConsumer.vertex(x, y, z);
		return this;
	}

	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		activeConsumer.color(red, green, blue, alpha);
		return this;
	}

	@Override
	public VertexConsumer uv(float u, float v) {
		activeConsumer.uv(u, v);
		return this;
	}

	@Override
	public VertexConsumer overlayCoords(int u, int v) {
		activeConsumer.overlayCoords(u, v);
		return this;
	}

	@Override
	public VertexConsumer uv2(int u, int v) {
		activeConsumer.uv2(u, v);
		return this;
	}

	@Override
	public VertexConsumer normal(float x, float y, float z) {
		activeConsumer.normal(x, y, z);
		return this;
	}

	@Override
	public void endVertex() {
		activeConsumer.endVertex();
	}

	@Override
	public void defaultColor(int red, int green, int blue, int alpha) {
		activeConsumer.defaultColor(red, green, blue, alpha);
	}

	protected void setActiveConsumer(boolean shaded) {
		activeConsumer = shaded ? shadedConsumer : unshadedConsumer;
	}
	@Override
	public void unsetDefaultColor() {
		activeConsumer.unsetDefaultColor();
	}
	private class ShadeSeparatingBakedModel extends ForwardingBakedModel {
		private final RenderContext.QuadTransform quadTransform = quad -> {
			ShadeSeparatingVertexConsumer.this.setActiveConsumer(FabricModelUtil.isShaded(quad));
			return true;
		};

		private void setWrapped(BakedModel model) {
			wrapped = model;
		}

		@Override
		public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
			context.pushTransform(quadTransform);
			super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
			context.popTransform();
		}
	}
}
