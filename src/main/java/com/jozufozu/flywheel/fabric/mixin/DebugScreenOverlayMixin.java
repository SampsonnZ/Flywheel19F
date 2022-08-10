package com.jozufozu.flywheel.fabric.mixin;

import com.jozufozu.flywheel.event.ForgeEvents;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin extends GuiComponent {
	@Inject(method = "getSystemInformation", at = @At("RETURN"))
	private void modifyRightText(CallbackInfoReturnable<List<String>> cir) {
		ForgeEvents.addToDebugScreen(cir.getReturnValue());
	}
}
