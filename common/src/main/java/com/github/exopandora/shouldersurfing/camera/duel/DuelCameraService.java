package com.github.exopandora.shouldersurfing.camera.duel;

import com.github.exopandora.shouldersurfing.camera.target.TargetService;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;

/** Render-time framing of interpolated entities, before skill tracks and shake. */
public final class DuelCameraService
{
	private static DuelConfig config = DuelConfig.DEFAULT;
	private static boolean sessionEnabled = true, controlling, limited, hasFrame;
	private static double previousTime = Double.NaN, dt, weight, mouseYaw, mousePitch;
	private static Vec3 worldOffset = Vec3.ZERO;
	private static double collisionDistance = Double.NaN;
	private DuelCameraService() {}
	public static void configure(DuelConfig value) { config = value; sessionEnabled = true; reset(); }
	public static void setEnabled(boolean enabled) { sessionEnabled = enabled; }
	public static void reset()
	{
		previousTime = Double.NaN; weight = 0; mouseYaw = 0; mousePitch = 0;
		controlling = false; limited = false; hasFrame = false; worldOffset = Vec3.ZERO; collisionDistance = Double.NaN;
	}
	public static String status()
	{
		return !config.enabled() || !sessionEnabled ? "disabled" : controlling ? (limited ? "framing (distance capped)" : "framing") : "standby";
	}
	private static boolean eligible()
	{
		var mc = Minecraft.getInstance(); var player = mc.player;
		var instance = ShoulderSurfingImpl.getInstance();
		return config.enabled() && sessionEnabled && TargetService.enabled() && TargetService.target() != null
				&& instance.isCameraDecoupled() && !instance.isAiming() && !instance.isFreeLooking()
				&& player != null && !com.github.exopandora.shouldersurfing.camera.assist.LockMovementService.releasesFacing()
				&& !player.isPassenger() && !player.isSleeping()
				&& !player.isFallFlying() && !player.isSwimming() && !player.getAbilities().flying;
	}
	public static boolean mouse(double yaw, double pitch)
	{
		if (!eligible()) return false;
		mouseYaw = Mth.clamp(mouseYaw + yaw, -config.mouseYawLimit(), config.mouseYawLimit());
		mousePitch = Mth.clamp(mousePitch + pitch, -config.mousePitchLimit(), config.mousePitchLimit());
		return true;
	}
	public static Vec2f prepare(ShoulderSurfingCamera shoulder, Entity entity, float partial, Vec2f base)
	{
		var mc = Minecraft.getInstance();
		if (entity != mc.player || mc.level == null || !ShoulderSurfingImpl.getInstance().isShoulderSurfing()) { reset(); return base; }
		double time = mc.level.getGameTime() + partial;
		dt = Double.isNaN(previousTime) || mc.isPaused() ? 0 : Mth.clamp((time - previousTime) / 20, 0, 0.25);
		previousTime = Double.isNaN(previousTime) ? time : Math.max(previousTime, time);
		controlling = eligible();
		double blend = DuelFraming.blend(dt, config.smoothSeconds());
		weight += ((controlling ? 1 : 0) - weight) * blend;
		if (!controlling)
		{
			mouseYaw = 0; mousePitch = 0;
			if (weight < 0.001) { weight = 0; hasFrame = false; collisionDistance = Double.NaN; }
			return base;
		}
		var target = TargetService.target();
		AABB playerBox = entity.getBoundingBox().move(entity.getPosition(partial).subtract(entity.position()));
		AABB targetBox = target.getBoundingBox().move(target.getPosition(partial).subtract(target.position()));
		Vec3 direction = targetBox.getCenter().subtract(playerBox.getCenter());
		double desiredYaw = direction.horizontalDistanceSqr() < 1.0E-8 ? base.y()
				: Math.toDegrees(Math.atan2(-direction.x, direction.z)) - config.sideAngle();
		double desiredPitch = Mth.clamp(12 - Math.toDegrees(Math.atan2(direction.y, Math.max(0.01, direction.horizontalDistance()))) * 0.5, -45, 45);
		float yaw = (float) DuelFraming.turn(base.y(), desiredYaw + mouseYaw, blend);
		float pitch = (float) Mth.lerp(blend, base.x(), desiredPitch + mousePitch);
		shoulder.setYRot(yaw); shoulder.setXRot(pitch);
		double aspect = (double) mc.getWindow().getWidth() / Math.max(1, mc.getWindow().getHeight());
		// Conservative FOV leaves room for vanilla sprint/zoom modifiers and frame edges.
		var frame = DuelFraming.fit(entity.getEyePosition(partial), playerBox, targetBox, yaw, pitch,
				Math.min(60, mc.options.fov().get()), aspect, config);
		worldOffset = !hasFrame ? frame.offset() : worldOffset.lerp(frame.offset(), blend);
		hasFrame = true;
		limited = frame.limited();
		mouseYaw *= Math.exp(-dt / 1.0); mousePitch *= Math.exp(-dt / 1.0);
		return new Vec2f(pitch, yaw);
	}
	public static boolean blending() { return weight > 0.001; }
	public static Vec3 offset(Camera camera, Vec3 normal)
	{
		Vec3 local = new Vec3(worldOffset.dot(new Vec3(camera.getLeftVector())),
				worldOffset.dot(new Vec3(camera.getUpVector())), -worldOffset.dot(new Vec3(camera.getLookVector())));
		return normal.lerp(local, weight);
	}
	public static double collisionDistance(double distance)
	{
		if (Double.isNaN(collisionDistance) || distance < collisionDistance) collisionDistance = distance;
		else collisionDistance += (distance - collisionDistance) * DuelFraming.blend(dt, config.smoothSeconds());
		return collisionDistance;
	}
}
