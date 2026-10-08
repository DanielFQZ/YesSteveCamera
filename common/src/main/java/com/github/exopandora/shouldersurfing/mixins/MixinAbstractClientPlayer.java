package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.camera.fov.MovementFovService;
import com.github.exopandora.shouldersurfing.camera.fov.MovementFovState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;

@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayer
{
	@Unique
	private final MovementFovState yesstevecamera$movementFov = new MovementFovState();

	/** Only replaces the speed read used by FOV; never changes the actual attribute. */
	@Redirect(method = "getFieldOfViewModifier", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/player/AbstractClientPlayer;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D"), require = 1)
	private double yesstevecamera$movementFovSpeed(AbstractClientPlayer player, Attribute attribute)
	{
		double speed = player.getAttributeValue(attribute);
		Minecraft mc = Minecraft.getInstance();
		if (player != mc.player || mc.getCameraEntity() != player || !player.isAlive())
		{
			this.yesstevecamera$movementFov.reset();
			return speed;
		}
		var config = MovementFovService.config();
		boolean inScope = !config.shoulderOnly() || Perspective.current() == Perspective.SHOULDER_SURFING;
		return this.yesstevecamera$movementFov.fovSpeed(speed, player.getAbilities().getWalkingSpeed(),
				inScope, player.isSprinting(), config, player.tickCount / 20.0);
	}

	@Redirect
	(
		method = "getFieldOfViewModifier",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		)
	)
	private boolean isFirstPerson(CameraType cameraType)
	{
		return cameraType.isFirstPerson() || Perspective.SHOULDER_SURFING == Perspective.current();
	}
}
