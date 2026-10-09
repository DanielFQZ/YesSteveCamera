package com.github.exopandora.shouldersurfing.camera.assist;

import com.github.exopandora.shouldersurfing.camera.target.TargetService;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;

/** Shared intent for movement, body facing and duel framing; automatic camera motion is ignored. */
public final class LockMovementService
{
	private static final SprintSteering SPRINT = new SprintSteering();
	private static LivingEntity previousTarget;
	private LockMovementService() {}
	public static void reset() { SPRINT.reset(); previousTarget = null; }
	private static boolean available()
	{
		var mc = Minecraft.getInstance();
		var instance = ShoulderSurfingImpl.getInstance();
		var target = TargetService.target();
		return !CombatAssistService.steeringSuppressed() && TargetService.enabled() && target != null && target.isAlive() && !target.isRemoved()
				&& target.level() == mc.level && instance.isCameraDecoupled()
				&& !instance.isFreeLooking() && !instance.isAiming()
				&& !mc.player.isPassenger() && !mc.player.isSleeping() && !mc.player.isSwimming()
				&& !mc.player.isFallFlying() && !mc.player.getAbilities().flying;
	}
	private static void update(float forward, float left)
	{
		if (!available()) { reset(); return; }
		var target = TargetService.target();
		if (previousTarget != target) { SPRINT.reset(); previousTarget = target; }
		float viewAngle = Mth.wrapDegrees(ShoulderSurfingImpl.getInstance().getCamera().getYRot() - targetYaw());
		SPRINT.update(Minecraft.getInstance().player.isSprinting(), forward, left, viewAngle);
	}
	private static void updateKeys()
	{
		var options = Minecraft.getInstance().options;
		update((options.keyUp.isDown() ? 1 : 0) - (options.keyDown.isDown() ? 1 : 0),
				(options.keyLeft.isDown() ? 1 : 0) - (options.keyRight.isDown() ? 1 : 0));
	}
	public static boolean releasesFacing()
	{
		updateKeys();
		var player = Minecraft.getInstance().player;
		return player != null && player.isSprinting() && (!available() || SPRINT.released());
	}
	private static float targetYaw()
	{
		var player = Minecraft.getInstance().player;
		var delta = TargetService.target().position().subtract(player.position());
		return delta.horizontalDistanceSqr() < 1.0E-8 ? player.getYRot() : (float) (Mth.atan2(-delta.x, delta.z) * Mth.RAD_TO_DEG);
	}
	public static boolean apply(Input input)
	{
		if (CombatAssistService.preservesHeading()) return false;
		update(input.forwardImpulse, input.leftImpulse);
		if (!available() || SPRINT.released()) return false;
		var movement = LockedMovement.relative(input.leftImpulse, input.forwardImpulse, targetYaw(), Minecraft.getInstance().player.getYRot());
		input.leftImpulse = movement.x(); input.forwardImpulse = movement.y();
		return true;
	}
	/** Returns true only on the transition that consumes/replays accumulated mouse intent. */
	public static boolean mouse(double yaw, double pitch)
	{
		updateKeys();
		if (!available() || !SPRINT.mouse(yaw)) return false;
		var camera = ShoulderSurfingImpl.getInstance().getCamera();
		camera.setYRot(targetYaw() + (float) SPRINT.mouseTurn());
		camera.setXRot(Mth.clamp(camera.getXRot() + (float) pitch, -90, 90));
		return true;
	}
}
