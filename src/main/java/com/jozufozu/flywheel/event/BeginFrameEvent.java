package com.jozufozu.flywheel.event;

import com.jozufozu.flywheel.fabric.event.EventContext;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;

public class BeginFrameEvent extends EventContext {
	private final ClientLevel world;
	private final Camera camera;
	private final Frustum frustum;

	public BeginFrameEvent(ClientLevel world, Camera camera, Frustum frustum) {
		this.world = world;
		this.camera = camera;
		this.frustum = frustum;
	}

	public ClientLevel getWorld() {
		return world;
	}

	public Camera getCamera() {
		return camera;
	}

	public Frustum getFrustum() {
		return frustum;
	}

	public Vec3 getCameraPos() {
		return camera.getPosition();
	}
}
