package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.github.exopandora.shouldersurfing.mixinducks.CameraDuck;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinCamera implements CameraDuck
{
	@Shadow
	private float xRot;
	
	@Shadow
	private float yRot;
	
	@Unique
	private float zRot;
	
	@Shadow
	protected abstract void setPosition(Vec3 position);

	@Shadow
	protected abstract void move(double x, double y, double z);
	
	@Shadow
	protected abstract void setRotation(float yRot, float xRot);
	
	@Inject
	(
		method = "setup",
		at = @At("HEAD")
	)
	private void setupRotations(CallbackInfo ci)
	{
		this.shouldersurfing$setZRot(0.0F);
	}
	
	@Inject
	(
		method = "setup",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;setPosition(DDD)V",
			shift = Shift.AFTER,
			ordinal = 0
		)
	)
	private void setupRotations(BlockGetter level, Entity cameraEntity, boolean detached, boolean isMirrored, float partialTick, CallbackInfo ci)
	{
		if(Perspective.SHOULDER_SURFING == Perspective.current() && !(cameraEntity instanceof LivingEntity livingEntity && livingEntity.isSleeping()))
		{
			ShoulderSurfingCamera camera = ShoulderSurfingImpl.getInstance().getCamera();
			Vec2f rotations = camera.calcRotations(cameraEntity, partialTick);
			this.setRotation(rotations.y(), rotations.x());
		}
	}
	
	@Redirect
	(
		method = "setup",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;move(DDD)V",
			ordinal = 0
		)
	)
	private void setupPosition(Camera cameraIn, double x, double y, double z, BlockGetter level, Entity cameraEntity, boolean detached, boolean isMirrored, float partialTick)
	{
		if(Perspective.SHOULDER_SURFING == Perspective.current() && !(cameraEntity instanceof LivingEntity livingEntity && livingEntity.isSleeping()))
		{
			ShoulderSurfingCamera camera = ShoulderSurfingImpl.getInstance().getCamera();
			Vec3 cameraOffset = camera.calcOffset(cameraIn, level, partialTick, cameraEntity);
			this.move(-cameraOffset.z(), cameraOffset.y(), cameraOffset.x());
			com.github.exopandora.shouldersurfing.camera.target.TargetService.captureCamera(cameraIn.getPosition(), this.xRot, this.yRot);
			Vec2f sway = camera.calcSway(camera, cameraEntity, partialTick);
			this.zRot = sway.y();
			this.setRotation(this.yRot, this.xRot + sway.x());
		}
		else
		{
			this.move(x, y, z);
		}
	}
	
	@Override
	public float shouldersurfing$getZRot()
	{
		return this.zRot;
	}
	
	@Override
	public void shouldersurfing$setZRot(float zRot)
	{
		this.zRot = zRot;
	}

	@Override
	public void shouldersurfing$constrainPosition(float partialTick)
	{
		Camera camera = (Camera) (Object) this;
		if (camera.getEntity() != null && !camera.getEntity().isSpectator()
				&& Perspective.current() == Perspective.SHOULDER_SURFING)
			this.setPosition(com.github.exopandora.shouldersurfing.camera.duel.CameraCollision.constrain(
					camera.getEntity(), camera.getEntity().getEyePosition(partialTick), camera.getPosition()));
	}

	@Override
	public void shouldersurfing$applyShake(double x, double y, double z, float xRot, float yRot, float zRot)
	{
		Camera camera = (Camera) (Object) this;
		Vec3 start = camera.getPosition();
		// Same lateral/up/distance convention as ShoulderSurfing's camera offset.
		this.move(-z, y, x);
		Vec3 end = camera.getPosition();
		var minecraft = net.minecraft.client.Minecraft.getInstance();
		if (minecraft.level != null && camera.getEntity() != null && start.distanceToSqr(end) > 1.0E-10)
		{
			var hit = minecraft.level.clip(new net.minecraft.world.level.ClipContext(start, end,
					net.minecraft.world.level.ClipContext.Block.VISUAL,
					net.minecraft.world.level.ClipContext.Fluid.NONE, camera.getEntity()));
			if (hit.getType() != net.minecraft.world.phys.HitResult.Type.MISS)
			{
				double distance = Math.max(0, start.distanceTo(hit.getLocation()) - 0.1);
				this.setPosition(start.add(end.subtract(start).normalize().scale(distance)));
			}
		}
		this.setRotation(this.yRot + yRot, this.xRot + xRot);
		this.zRot += zRot;
	}
}
