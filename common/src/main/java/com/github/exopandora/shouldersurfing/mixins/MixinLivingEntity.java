package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.camera.assist.CombatAssistService;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Vanilla derives body yaw from travel direction, including the return leg of root motion. */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity
{
	@Inject(method = "tickHeadTurn", at = @At("HEAD"), cancellable = true)
	private void yesstevecamera$preserveAttackHeading(float bodyYaw, float distance,
			CallbackInfoReturnable<Float> callback)
	{
		if ((Object) this == Minecraft.getInstance().player && CombatAssistService.preservesHeading())
		{
			CombatAssistService.applyAttackHeading();
			callback.setReturnValue(distance);
		}
	}
}
