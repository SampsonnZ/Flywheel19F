
package com.jozufozu.flywheel.core.model;

import com.jozufozu.flywheel.util.Pair;
import com.mojang.blaze3d.vertex.BufferBuilder.RenderedBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.util.RandomSource;

import java.util.Random;

/**
 * An interface for objects that can "rendered" into a BufferBuilder.
 */
public interface Bufferable {

	default Pair<RenderedBuffer, Integer> build() {
		return ModelUtil.getRenderedBuffer(this);
	}

	void bufferInto(ModelBlockRenderer modelRenderer, VertexConsumer consumer, RandomSource random);
}
