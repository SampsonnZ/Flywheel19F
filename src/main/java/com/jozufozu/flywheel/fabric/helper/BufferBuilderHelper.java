package com.jozufozu.flywheel.fabric.helper;

import com.jozufozu.flywheel.fabric.mixin.BufferBuilderAccessor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;

import java.nio.ByteBuffer;

public final class BufferBuilderHelper {
	public static void fixByteOrder(BufferBuilder self, ByteBuffer buffer) {
		buffer.order(((BufferBuilderAccessor) self).getBuffer().order());
	}

	public static VertexFormat getVertexFormat(BufferBuilder self) {
		return ((BufferBuilderAccessor) self).getFormat();
	}
}
